#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."
ENV_FILE="$DOCKER_DIR/.env.docker"

# 내부 포트 추출
INT_PORT=$(grep '^KAFKA_INTERNAL_PORT=' "$ENV_FILE" | cut -d '=' -f2)

echo "Starting Kafka Cluster..."

# 컨테이너 기동
docker compose \
  --env-file "$ENV_FILE" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  up -d

echo "Kafka started."

# 컨테이너 상태 확인
docker compose \
  --env-file "$ENV_FILE" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps

# 동적으로 Kafka 노드 서비스 이름 가져오기
KAFKA_SERVICES=$(docker compose --env-file "$ENV_FILE" -f "$DOCKER_DIR/docker-compose.yml" ps --services | grep '^kafka-[0-9]' || true)

echo "Waiting for Kafka Cluster nodes to become ready..."

# 발견된 모든 노드에 대해 순차적으로 준비 상태 확인
for SERVICE in $KAFKA_SERVICES; do
  echo "Checking $SERVICE..."
  until docker exec "$SERVICE" /opt/kafka/bin/kafka-metadata-quorum.sh --bootstrap-server localhost:"$INT_PORT" describe --status >/dev/null 2>&1; do
    sleep 1
  done
done

echo "All Kafka Cluster nodes are ready."

# 전체 클러스터 상태 확인 (첫 번째 노드 기준)
FIRST_NODE=$(echo "$KAFKA_SERVICES" | head -n 1)
if [ -n "$FIRST_NODE" ]; then
  echo "Cluster Quorum status (via $FIRST_NODE):"
  docker exec "$FIRST_NODE" /opt/kafka/bin/kafka-metadata-quorum.sh --bootstrap-server localhost:"$INT_PORT" describe --status
fi
