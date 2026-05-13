# Kuke Board

고트래픽 환경을 가정한 게시판 시스템을 MSA 구조로 설계 및 구현하는 프로젝트,  
[스프링부트로 직접 만들면서 배우는 대규모 시스템 설계 - 게시판](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8%EB%A1%9C-%EB%8C%80%EA%B7%9C%EB%AA%A8-%EC%8B%9C%EC%8A%A4%ED%85%9C%EC%84%A4%EA%B3%84-%EA%B2%8C%EC%8B%9C%ED%8C%90?cid=334365)
코틀린으로 따라하기

---

## Architecture Overview

- **MSA + 멀티모듈 구조**

    - `service:*` → 도메인 서비스
    - `common:*` → 공통 라이브러리

- **API Gateway 중심 진입 구조**

    - JWT 검증 후 내부 서비스로 라우팅
    - `X-User-Id` 헤더 전달 (인증 컨텍스트 전파)

- **이벤트 기반 데이터 흐름**

    - Outbox + Kafka → 비동기 전파
    - 읽기 모델(`article-read`, `hot-article`)은 이벤트 기반으로 갱신

- **데이터 저장소 역할 분리**

    - MySQL → 정합성 중심 원본 데이터
    - Redis → 캐시 / 카운터 / 랭킹
    - Kafka → 이벤트 스트림

---

## Core Design Principles

### 1. Write / Read 분리 (CQRS)

- 쓰기(Command): `article` 서비스가 MySQL에 정합성 있는 원본 데이터 관리
- 읽기(Query): `article-read` 서비스가 Redis 기반 비정규화 읽기 모델로 응답
- 두 서비스는 Kafka 이벤트로 느슨하게 결합
    - Event Sourcing 없이 서비스 단위로 Command/Query 책임을 분리한 CQRS 구현

### 2. Outbox Pattern + Kafka

트랜잭션 커밋과 이벤트 발행 간 불일치(dual-write 문제)를 해결하기 위해 Outbox 패턴을 적용했다.

**Outbox 테이블 구조**

| 컬럼           | 설명                                 |
|--------------|------------------------------------|
| `id`         | TSID (hypersistence-utils `@Tsid`) |
| `event_type` | 이벤트 종류 (Kafka 토픽과 1:1 매핑)          |
| `payload`    | JSON 직렬화된 이벤트 본문 (TEXT)            |
| `shard_key`  | 샤드 분산용 키                           |
| `created_at` | 생성 시각                              |

인덱스 `(shard_key, created_at)`: 샤드별 미전송 이벤트 폴링 쿼리 최적화

**이벤트 발행 흐름 (이중 발행 전략)**

```
도메인 서비스
  └─ OutboxEventPublisher.publish()  →  Spring ApplicationEvent 발행

MessageRelay (TransactionalEventListener)
  ├─ BEFORE_COMMIT : Outbox row 저장 (비즈니스 트랜잭션과 동일한 커밋 단위)
  └─ AFTER_COMMIT  : Kafka 즉시 전송 시도 (비동기) → 성공 시 Outbox row 삭제
```

커밋 직후 즉시 전송에 실패하더라도 Outbox row가 남아 있으므로 이벤트가 유실되지 않는다.

**미전송 이벤트 재시도 (폴링 스케줄러)**

- 10초마다 실행, `created_at < now - 10s` 조건으로 미전송 row 조회
- 샤드 단위로 분할 처리, 배치 크기 100건
- 전송 성공 시 해당 Outbox row 삭제

**다중 인스턴스 샤드 조정 (MessageRelayCoordinator)**

- 각 인스턴스가 Redis Sorted Set에 3초마다 ping (score = epoch ms)
- 9초(3회) 이상 무응답 인스턴스는 자동 제거
- 살아있는 인스턴스 목록 기준으로 4개 샤드를 균등 분배
- 인스턴스 수 변동 시 다음 폴링 주기에 자동 재할당

**at-least-once delivery 보장**

