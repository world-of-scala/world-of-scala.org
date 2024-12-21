#!/usr/bin/env bash
# TODO
# - Be sure to install the npm dependencies
#    Setup must be run before this script
#
MOD="ESModule"
while getopts ":m :n" opt; do
    case ${opt} in
    m)
        MOD="ESModule"
        ;;
    n)
        MOD="CommonJs"
        ;;
    \?)
        echo "Invalid option: $OPTARG" 1>&2
        exit 1
        ;;
    esac
done
echo "Js Module: $MOD"
MOD=$MOD sbt server/Docker/publishLocal
