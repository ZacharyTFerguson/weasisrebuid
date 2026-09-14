#!/usr/bin/env bash
# Headless DICOM-understanding oracle. JSON verdict on stdout. No GUI.
# Usage: scripts/dicom-oracle.sh <part-10-path>
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if [[ -z "${JAVA_HOME:-}" ]]; then
  if command -v java >/dev/null 2>&1; then
    JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
  else
    echo "dicom-oracle.sh: set JAVA_HOME or install JDK 25+" >&2
    exit 1
  fi
fi
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"
if [[ $# -lt 1 ]]; then
  echo "usage: dicom-oracle.sh <part-10-path>" >&2
  exit 2
fi
FILE="$1"
# Compile reactor first, then exec only on the codec module (parent has no mainClass).
mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests install
mvn -q -pl weasis-dicom/weasis-dicom-codec exec:java -Dexec.args="$FILE"
