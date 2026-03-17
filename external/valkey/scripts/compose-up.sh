#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCKER_DIR="$SCRIPT_DIR/.."

echo "Starting Valkey containers..."

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  up -d

echo "Valkey started."

# 컨테이너 상태 확인

docker compose \
  --env-file "$DOCKER_DIR/.env.docker" \
  -f "$DOCKER_DIR/docker-compose.yml" \
  ps

echo "Waiting for Valkey to become ready..."

# primary 준비 확인

until docker exec valkey-primary valkey-cli ping >/dev/null 2>&1; do
  sleep 1
done

# replica 준비 확인

until docker exec valkey-replica valkey-cli ping >/dev/null 2>&1; do
  sleep 1
done

echo "Valkey containers are ready."

# replication 상태 확인

echo "Replication status:"
docker exec valkey-primary valkey-cli INFO replication | grep -E "role|connected_slaves"
