#!/usr/local/bin/env bash
set -e

export PLATFORM=$(echo $(uname) | tr '[:upper:]' '[:lower:]')
export WORKSPACE=$GITHUB_WORKSPACE
mkdir -p "$WORKSPACE"

export DRIVER_NAME=hibernate-orm
