#!/usr/bin/env bash
set -e

#
# Build Hibernate
#
set -o pipefail
THIS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export WORKSPACE=${WORKSPACE:=/tmp}
"$THIS_DIR/container/build_component.sh"
