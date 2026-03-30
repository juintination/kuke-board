#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

# .env.docker 파일 읽어서 환경 변수로 export
ENV_FILE="$DOCKER_DIR/.env.docker"
export $(grep -v '^#' "$ENV_FILE" | xargs)

docker compose \
  --env-file "$ENV_FILE" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  up -d

echo "MySQL started."

# 컨테이너 상태 확인
docker compose \
  --env-file "$ENV_FILE" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps

# MySQL 준비 확인
echo "Waiting for MySQL to become ready..."
until docker exec mysql mysqladmin ping -uroot -p${MYSQL_ROOT_PASSWORD} >/dev/null 2>&1; do
  sleep 1
done

echo "MySQL container is ready."
