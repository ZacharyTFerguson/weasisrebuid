# Team B SCORE — WP-0–4

**Clone:** `ZacharyTFerguson/weasisrebuid`  
**Tree scored:** `main` at `ffe6a464b86c64e19ce546a5e4964c592e1c157d` plus Team A CD-ROM detector (this commit).  
**Pin:** Weasis **4.7.3** clean-room (not a fork of `nroduit/Weasis`).  
**Spec oracle:** `/tmp/weasis-spec/` (CHECKLIST, SRS, ARCHITECTURE, REIMPLEMENTATION, CLONE-TEAM).  
**When scored:** 2026-09-11. Headless Cloud Agent. **GUI: not run.** Composer does **not** tick dicomlight CHECKLIST or §6 Pass.

## Evidence that counts

| Command | Result |
|---|---|
| `JAVA_HOME=$HOME/tools/jdk-25` `mvn -q test` (reactor) | **PASS** (exit 0) |
| `mvn -q spotless:check` | **PASS** (`SPOTLESS_OK`) |
| `bash scripts/gogo-smoke.sh` | **PASS** (`SMOKE_OK`) |

Smoke log (not §6 Pass): Apache Felix **7.0.5**; `weasis:info -v` → **4.7.3**; `lb` Active Gogo @1/@3, DS @5, core-img @10, Weasis Core @12, ImageIO Codec @15, OpenCV native **Resolved @23**, DICOM Codec @30, Jackson @35, Explorer **Active @40**, Base UI @60, viewer2d **Active @70**. Later-WP jars skipped (SR/AU/wave/send/qr/rt/3d/JOGL) — expected until those WPs.

Synthetic DICOMDIR / EVR LE CT: `LoadLocalDicomTest`, `WindowLevelPaintTest`, `View2dLutPaintTest` (UID `2.25.*`, no PHI).

**Honesty:** this is a **partial clone**. `weasis:info` and JUnit are **not** CHECKLIST §6 Pass. Parties are not fully satisfied until Team B clicks the live GUI.

---

## Verdicts (WP Done criterion = Have via unit/smoke)

| WP | Verdict | Done criterion | Notes |
|---|---|---|---|
| **WP-0** | **PASS** | `weasis:info`; `lb` Felix + core | Smoke + `Mx03ShippingPrefsTest` + `Mx16GogoPortTest` |
| **WP-1** | **PASS** | dummy `SeriesViewerFactory` opens blank `ViewerPlugin` | `DummySeriesViewerFactoryTest`, `SeriesViewerFactoryRegistrationTest` |
| **WP-2** | **PASS** | EVR LE MONOCHROME2 CT; W/L paints | `WindowLevelPaintTest`; OpenCV install @23 |
| **WP-3** | **PASS** | Import + Explorer on synthetic DICOMDIR | `LoadLocalDicomTest`; Team A added `CdromDetector` (Detect CD-ROM Have) |
| **WP-4** | **PASS** | 2D + LUT Have (W/L paints); **no MPR** | `View2dLutPaintTest`, `DicomView2dCommandsTest`; MX-07 names |

Later-WP Have boxes (docking persistence, Q/R, MPR, fusion, 3D, SR/AU, Dicomizer, `weasis://`) are **not** scored as WP-0–4 FAIL.

---

## SCORE table

