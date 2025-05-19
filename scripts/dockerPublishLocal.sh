#!/usr/bin/env bash
set -e
# Import the project environment variables
. ./scripts/env.sh

. ./scripts/setup-noninteractive.sh

MOD=$BUILD_MOD sbt "${BUILD_CLEAN}server/Docker/publishLocal"
