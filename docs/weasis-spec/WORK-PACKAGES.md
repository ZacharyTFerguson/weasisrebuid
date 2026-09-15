# Work packages

| WP | Scope | Done (Have) |
|---|---|---|
| 0 | `AppLauncher` Felix 7.0.5, Gogo 17179, `weasis:info -v` → 4.7.3 | smoke `lb` Felix + core |
| 1 | Core SDK, dummy `SeriesViewerFactory` | blank `ViewerPlugin` |
| 2 | OpenCV fragment, imageio, dicom-codec | EVR LE MONOCHROME2 CT W/L paints |
| 3 | Import + Explorer | synthetic DICOMDIR; File>Import DICOM and DICOM CD |
| 4 | View2d LUT chain, `dcmview2d:*` | W/L paints; **no MPR** |
| 5 | Draw & Measure, KO, PR/GSPS | every graphic `buildShape` non-null when complete; KO/PR files |
| 6 | Docking Frames, FoR+manual sync, lens, 2D 3D-cursor | FoR vs MANUAL MX-14; docking-frames **Active** @10 (FlatLaf extras on system classpath) |
| 7 | MPR / CPR / MIP | `mpr` types; MIP None/Min/Mean/Max |
| 8 | Fusion | `FusionController.targetViews`; SUVbw |
| 9 | JOGL 3D VR | MX-15 Have: GL 3.3+; refuse llvmpipe; N/A no GPU; jogamp Active @120, native Resolved @121 |
| 10 | SR AU ECG RT SEG | MIME + factories Active on `lb` |
| 11 | QR C-FIND/MOVE/GET, WADO, QIDO/STOW, send, ISO, print | network Have tests |
| 12 | Dicomizer profile | `dicomizer.json` overlay; acquire convert Have; Gogo 17181 when `weasis.profile=dicomizer` |
| 13 | `weasis://`, `$dicom:get`/`-rs`, non-DICOM profile | URI + `$weasis:config` parser; `dicom:get`/`rs`/`close`; `non-dicom-explorer.json` |
| 14 | i18n fragments, ≥30 % lang pref, native zip | `weasis-core-i18n` fragment (name ends in i18n) Installed @13; language list ≥ 30 %; `weasis-native.zip` via `-P compressXZ` |
| 15 | ViewerHub `/display`, dcm4chee IID | `/display` and `/display/IHEInvokeImageDisplay` 302 `weasis://`; dcm4chee IID templates |

WP-0–4 Have is recorded in `docs/loops/TEAM-B-SCORE-wp0-4.md`. This branch adds Have tests for MX-14 (FoR vs manual), MPR/MIP, fusion `targetViews`, special-SOP factories Active on `lb`, WP-11 Q/R + DICOMweb + STOW + ISO + print, WP-12 Dicomizer overlay plus still/video/PDF/STL convert Have, WP-6 Docking Frames **Active**, WP-9 MX-15 OpenGL 3.3+ / refuse llvmpipe (jogamp **Active @120**, native **Resolved @121**), WP-13 `weasis://` parsers, WP-14 i18n ≥30 % plus `weasis-native.zip`, and WP-15 ViewerHub `/display` plus dcm4chee IID `weasis://`. All **985** Weasis 4.7.3 main Java paths exist; matching a path is **not** feature-complete. Headed §6 Pass stays empty. SCORE may only change Have boxes.
