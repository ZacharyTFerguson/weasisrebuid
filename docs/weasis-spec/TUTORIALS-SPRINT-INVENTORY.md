# TUTORIALS.md sprint inventory (PR #25)

Mapped once from [TUTORIALS.md](TUTORIALS.md) tutorial-order rows. **§6 Pass stays empty** until a single headed full walkthrough completes.

**Staffing (2026-09-17):** Grok writer `bc-46ed1833` (**RUNNING**, “Next TUTORIALS after R/Q PASS”) · Composer `bc-8bf553b7` (**IDLE**, prove fresh jars) · Coordinator documents only while writer runs.

**HEAD (inventory time):** `b63e035` · product `0136004` (`alt-state` chrome landed; Composer proof **pending**).

## Row ledger

| # | TUTORIALS.md row | WP | Headed slice status | Blocked? | Owner |
|---|---|---|---|---|---|
| 1 | GUI overview / prefs | 0–1 | Partial: Help shortcuts/resources, About, Preferences **headed OK** (`e88e780`, `17df6d0`, `fe8df27`); Felix/`weasis:ui` Have | — | Composer (residual keys/F11 if queued) |
| 2 | DICOM Import / Explorer | 3 | Import/Export **OK** (`a569b23`, `363f172`); explorer filter **OK** (`267135c`); series DnD → `CLocation` split **not exercised** (`0831d3d` east tab only) | DnD split needs multi-series fixture + drag script | Writer → Composer |
| 3 | DICOM 2D / LUT / Zoom | 4 | Large chrome PASS set (flip/window/crop/brightness/autolevels/mask/shutter/filter/inverse/zoom-rot/T/W/S/Z/R/Q/**alt-state pending** on `0136004`); ImagePrint/Freeze/Cal **OK** | **Cine on Reed** skipped per steer | Composer **now** on `0136004`; then writer next chrome |
| 4 | Draw & Measure | 5 | Measure angle/ROI/hang **OK** (`ddcc24c`); graphics-count/sel **OK** (`3699b45`, `da1e703`); region-stats **OK** | — | Writer if new measure chrome; Composer proof |
| 5 | Build KO and PR | 5 | apply-pr, interp, ko/pr uid, ko-state **OK** (`9b9c9dc`…`4bf92c6`) | — | — |
| 6 | Docking / sync / 3D cursor / histogram / lens | 6 | FoR, lens, pixel-info, mini-tool, histogram, docking keys, east split **OK**; west/south/north splits **not captured** | — | Writer → Composer |
| 7 | MPR / CPR / MIP | 7 | MPR chrome **OK** (`d1e96de`) | Anatomy for CPR depth optional | Composer spot-check |
| 8 | Fusion / SUV | 8 | Fusion chrome **OK** (`ec522f1`) | PET/CT stack headed pixels optional | — |
| 9 | 3D VR | 9 | 3D toolbar chrome **OK** (`6f19c30`) | GPU / llvmpipe environment | — |
| 10 | SR / AU / ECG / RT / SEG | 10 | SEG Show overlay toggle **OK** (`b1a692a`); mask pixels **not scored** (no SEG SOP) | **AU/ECG/SR/RT** without SOP anatomy | **Skip** (steer) |
| 11 | Q/R, DICOMweb, send, ISO, print | 11 | qr/send/iso/print **OK**; WADO/DICOMweb headed not in PR ledger | No PACS / persist calling-AE | Writer for chrome only |
| 12 | Dicomizer | 12 | Gogo 17181 + Have tests; **no headed Dicomizer row in PR #25 ledger** | — | Writer → Composer |
| 13 | `weasis://` `$dicom:get` `$dicom:rs` | 13 | `dicom:get` used everywhere; **no headed** `dicom:close` / `image:get` / non-DICOM explorer walkthrough | — | Writer → Composer |
| 14 | i18n ≥30 %, native zip | 14 | Have (`NativeDistributionHaveTest`, fragments @13); **no headed lang-pref walkthrough** | — | Writer doc/chrome |
| 15 | ViewerHub / IID | 15 | Have `/display` tests; **no headed ViewerHub ledger** | — | Writer → Composer |

## Active queue (tutorial-order)

1. **Composer (immediate):** headed proof **`0136004`** — Alt+R/L/F / `name=alt-state` on chest-bound `org.weasis.dicom.viewer2d.View2d` (not `cal-view`). Jar: `/tmp/weasis-headed-0136004.jar`, SHA `01360042956f3f75eae05008a74b9557334a4e4e`.
2. **Writer (`bc-46ed1833`, do not duplicate):** next tutorial-order item after alt-state lands — likely explorer **series DnD** `CLocation` split and/or **Dicomizer** / **protocol** headed chrome (see gaps above).
3. **Skip (do not staff):** cine on Reed; AU/ECG/SR/RT without SOPs; Q/R calling-AE persist; PACS/print SCP/ISO burn.

## SOURCE-SURFACE / WORK-PACKAGES

- `scripts/source-surface-report.py` → **SURFACE_OK** (985/985 paths, 87 clone-only documented).
- Remaining work is **feature depth** and **headed mastery**, not missing Java paths.

## Slack

**Not used** — no in-repo blocker requiring team input this tick.
