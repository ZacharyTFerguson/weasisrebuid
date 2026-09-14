#!/usr/bin/env bash
# Headless DICOM-understanding oracle. JSON verdict on stdout. No GUI.
# Fail-closed: exit 0 only when understood; 1 when not decoded; 2 when unopenable / usage.
# Usage: scripts/dicom-oracle.sh <part-10-path>
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-$HOME/tools/jdk-25}"
export PATH="$JAVA_HOME/bin:$PATH"
if [[ $# -lt 1 ]]; then
  echo "usage: dicom-oracle.sh <part-10-path>  (JSON verdict on stdout; synthetic fixtures; no PHI)" >&2
  exit 2
fi
FILE="$1"
# Compile reactor first, then exec only on the codec module (parent has no mainClass).
<<<<<<< HEAD
if [[ "${DICOM_ORACLE_SKIP_BUILD:-}" != "1" ]]; then
  mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests install
fi
set +e
mvn -q -pl weasis-dicom/weasis-dicom-codec exec:java -Dexec.args="$FILE"
code=$?
set -e
exit "$code"
=======
mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests install
# Maven may log above JSON; cross-oracle consumers keep the last line starting with "{".
mvn -q -pl weasis-dicom/weasis-dicom-codec exec:java -Dexec.args="$FILE" 2>/dev/null | awk 'BEGIN{last=""} /^\{/{last=$0} END{print last}'
>>>>>>> origin/cursor/oracle-taste-honest-verdict-3711
