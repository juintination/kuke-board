#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

echo "Stopping Valkey containers..."

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  down

echo "Valkey stopped."

# 컨테이너 상태 확인

RUNNING=$(docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps -q)

if [ -z "$RUNNING" ]; then
  echo "Valkey containers successfully stopped and removed."
else
  echo "Some Valkey containers are still running:"

  docker compose \
    --env-file "$DOCKER_DIR/.env.docker" \
    -f "$DOCKER_DIR/docker-compose.yml" \
    ps

  exit 1
fi
