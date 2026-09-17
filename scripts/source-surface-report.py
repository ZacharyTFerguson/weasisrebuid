#!/usr/bin/env python3
"""Compare this clone's src/main/java paths to Weasis 4.7.3 public layout."""
from __future__ import annotations

import argparse
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FIXTURE = ROOT / "docs/weasis-spec/fixtures/weasis-4.7.3-main-java.txt"
CLONE_ONLY = ROOT / "docs/weasis-spec/fixtures/clone-only-main-java.txt"
GAP = ROOT / "docs/weasis-spec/fixtures/source-surface-gap.txt"


def local_main_java() -> set[str]:
    out = set()
    for p in ROOT.rglob("src/main/java/**/*.java"):
        if "/target/" in str(p):
            continue
        rel = p.relative_to(ROOT).as_posix()
        out.add(rel)
    return out


def load_list(path: Path) -> set[str]:
    if not path.is_file():
        return set()
    return {ln.strip() for ln in path.read_text(encoding="utf-8").splitlines() if ln.strip() and not ln.startswith("#")}


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--write-gap", action="store_true")
    args = parser.parse_args()
    upstream = load_list(FIXTURE)
    extras_allow = load_list(CLONE_ONLY)
    local = local_main_java()
    unexpected = sorted(p for p in local if p not in upstream and p not in extras_allow)
    missing = sorted(p for p in upstream if p not in local)
    print(f"local_main_java={len(local)}")
    print(f"weasis_4_7_3_main_java={len(upstream)}")
    print(f"present={len(local & upstream)}")
    print(f"clone_only_allowed={len(local & extras_allow)}")
    print(f"unexpected={len(unexpected)}")
    print(f"missing={len(missing)}")
    if args.write_gap:
        GAP.write_text("\n".join(missing) + ("\n" if missing else ""), encoding="utf-8")
        print(f"wrote {GAP}")
    if unexpected:
        print("UNEXPECTED_PATHS")
        for p in unexpected:
            print(p)
        return 1
    print("SURFACE_OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
