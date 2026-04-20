# Kuke Board

고트래픽 환경을 가정한 게시판 시스템을 MSA 구조로 설계 및 구현하는 프로젝트,  
[스프링부트로 직접 만들면서 배우는 대규모 시스템 설계 - 게시판](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8%EB%A1%9C-%EB%8C%80%EA%B7%9C%EB%AA%A8-%EC%8B%9C%EC%8A%A4%ED%85%9C%EC%84%A4%EA%B3%84-%EA%B2%8C%EC%8B%9C%ED%8C%90?cid=334365) 코틀린으로 따라하기

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

### 1. Write / Read 분리 (CQRS-lite)

- 쓰기: 정합성 보장 (MySQL)
- 읽기: 성능 최적화 (Redis 기반 Query Model)

### 2. Outbox Pattern + Kafka

- 트랜잭션과 이벤트 발행 간의 불일치 문제 해결
- **at-least-once delivery 보장**

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
- JWT 검증 → `X-User-Id` 헤더 주입
- 읽기/쓰기 API 라우팅 분리

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
