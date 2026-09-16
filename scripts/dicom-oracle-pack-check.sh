#!/usr/bin/env bash
# Run the Java oracle on every file in testdata/weasis-roundtrip/manifest.json.
# Fail-closed: exit 1 if any instance is not understood (matches cross-oracle expectations).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PACK="$ROOT/testdata/weasis-roundtrip"
MANIFEST="$PACK/manifest.json"
if [[ ! -f "$MANIFEST" ]]; then
  echo "missing $MANIFEST" >&2
  exit 2
fi
export DICOM_ORACLE_SKIP_BUILD=1
fail=0
while IFS= read -r path; do
  [[ -z "$path" ]] && continue
  abs="$PACK/$path"
  if ! bash "$ROOT/scripts/dicom-oracle.sh" "$abs"; then
    echo "oracle failed: $path" >&2
    fail=1
  fi
done < <(
  python3 -c "
import json
from pathlib import Path
m = json.loads(Path('${MANIFEST}').read_text())
for s in m['studies']:
    print(s['path'])
"
)
exit "$fail"
