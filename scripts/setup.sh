#!/usr/bin/env bash
set -e
#
# This script is used to setup the project
# - Install npm dependencies
# - Generate Scala.js bindings
#
. ./scripts/env.sh

if [ ! -e $BUILD_ENV_FILE ]; then
    echo "Generating $BUILD_ENV_FILE"
    echo This file will store the Scala version.
    echo

    sbt projects # Will generate the BUILD_ENV_FILE file
fi
. $BUILD_ENV_FILE

rm -f $MAIN_JS_FILE

# Define get_mtime function based on OS
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    get_mtime() { stat -f %m "$1"; }
else
    # Linux and others
    get_mtime() { stat -c %Y "$1"; }
fi

filename_lock=node_modules/.package-lock.json

function npmInstall() {
    if [ ! -f "$filename_lock" ]; then
        echo "First time setup: Installing npm dependencies..."
        npm i
    else
        filename=package.json
        age=$(get_mtime "$filename")
        age_lock=$(get_mtime "$filename_lock")
        if [ $age_lock -lt $age ]; then
            echo "Updating npm dependencies..."
            npm i
        fi
    fi
}

pushd() {
    command pushd "$@" >/dev/null
}

popd() {
    command popd "$@" >/dev/null
}

pushd modules/client
npmInstall
popd
