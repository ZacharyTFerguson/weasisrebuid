#!/usr/bin/env bash
# Gogo smoke (not JUnit): AppLauncher, weasis:info -v, lb. Not a CHECKLIST §6 Pass.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-$HOME/tools/jdk-25}"
export PATH="$JAVA_HOME/bin:$PATH"
PORT="${GOSH_PORT:-17179}"
CACHE="${TMPDIR:-/tmp}/weasis-wp0-cache-$$"
rm -rf "$CACHE"
mvn -q install -DskipTests
JAR=$(ls weasis-launcher/target/weasis-launcher-*.jar | grep -v original | head -1)
java -Djava.awt.headless=true \
  -Dgosh.port="$PORT" \
  -Dweasis.base.json="$ROOT/weasis-launcher/conf/base.json" \
  -Dorg.osgi.framework.storage="$CACHE" \
  -Dweasis.boot.timeout.seconds=45 \
  -jar "$JAR" &
PID=$!
trap 'kill "$PID" 2>/dev/null || true; rm -rf "$CACHE"' EXIT
python3 - "$PORT" << 'PY'
import socket, sys, time
port = int(sys.argv[1])
deadline = time.time() + 30
sock = None
while time.time() < deadline:
    try:
        sock = socket.create_connection(("127.0.0.1", port), 1)
        break
    except OSError:
        time.sleep(0.2)
if sock is None:
    sys.exit("Gogo did not listen on %s" % port)
def send(cmd):
    sock.sendall((cmd + "\n").encode())
    time.sleep(0.4)
    sock.settimeout(2)
    chunks = []
    try:
        while True:
            data = sock.recv(4096)
            if not data:
                break
            chunks.append(data)
            if b"g!" in b"".join(chunks) and len(chunks) > 1:
                break
    except TimeoutError:
        pass
    return b"".join(chunks).decode("utf-8", "replace")
banner = send("")
print(banner)
out = send("weasis:info -v")
print(out)
if "4.7" not in out:
    sys.exit("weasis:info -v did not print a 4.7 version: %r" % out)
lb = send("lb")
print(lb)
felix_ok = "System Bundle" in lb or "7.0.5" in lb or "felix" in lb.lower()
core_ok = "Weasis Core" in lb or "weasis-core" in lb or "org.weasis.core" in lb
if not felix_ok or not core_ok:
    sys.exit("lb did not list Felix + core: %r" % lb)
img_ok = "weasis-core-img" in lb or "Image processing" in lb
imageio_ok = "weasis-imageio" in lb or "ImageIO" in lb or "org.weasis.imageio" in lb
codec_ok = "weasis-dicom-codec" in lb or "DICOM Codec" in lb or "org.weasis.dicom.codec" in lb
opencv_ok = "weasis-opencv-core" in lb or "linux-x86-64" in lb
if not img_ok:
    sys.exit("lb did not list weasis-core-img: %r" % lb)
if not imageio_ok:
    sys.exit("lb did not list weasis-imageio-codec: %r" % lb)
if not codec_ok:
    sys.exit("lb did not list weasis-dicom-codec: %r" % lb)
if not opencv_ok:
    sys.exit("lb did not list OpenCV native fragment (install @23): %r" % lb)
send("weasis:ui -q")
print("SMOKE_OK")
PY
