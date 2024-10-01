#!/bin/bash -e
#
# Build Hibernate
#
set -o pipefail
THIS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
HIBERNATE_ROOT=$(cd "${THIS_DIR}/../../" && pwd)

cd "$HIBERNATE_ROOT"
rm -f lib/*.jar
mvn clean install --batch-mode --show-version -DtestGroups=UNIT
