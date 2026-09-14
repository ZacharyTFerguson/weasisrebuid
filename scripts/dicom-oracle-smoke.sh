#!/usr/bin/env bash
# CI/local smoke: codec bundle embed + oracle unit tests (includes CLI JSON on stdout).
# After a root `mvn package`, set DICOM_ORACLE_SKIP_BUILD=1 to reuse reactor artifacts.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
if [[ "${DICOM_ORACLE_SKIP_BUILD:-}" != "1" ]]; then
  mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests package
fi
mvn -q -pl weasis-dicom/weasis-dicom-codec -am test \
  -Dtest=CodecBundleEmbedTest,DicomUnderstandingOracleTest \
  -Dsurefire.failIfNoSpecifiedTests=false
echo "DICOM_ORACLE_SMOKE_OK"
