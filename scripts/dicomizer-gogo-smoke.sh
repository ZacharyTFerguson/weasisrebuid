#!/usr/bin/env bash
# Dicomizer Gogo smoke (not JUnit): overlay dicomizer.json, Gogo 17181, acquire Active, no viewer2d.
# Does not replace desktop gogo-smoke.sh (17179). Not a CHECKLIST §6 Pass.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-$HOME/tools/jdk-25}"
export PATH="$JAVA_HOME/bin:$PATH"
PORT="${GOSH_PORT:-17181}"
CACHE="${TMPDIR:-/tmp}/weasis-dicomizer-cache-$$"
rm -rf "$CACHE"
mvn -q install -DskipTests
JAR=$(ls weasis-launcher/target/weasis-launcher-*.jar | grep -v original | head -1)
java -Djava.awt.headless=true \
  -Dfelix.extended.config.properties="file:$ROOT/weasis-launcher/conf/dicomizer.json" \
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
    sys.exit("Dicomizer Gogo did not listen on %s" % port)
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
ver = send("weasis:info -v")
print(ver)
if "4.7" not in ver:
    sys.exit("weasis:info -v did not print a 4.7 version: %r" % ver)
info = send("weasis:info -a")
print(info)
if "17181" not in info:
    sys.exit("weasis:info -a did not print gosh.port 17181: %r" % info)
if "dicomizer" not in info.lower():
    sys.exit("weasis:info -a did not print weasis.profile dicomizer: %r" % info)
lb = send("lb")
print(lb)
def bundle_state(listing, needle):
    for line in listing.splitlines():
        if needle in line:
            parts = [p.strip() for p in line.split("|")]
            if len(parts) >= 2:
                return parts[1]
    return ""
if bundle_state(lb, "Weasis Acquire Explorer") != "Active":
    sys.exit("dicomizer lb missing acquire-explorer ACTIVE: %r" % lb)
if bundle_state(lb, "Weasis Acquire Editor") != "Active":
    sys.exit("dicomizer lb missing acquire-editor ACTIVE: %r" % lb)
if bundle_state(lb, "Weasis DICOM Send") != "Active":
    sys.exit("dicomizer lb missing send ACTIVE: %r" % lb)
if bundle_state(lb, "Weasis DICOM 2D Viewer") == "Active":
    sys.exit("dicomizer must not start viewer2d: %r" % lb)
if bundle_state(lb, "Weasis DICOM Q/R") == "Active":
    sys.exit("dicomizer must not start qr: %r" % lb)
if "START LEVEL 110" not in lb and "Level" not in lb:
    pass
send("weasis:ui -q")
print("DICOMIZER_SMOKE_OK")
PY
