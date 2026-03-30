#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

echo "Starting MySQL container..."

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  up -d

echo "MySQL started."

# 컨테이너 상태 확인
docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps

# MySQL 준비 확인
echo "Waiting for MySQL to become ready..."
until docker exec mysql mysqladmin ping -uroot -p${MYSQL_ROOT_PASSWORD} >/dev/null 2>&1; do
  sleep 1
done

echo "MySQL container is ready."
