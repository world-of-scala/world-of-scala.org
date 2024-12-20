#!/usr/bin/env bash
# TODO
# - Be sure to install the npm dependencies
#    Setup must be run before this script
#
MOD=module sbt -mem 4096 "clean; server/run"
