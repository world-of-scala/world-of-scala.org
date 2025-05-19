#!/usr/bin/env bash
set -e
# Import the project environment variables
. ./scripts/env.sh

./scripts/setup.sh

MOD=Docker sbt "server/Docker/publishLocal"
