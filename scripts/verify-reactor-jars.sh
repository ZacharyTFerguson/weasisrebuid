#!/usr/bin/env bash
# Assert OSGi bundle jars exist after `mvn package` (Felix base.json points at these artifacts).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
VERSION="${WEASIS_VERSION:-4.7.3}"
OPENCV_VER="${WEASIS_OPENCV_PKG_VERSION:-5.0.0-dcm}"
SPEC="${WEASIS_NATIVE_SPEC:-linux-x86-64}"

require_jar() {
  local path="$1"
  if [[ ! -f "$path" ]]; then
    echo "missing reactor jar: $path" >&2
    exit 1
  fi
}

require_jar "weasis-core/target/weasis-core-${VERSION}.jar"
require_jar "weasis-imageio/target/weasis-imageio-codec-${VERSION}.jar"
require_jar "weasis-base/weasis-base-ui/target/weasis-base-ui-${VERSION}.jar"
require_jar "weasis-dicom/weasis-dicom-codec/target/weasis-dicom-codec-${VERSION}.jar"
require_jar "weasis-dicom/weasis-dicom-explorer/target/weasis-dicom-explorer-${VERSION}.jar"
require_jar "weasis-dicom/weasis-dicom-viewer2d/target/weasis-dicom-viewer2d-${VERSION}.jar"
require_jar "weasis-launcher/target/weasis-launcher-${VERSION}.jar"
require_jar "weasis-opencv/weasis-opencv-core-${SPEC}/target/weasis-opencv-core-${SPEC}-${OPENCV_VER}.jar"
echo "REACTOR_JARS_OK"