| Have id | Clone | Test | GUI | Notes |
|---|---|---|---|---|
| WP-0 Done: `weasis:info`; `lb` Felix + core | PASS | `scripts/gogo-smoke.sh`; `WeasisInfoCommandTest` | not run | Felix 7.0.5 + Weasis Core Active @12 |
| WP-0 MX-03 dist vs launcher `base.json` | PASS | `Mx03ShippingPrefsTest` | N/A | Shipping = dist INFO / stack 3 / felix.log.level 1 |
| WP-0 MX-16 Gogo **17179** | PASS | `Mx16GogoPortTest`; smoke binds 17179 | N/A | `gosh.port` VM property, not JSON. Dicomizer 17181 is WP-12 |
| WP-0 AppLauncher plain JAR / beginning 130 / bundle 300 | PASS | smoke start-level 130 | N/A | |
| §3 start **1 / 3 / 4 / 5** Gogo+DS | PASS | smoke `lb` | N/A | |
| §3 start **7** MigLayout + JAXB-OSGi | FAIL | smoke skip missing miglayout jars | N/A | WP-6 chrome; not WP-0 Done |
| §3 start **10** Docking Frames + core-img + zip4j + JOML | PARTIAL | smoke: core-img/zip4j/JOML Active; Docking Frames not installed | N/A | Docking Frames is WP-6 |
| §3 start **12** weasis-core | PASS | smoke | N/A | |
| §3 start **15** `weasis-imageio-codec` (dir `weasis-imageio/`) | PASS | smoke; `ImageioCodecTest` | N/A | Artifact name is not `weasis-imageio` |
| §3 start **23** OpenCV `weasis-opencv-core-${native.library.spec}-${weasis.opencv.pkg.version}.jar` | PASS | smoke Resolved @23 | N/A | linux-x86-64 `5.0.0-dcm` |
| §3 start **30** weasis-dicom-codec | PASS | smoke | N/A | |
| §3 start **35** Jackson | PASS | smoke | N/A | |
| §3 start **40** weasis-dicom-explorer | PASS | smoke | N/A | |
| §3 start **60** weasis-base-ui | PASS | smoke | N/A | Dummy viewer factory |
| §3 start **70** viewer2d + SR | PARTIAL | smoke viewer2d Active; SR jar skipped | N/A | SR is WP-10 |
| §3 start **75–121** AU/wave/base-viewer2d/send/qr/rt/JOGL/3d | FAIL | smoke skip missing | N/A | WP-10/11/9 — not WP-0–4 Done |
| §3 Dicomizer profile / non-DICOM profile / i18n fragments | FAIL | none | N/A | WP-12 / WP-13 / WP-14 |
| §3.1 weasis-core-img + weasis-dicom-tools | PASS | codec reactor + smoke | N/A | Published deps |
| WP-1 Done: dummy viewer opens | PASS | `DummySeriesViewerFactoryTest`; `SeriesViewerFactoryRegistrationTest` | not run | Headless; factory returns `DummyViewerPlugin` |
| WP-1 Insertable / OpManager / Graphic stubs / prefs / UICore | PASS | `InsertableFactoryRegistrationTest`; `SimpleOpManagerTest`; `PreferenceDialogTest`; `WPropertiesTest` | not run | Graphic **stubs** until WP-5 |
| WP-1 MX-06 Direct vs Manual proxy | PASS | `Mx06ProxyPrefsTest` | not run | Switching Direct does not clear in-session hosts; no user/password on Manual |
| WP-2 Done: EVR LE MONOCHROME2 CT W/L paints | PASS | `WindowLevelPaintTest` | not run | 8-bit grey; center brighter than corner |
| WP-2 TS catalog / MIME / LUT / anonymize / SUVbw factor | PASS | `TransferSyntaxTest`; `DicomMimeTest`; `LutPipelineTest`; `AnonymizationProfileTest`; `SuvFactorTest` | N/A | |
| WP-2 codec providers Dicom/Imageio/DicomZip/NativeOpenCV | PASS | codec + imageio tests | N/A | `@Component(service = Codec.class)` |
| WP-2 OpenCvNativeIT | PASS | skip `N/A: no native` or run on this linux-x86-64 builder | N/A | |
| WP-3 Done: Import + Explorer synthetic DICOMDIR | PASS | `LoadLocalDicomTest.dicomdirResolvesReferencedFile` | not run | No PHI |
| WP-3 File > Import > DICOM **and** DICOM CD | PASS | `ImportDicomDialog` / `LocalImportFactory.PAGE_CD` | not run | Two entry points |
| WP-3 local folder / ZIP password / Part-10 magic | PASS | `LoadLocalDicomTest` | not run | zip4j `char[]` |
| WP-3 Detect CD-ROM / copy-to-temp | PASS | `CdromDetectorTest`; `ImportDicomPage.copyLocal` | not run | Team A loop: detector walks injectable roots for DICOMDIR |
| WP-3 skip-unsupported SOP Information | PASS | `LoadLocalDicomTest.skipUnsupportedEncapsulatedPdf` | not run | |
| WP-3 patient join name **and** ID | PASS | `LoadLocalDicomTest.samePatientRequiresNameAndId` | N/A | |
| WP-3 series sort SERIES_COMPARATOR | PASS | `DicomSorterTest` | N/A | Have = tag v4.7.3 comparator (SR/DOC last) |
| WP-3 MX-10 concurrent series = 3 | PASS | `SeriesDownloadManagerTest` | N/A | One-sided cap |
| WP-3 MX-11 concurrent images in series = 4 | PASS | `SeriesDownloadManagerTest` | N/A | One-sided cap |
| WP-3 MX-12 thumbnail preempt | FAIL | none | N/A | WP-11 Q/R path |
| WP-3 Q/R DIMSE / DICOMweb | FAIL | none | N/A | WP-11 |
| WP-3 explorer TEXT/DATE/MODALITY filter | PARTIAL | `SeriesFilterTest` | not run | Filter modes exist; full tutorial chrome is GUI |
| WP-4 Done: 2D + LUT (W/L paints) | PASS | `View2dLutPaintTest` | not run | Op chain WindowAndPresets → Filter → PseudoColor → Shutter → Overlay → Affine |
| WP-4 overlays 60xx / shutter / padding / LOSSY | PASS | `View2dOverlayShutterLossyTest` | not run | |
| WP-4 `dcmview2d:*` (layout, mouseLeftAction `draw`, `--` before negatives, reset, scroll, synch, zoom magic −200/−100) | PASS | `DicomView2dCommandsTest` | not run | |
| WP-4 MX-07 monitor cal ≠ session Manual; Freeze Parameters ≠ Freeze Image | PASS | `View2dGraphicTest.monitorCalibrationIsNotSessionManual` | not run | Names distinct; GUI Pass later |
| WP-4 MX-21 no MPR in this WP | PASS | no MPR types in viewer2d | N/A | WP-7 |
| WP-4 zoom presets / Best Fit default | PASS | `View2dZoomTest` | not run | Centered on view |
| §4.3 docking / FoR+manual sync / lens / histogram layout | FAIL | none | not run | **WP-6** (not WP-4 FAIL) |
| §4.4 every graphic / GSPS / KO | FAIL | stubs only (`AngleToolGraphic.buildShape` null) | not run | **WP-5** |
| §5.2 `weasis:info -v` | PASS | smoke | N/A | |
| §5.2 Gogo 17179 | PASS | smoke | N/A | |
| §5.2 `dcmview2d:*` | PASS | `DicomView2dCommandsTest` | not run | |
| §5.1 `weasis://` / `$dicom:get` / `$dicom:rs` | FAIL | none | N/A | WP-13 |
| §6 tutorials (all) | FAIL | none | **not run** | Do **not** tick Pass. Headless ≠ Pass |

---

## Team A loop

1. Team B first pass: WP-3 Detect CD-ROM was **FAIL** (import CD page existed; no volume walk).
2. Team A fixed the **clone**: `CdromDetector` + `CdromDetectorTest` + Detect CD-ROM button on the DICOM CD page.
3. Re-SCORE WP-3 Detect CD-ROM → **PASS** (unit). GUI still **not run**.

No other WP-0–4 Done-criterion FAIL. Graphic geometry holes are **WP-5**, not a fake WP-1 Pass.

---

## Still PARTIAL (honest, out of WP-0–4 Done)

- No live GUI Pass. Dummy / W/L `BufferedImage` ≠ tutorial Pass.
- MigLayout / Docking Frames not on the Felix `lb` (WP-6).
- `weasis-dicom-sr` skipped @70 (WP-10).
- Measure/PR/KO, docking/sync/lens, MPR/fusion/3D, network, Dicomizer, protocol, i18n/native zip: remaining WPs.

§6 Pass boxes stay **empty**.
