#!/usr/bin/env bash
# Copy reactor module target jars into weasis-distributions/target/native-stage.
# Does not read ~/.m2. Third-party OSGi jars must already be in --third-party DIR
# (typically filled by `mvn -P compressXZ` copy-dependencies, a *build-time* cache).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
STAGE="${ROOT}/weasis-distributions/target/native-stage"
THIRD=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --output) STAGE="${2:?}"; shift 2 ;;
    --third-party) THIRD="${2:?}"; shift 2 ;;
    *) echo "unknown arg: $1" >&2; exit 2 ;;
  esac
done
rm -rf "$STAGE"
mkdir -p "$STAGE/bundle" "$STAGE/i18n" "$STAGE/conf"

shopt -s nullglob
copy_one() {
  local src="$1"
  local dest_dir="$2"
  if [[ -f "$src" && "$src" != *original-* && "$src" != *-sources.jar && "$src" != *-javadoc.jar ]]; then
    cp -a "$src" "$dest_dir/"
  fi
}

LAUNCHER=""
for j in "$ROOT"/weasis-launcher/target/weasis-launcher-*.jar; do
  if [[ "$j" != *original* ]]; then
    LAUNCHER="$j"
  fi
done
if [[ -n "$LAUNCHER" ]]; then
  cp -a "$LAUNCHER" "$STAGE/Weasis.jar"
fi

for j in \
  "$ROOT"/weasis-core/target/weasis-core-*.jar \
  "$ROOT"/weasis-imageio/target/weasis-imageio-codec-*.jar \
  "$ROOT"/weasis-base/*/target/weasis-base-*.jar \
  "$ROOT"/weasis-dicom/*/target/weasis-dicom-*.jar \
  "$ROOT"/weasis-acquire/*/target/weasis-acquire-*.jar \
  "$ROOT"/weasis-opencv/*/target/weasis-opencv-*.jar
do
  copy_one "$j" "$STAGE/bundle"
done

if [[ -f "$ROOT/weasis-distributions/etc/config/base.json" ]]; then
  cp -a "$ROOT/weasis-distributions/etc/config/base.json" "$STAGE/conf/base.json"
elif [[ -f "$ROOT/weasis-launcher/conf/base.json" ]]; then
  cp -a "$ROOT/weasis-launcher/conf/base.json" "$STAGE/conf/base.json"
fi

if [[ -n "$THIRD" ]]; then
  if [[ "$THIRD" == *".m2"* ]]; then
    echo "stage-from-reactor.sh: refusing --third-party under .m2 (pass a staged directory)" >&2
    exit 2
  fi
  if [[ -d "$THIRD" ]]; then
    cp -a "$THIRD"/. "$STAGE/bundle/" || true
  fi
fi

# Package i18n fragments from source (jar cf), not Maven cache.
if [[ -d "$ROOT/weasis-i18n-dist" ]]; then
  for frag in "$ROOT/weasis-i18n-dist"/*/ ; do
    [[ -d "$frag" ]] || continue
    name="$(basename "$frag")"
    [[ "$name" == .* ]] && continue
    if [[ -f "$frag/META-INF/MANIFEST.MF" ]]; then
      (cd "$frag" && jar cf "$STAGE/i18n/${name}.jar" .)
    fi
  done
fi

echo "STAGED $STAGE"
