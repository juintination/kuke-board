#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

echo "Stopping Kafka containers..."

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  down

echo "Kafka stopped."

# 컨테이너 상태 확인
RUNNING=$(docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps -q)

if [ -z "$RUNNING" ]; then
  echo "Kafka containers successfully stopped and removed."
else
  echo "Some Kafka containers are still running:"
  docker compose \
    --env-file "$DOCKER_DIR/.env.docker" \
    -f "$DOCKER_DIR/docker-compose.yml" \
    ps
  exit 1
fi
