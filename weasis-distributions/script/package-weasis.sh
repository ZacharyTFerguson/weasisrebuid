#!/usr/bin/env bash
# Native zip from a staged tree. Does NOT read ~/.m2 or maven.local.repo.
# maven.local.repo remains DEV-only (weasis-launcher/conf/base.json).
#
# Usage:
#   package-weasis.sh --jdk /path/to/jdk [--from-target DIR] [--output FILE] [--skip-jpackage]
#
# --from-target layout (required; default weasis-distributions/target/native-stage):
#   Weasis.jar              shaded launcher (or weasis-launcher-*.jar)
#   bundle/*.jar            OSGi host + third-party bundles (already copied)
#   i18n/*.jar              optional weasis-i18n-dist fragments (not auto.start)
#   conf/base.json          Maven-dev JSON; this script rewrites file: URLs
#
# Documented no-m2 path: stage-from-reactor.sh then this script.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
JDK=""
FROM_TARGET="${ROOT}/weasis-distributions/target/native-stage"
OUTPUT=""
SKIP_JPACKAGE=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    --jdk)
      JDK="${2:?}"
      shift 2
      ;;
    --from-target)
      FROM_TARGET="${2:?}"
      shift 2
      ;;
    --output)
      OUTPUT="${2:?}"
      shift 2
      ;;
    --skip-jpackage)
      SKIP_JPACKAGE=1
      shift
      ;;
    -h|--help)
      sed -n '2,20p' "$0"
      exit 0
      ;;
    *)
      echo "unknown arg: $1" >&2
      exit 2
      ;;
  esac
done

if [[ -z "$JDK" ]]; then
  echo "package-weasis.sh: --jdk is required (ARCHITECTURE native zip / jpackage)" >&2
  exit 2
fi
if [[ ! -x "$JDK/bin/java" && ! -x "$JDK/bin/java.exe" ]]; then
  echo "package-weasis.sh: --jdk does not look like a JDK: $JDK" >&2
  exit 2
fi
if [[ ! -d "$FROM_TARGET" ]]; then
  echo "package-weasis.sh: --from-target missing: $FROM_TARGET" >&2
  echo "maven.local.repo is DEV only. Stage with weasis-distributions/script/stage-from-reactor.sh" >&2
  exit 2
fi

# Refuse to probe the Maven cache.
if [[ "${FROM_TARGET}" == *".m2"* ]]; then
  echo "package-weasis.sh: refusing a from-target under .m2" >&2
  exit 2
fi

WORK="$(mktemp -d "${TMPDIR:-/tmp}/weasis-native.XXXXXX")"
trap 'rm -rf "$WORK"' EXIT
DEST="$WORK/weasis-native"
mkdir -p "$DEST/resources/bundle" "$DEST/resources/i18n" "$DEST/resources/conf"
mkdir -p "$DEST/dicom" "$DEST/DICOM" "$DEST/IMAGES" "$DEST/images"

LAUNCHER=""
for cand in "$FROM_TARGET/Weasis.jar" "$FROM_TARGET"/weasis-launcher-*.jar; do
  if [[ -f "$cand" ]]; then
    LAUNCHER="$cand"
    break
  fi
done
if [[ -z "$LAUNCHER" ]]; then
  echo "package-weasis.sh: no Weasis.jar / weasis-launcher-*.jar in $FROM_TARGET" >&2
  exit 2
fi
cp "$LAUNCHER" "$DEST/Weasis.jar"

if [[ -d "$FROM_TARGET/bundle" ]]; then
  cp -a "$FROM_TARGET/bundle/." "$DEST/resources/bundle/" || true
fi
if [[ -d "$FROM_TARGET/i18n" ]]; then
  cp -a "$FROM_TARGET/i18n/." "$DEST/resources/i18n/" || true
fi

BASE_SRC=""
for cand in "$FROM_TARGET/conf/base.json" "$FROM_TARGET/etc/config/base.json"; do
  if [[ -f "$cand" ]]; then
    BASE_SRC="$cand"
    break
  fi
done
if [[ -z "$BASE_SRC" ]]; then
  echo "package-weasis.sh: no conf/base.json in $FROM_TARGET" >&2
  exit 2
fi

python3 - "$BASE_SRC" "$DEST/resources/conf/base.json" << 'PY'
import re, sys
src, dst = sys.argv[1], sys.argv[2]
text = open(src, encoding="utf-8").read()
pat = re.compile(
    r"file:\$\{(?:maven\.localRepository|maven\.local\.repo|settings\.localRepository)\}/\S+?/([^/\"\s]+\.jar)"
)
text = pat.sub(lambda m: "file:${weasis.resources.path}/bundle/" + m.group(1), text)
if "weasis.i18n.dir" not in text:
    needle = '"weasisPreferences": ['
    insert = (
        needle
        + "\n    {\"code\": \"weasis.i18n.dir\", \"value\": \"${weasis.resources.path}/i18n\","
        + ' "description": "i18n fragments (not felix.auto.start)", "type": "A", "category": "LAUNCH"},'
    )
    text = text.replace(needle, insert, 1)
open(dst, "w", encoding="utf-8").write(text)
if "${maven.localRepository}" in text or "${maven.local.repo}" in text:
    # leftover placeholders only allowed on the unused maven.local.repo pref value
    import json
    data = json.loads(text)
    for pref in data.get("weasisPreferences", []):
        code = pref.get("code", "")
        val = pref.get("value", "")
        if code.startswith("felix.auto.") and (
            "maven.localRepository" in val or "maven.local.repo" in val
        ):
            sys.exit("rewrite left maven repo on " + code)
PY

cat > "$DEST/Weasis.sh" << 'SH'
#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/java}"
if [[ -z "${JAVA_BIN}" || ! -x "${JAVA_BIN}" ]]; then
  JAVA_BIN="java"
fi
exec "$JAVA_BIN" -Djava.awt.headless="${WEASIS_HEADLESS:-false}" \
  -Dweasis.resources.path="$HERE/resources" \
  -Dweasis.base.json="$HERE/resources/conf/base.json" \
  -Dweasis.portable.root="$HERE" \
  -jar "$HERE/Weasis.jar" "$@"
SH
chmod +x "$DEST/Weasis.sh"

if [[ $SKIP_JPACKAGE -eq 0 && -x "$JDK/bin/jpackage" ]]; then
  echo "package-weasis.sh: jpackage present at $JDK (outline only; zip is the Have artifact)" >&2
fi

OUTPUT="${OUTPUT:-$PWD/weasis-native.zip}"
python3 - "$DEST" "$OUTPUT" << 'PY'
import os, sys, zipfile
src, dst = sys.argv[1], sys.argv[2]
os.makedirs(os.path.dirname(os.path.abspath(dst)), exist_ok=True)
with zipfile.ZipFile(dst, "w", zipfile.ZIP_DEFLATED) as zf:
    for root, dirs, files in os.walk(src):
        for name in files:
            path = os.path.join(root, name)
            arc = os.path.relpath(path, src)
            zf.write(path, arcname=os.path.join("weasis-native", arc))
        for name in dirs:
            path = os.path.join(root, name)
            if not os.listdir(path):
                arc = os.path.relpath(path, src) + "/"
                zf.writestr(os.path.join("weasis-native", arc), "")
print("wrote", dst)
PY
echo "NATIVE_ZIP_OK $OUTPUT"
