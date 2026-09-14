#!/usr/bin/env bash
# CI/local smoke: codec bundle embed + oracle unit tests (includes CLI JSON on stdout).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
mvn -q -pl weasis-dicom/weasis-dicom-codec -am -DskipTests package
mvn -q -pl weasis-dicom/weasis-dicom-codec test -Dtest=CodecBundleEmbedTest,DicomUnderstandingOracleTest
echo "DICOM_ORACLE_SMOKE_OK"
