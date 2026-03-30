#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

echo "Stopping MySQL container..."

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  down

echo "MySQL stopped."

# 컨테이너 상태 확인
RUNNING=$(docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps -q)

if [ -z "$RUNNING" ]; then
  echo "MySQL container successfully stopped and removed."
else
  echo "Some MySQL containers are still running:"
  docker compose \
    --env-file "$DOCKER_DIR/.env.docker" \
    -f "$DOCKER_DIR/docker-compose.yml" \
    ps
  exit 1
fi