"BEFORE_COMMIT 저장 → AFTER_COMMIT 즉시 전송 → 폴링 재시도"의 3단계 구조로 이벤트 유실을 방지한다.
단, 중복 전송 가능성이 있으므로 Consumer 측 멱등성 처리가 전제된다.

### 3. Redis 적극 활용

- 조회수 카운터
- 중복 조회 방지 (TTL 기반 락)
- 인기글 랭킹 (Sorted Set)
- 읽기 모델 캐시

### 4. Gateway 인증 오프로딩

- 인증/인가 로직을 Gateway로 집중
- 내부 서비스는 비즈니스 로직에 집중

---

## Services

### Gateway (8080)

- Spring Cloud Gateway (WebFlux)
- 라우팅 정책:
    - 조회성 API(GET)는 read/공개 서비스로 라우팅
    - 변경성 API(POST/PUT/PATCH/DELETE)는 JWT 필터 적용
- `JwtAuthenticationFilter`가 Bearer 토큰을 검증하고 `X-User-Id`를 downstream 요청 헤더에 추가

### User (8087)

- 회원가입 / 로그인
- BCrypt 기반 비밀번호 해시
- JWT 발급 (userId 포함)

### Article (8081)

- 게시글 CRUD
- 권한 검증 (작성자 기준)
- 이벤트 발행:
    - `ARTICLE_CREATED`
    - `ARTICLE_UPDATED`
    - `ARTICLE_DELETED`

### Comment (8082)

- 댓글 / 대댓글 트리 구조
- `CommentPath` 기반 계층 표현 (문자열 경로)
- 삭제 전략:
    - 자식 없음 → 물리 삭제
    - 자식 있음 → tombstone

### Like (8083)

- Toggle 기반 좋아요
- soft delete (tombstone/restore)
- 이벤트:
    - `ARTICLE_LIKED`
    - `ARTICLE_UNLIKED`

### View (8084)

- Redis 기반 조회수 카운팅
- 중복 방지:
    - `view:article:{id}:user:{id}` 락 + TTL
- 100건 단위 MySQL 백업 + 이벤트 발행

### Article-Read (8086)

- 읽기 전용 Query Model
- Redis 캐시 기반 응답
- 캐시 미스 시:
    - 내부 서비스 호출 → 재구성 → 캐싱
- Kafka 이벤트로 비동기 동기화

### Hot-Article (8085)

- 실시간 인기글 집계
- 점수 계산:

  ```
  score = (like * 3) + (view * 1) + (comment * 2)
  ```

- Redis Sorted Set 기반 랭킹 유지
- 당일 게시글만 집계

---

## Data Flow

### 게시글 생성

1. Client → Gateway → Article
2. MySQL 커밋 + Outbox 저장
3. Kafka 이벤트 발행
4. Read Model / Hot Article 갱신

### 댓글 / 좋아요 / 조회수

1. 각 서비스에서 상태 변경
2. Outbox 이벤트 발행
3. Read Model 비동기 반영
4. Hot Article 점수 갱신

---

## Common Modules

### common:event

- `EventType`이 이벤트 페이로드 타입과 Kafka 토픽을 1:1로 매핑한다.
- `Event`는 TSID 기반 `eventId`를 생성하고 JSON 직렬화/역직렬화를 담당한다.

### common:outbox

- 도메인 서비스는 트랜잭션 내에서 `OutboxEventPublisher.publish()`를 호출한다.
- `MessageRelay`는
    1. `BEFORE_COMMIT` 단계에서 outbox row를 저장하고,
    2. `AFTER_COMMIT` 단계에서 Kafka publish를 시도,
    3. 실패/유실 보완을 위해 스케줄러가 미전송 이벤트를 shard 단위로 재시도한다.
- 즉, DB 커밋과 이벤트 발행 간 간극을 줄여 "최소 1회(at-least-once)" 전달 특성을 확보하려는 구현이다.

### common:jpa / common:serialization / common:pagination

- `jpa`: 감사/기본 엔티티 설정 공유.
- `serialization`: Jackson 기반 공통 직렬화 유틸.
- `pagination`: 페이지네이션/커서 응답 DTO와 카운트 제한 계산 유틸.
