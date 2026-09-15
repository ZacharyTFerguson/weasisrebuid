# Work packages

| WP | Scope | Done (Have) |
|---|---|---|
| 0 | `AppLauncher` Felix 7.0.5, Gogo 17179, `weasis:info -v` → 4.7.3 | smoke `lb` Felix + core |
| 1 | Core SDK, dummy `SeriesViewerFactory` | blank `ViewerPlugin` |
| 2 | OpenCV fragment, imageio, dicom-codec | EVR LE MONOCHROME2 CT W/L paints |
| 3 | Import + Explorer | synthetic DICOMDIR; File>Import DICOM and DICOM CD |
| 4 | View2d LUT chain, `dcmview2d:*` | W/L paints; **no MPR** |
| 5 | Draw & Measure, KO, PR/GSPS | every graphic `buildShape` non-null when complete; KO/PR files |
| 6 | Docking Frames, FoR+manual sync, lens, 2D 3D-cursor | start level 10 docking jar; MX-14 |
| 7 | MPR / CPR / MIP | `mpr` types; MIP None/Min/Mean/Max |
| 8 | Fusion | `FusionController.targetViews`; SUVbw |
| 9 | JOGL 3D VR | OpenGL 3.3+; refuse llvmpipe; N/A no GPU |
| 10 | SR AU ECG RT SEG | MIME + factories Active on `lb` |
| 11 | QR C-FIND/MOVE/GET, WADO, QIDO/STOW, send, ISO, print | network Have tests |
| 12 | Dicomizer profile | acquire modules; Gogo 17181 |
| 13 | `weasis://`, `$dicom:get`/`-rs`, non-DICOM profile | protocol tests |
| 14 | i18n fragments, ≥30 % lang pref, native zip | compressXZ without relying on Maven cache as the zip |
| 15 | ViewerHub `/display`, dcm4chee IID | opens clone via `weasis://` |

WP-0–4 Have is recorded in `docs/loops/TEAM-B-SCORE-wp0-4.md`. This branch adds Have tests for MX-14 (FoR vs manual), MPR/MIP, fusion `targetViews`, and special-SOP factories Active on `lb`. Docking Frames at start level 10, JOGL natives, headed §6 Pass, and remaining source-surface paths stay open. SCORE may only change Have boxes.
