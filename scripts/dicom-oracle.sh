#!/usr/bin/env bash
# Headless DICOM-understanding oracle. JSON verdict on stdout. No GUI.
# Usage: scripts/dicom-oracle.sh <part-10-path>
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-$HOME/tools/jdk-25}"
export PATH="$JAVA_HOME/bin:$PATH"
if [[ $# -lt 1 ]]; then
  echo "usage: dicom-oracle.sh <part-10-path>" >&2
  exit 2
fi
FILE="$1"
mvn -q -pl weasis-dicom/weasis-dicom-codec -am exec:java -Dexec.args="$FILE"
