#!/usr/bin/env bash
# TODO
# - Be sure to install the npm dependencies
#    Setup must be run before this script
#
while getopts ":m :n" opt; do
    case ${opt} in
    m)
        MOD="module"
        ;;
    n)
        MOD="prod"
        ;;
    \?)
        echo "Invalid option: $OPTARG" 1>&2
        exit 1
        ;;
    *)
        echo "Invalid option: $OPTARG requires an argument" 1>&2
        exit 1
        ;;
    esac
done
echo "MOD: $MOD"
MOD=$MOD sbt -mem 4096 server/Docker/publish
