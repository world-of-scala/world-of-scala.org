#!/usr/bin/env bash

# Import the project environment variables
. ./scripts/env.sh

# This is a hack to force the build-env.sh file to be generated
# And setup the project
if [ ! -e $BUILD_ENV_FILE ]; then
    sbt projects
    . scripts/setup.sh
fi

MOD=$BUILD_MOD sbt -mem 4096 "${BUILD_CLEAN}server/run"
