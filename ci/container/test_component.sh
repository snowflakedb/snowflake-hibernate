#!/bin/bash -e
#
# Test Hibernate for Linux/MAC
#
set -o pipefail
THIS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export WORKSPACE=${WORKSPACE:-/mnt/workspace}
export SOURCE_ROOT=${SOURCE_ROOT:-/mnt/host}
MVNW_EXE=$SOURCE_ROOT/mvnw

echo "[INFO] Hibernate tests"

if [[ -f "$WORKSPACE/parameters.json" ]]; then
    echo "[INFO] Found parameter file in $WORKSPACE"
    PARAMETER_FILE=$WORKSPACE/parameters.json
else
    echo "[INFO] Use the default test parameters.json"
    PARAMETER_FILE=$SOURCE_ROOT/src/test/resources/parameters.json
fi
# shellcheck disable=SC2046
eval $(jq -r '.testconnection | to_entries | map("export \(.key)=\(.value|tostring)")|.[]' "$PARAMETER_FILE")

if [[ -n "$GITHUB_SHA" ]]; then
    # Github Action
    export TARGET_SCHEMA_NAME=${RUNNER_TRACKING_ID//-/_}_${GITHUB_SHA}

    function finish() {
        pushd "$SOURCE_ROOT"/ci/container >& /dev/null
            echo "[INFO] Drop schema $TARGET_SCHEMA_NAME"
            python3 drop_schema.py
        popd >& /dev/null
    }
    trap finish EXIT

    pushd "$SOURCE_ROOT/ci/container" >& /dev/null
        echo "[INFO] Create schema $TARGET_SCHEMA_NAME"
        if python3 create_schema.py; then
            export SNOWFLAKE_TEST_SCHEMA=$TARGET_SCHEMA_NAME
        else
            echo "[WARN] SNOWFLAKE_TEST_SCHEMA: $SNOWFLAKE_TEST_SCHEMA"
        fi
    popd >& /dev/null
fi

# we change password, create SSM_KNOWN_FILE
source "$THIS_DIR/../log_analyze_setup.sh"
if [[ "${ENABLE_CLIENT_LOG_ANALYZE}" == "true" ]]; then
    echo "[INFO] Log Analyze is enabled."

    setup_log_env

    echo "[INFO] Not running test with local instance. Won't set a new password"
fi

env | grep SNOWFLAKE_ | grep -v PASS | sort

# Avoid connection timeouts
export MAVEN_OPTS="$MAVEN_OPTS -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false -Dmaven.wagon.http.retryHandler.class=standard -Dmaven.wagon.http.retryHandler.count=3 -Dmaven.wagon.httpconnectionManager.ttlSeconds=120"

cd "$SOURCE_ROOT"

# Avoid connection timeout on plugin dependency fetch or fail-fast when dependency cannot be fetched
$MVNW_EXE --batch-mode --show-version dependency:go-offline

echo "[INFO] Run Hibernate tests"
$MVNW_EXE -Djava.io.tmpdir="$WORKSPACE" \
    -Djacoco.skip.instrument=false \
    -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
    verify \
    -DtestGroups="$TEST_GROUPS" \
    --batch-mode --show-version
