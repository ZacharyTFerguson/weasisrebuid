#!/usr/bin/env bash
# Headless DICOM-understanding oracle. JSON verdict on stdout. No GUI.
# Fail-closed: exit 0 only when understood; 1 when not decoded; 2 when unopenable / usage.
# Gate: DicomUnderstandingLimits (uncompressed EVR LE MONOCHROME2). See
# docs/architecture/clean-room-and-understanding.md
# Maven may log above JSON; cross-oracle consumers keep the last line starting with "{".
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
  echo "usage: dicom-oracle.sh <part-10-path>  (JSON verdict on stdout; synthetic fixtures; no PHI)" >&2
  exit 2
fi
FILE="$1"
# Compile reactor first, then exec only on the codec module (parent has no mainClass).
if [[ "${DICOM_ORACLE_SKIP_BUILD:-}" != "1" ]]; then
  mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests install
fi
set +e
raw="$(mvn -q -pl weasis-dicom/weasis-dicom-codec exec:java -Dexec.args="$FILE" 2>/dev/null)"
code=$?
set -e
json="$(printf '%s\n' "$raw" | awk 'BEGIN{last=""} /^\{/{last=$0} END{print last}')"
if [[ -n "$json" ]]; then
  printf '%s\n' "$json"
fi
exit "$code"
