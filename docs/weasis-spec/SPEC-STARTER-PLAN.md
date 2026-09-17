# Spec starter file plan (WP-0…WP-15)

Goal: one **main starter anchor** per work package so implementers open the right module/class first. Paths already exist for all 985 Weasis 4.7.3 `src/main/java` entries ([SOURCE-SURFACE.md](SOURCE-SURFACE.md)); starters document **entry points** and **next holes**, not new package names.

## Layout

| Starter doc | Primary Java entry (compile module) | Spec anchors |
|---|---|---|
| [starters/WP-00-launcher.md](starters/WP-00-launcher.md) | `weasis-launcher/.../AppLauncher.java` | WP-0, COMMANDS `weasis:info` |
| [starters/WP-01-base-ui.md](starters/WP-01-base-ui.md) | `weasis-base-ui/.../WeasisWin.java` | WP-1, SHORTCUTS, PREFERENCES |
| [starters/WP-02-codec-imageio.md](starters/WP-02-codec-imageio.md) | `weasis-dicom-codec/.../DicomImageElement.java` | WP-2 |
| [starters/WP-03-explorer.md](starters/WP-03-explorer.md) | `weasis-dicom-explorer/.../DicomExplorer.java` | WP-3, TUTORIALS import row |
| [starters/WP-04-viewer2d.md](starters/WP-04-viewer2d.md) | `weasis-dicom-viewer2d/.../View2d.java` | WP-4, TUTORIALS 2D row |
| [starters/WP-05-measure-ko-pr.md](starters/WP-05-measure-ko-pr.md) | `weasis-dicom-viewer2d/.../MeasureToolBar.java` | WP-5 |
| [starters/WP-06-docking.md](starters/WP-06-docking.md) | `weasis-core/.../DockableTool.java` + `ViewerToolBar` | WP-6 |
| [starters/WP-07-mpr.md](starters/WP-07-mpr.md) | `weasis-dicom-viewer3d/.../MprContainer.java` | WP-7 |
| [starters/WP-08-fusion.md](starters/WP-08-fusion.md) | `weasis-dicom-viewer2d/.../fusion/FusionController.java` | WP-8 |
| [starters/WP-09-vr.md](starters/WP-09-vr.md) | `weasis-dicom-3d/viewer3d/.../vr/View3d.java` | WP-9 |
| [starters/WP-10-special-sop.md](starters/WP-10-special-sop.md) | `weasis-dicom-sr/.../SRView.java` (+ au/wave/rt/seg bundles) | WP-10 |
| [starters/WP-11-network.md](starters/WP-11-network.md) | `weasis-dicom-send/.../DicomNodeDialog.java` | WP-11 |
| [starters/WP-12-dicomizer.md](starters/WP-12-dicomizer.md) | `weasis-acquire-explorer/.../AcquireExplorerFactory.java` | WP-12 |
| [starters/WP-13-protocol.md](starters/WP-13-protocol.md) | `weasis-dicom-explorer/.../DicomCommands.java` | WP-13, COMMANDS |
| [starters/WP-14-i18n-native.md](starters/WP-14-i18n-native.md) | `weasis-launcher/.../NativeDistribution.java` | WP-14 |
| [starters/WP-15-viewerhub.md](starters/WP-15-viewerhub.md) | `weasis-pacs-connector/.../ViewerHub.java` | WP-15 |

## Clone-only extras (documented in fixture)

See [fixtures/clone-only-main-java.txt](fixtures/clone-only-main-java.txt) — e.g. `DicomCommands.java`, `AcquirePatientCommand.java`, `ViewerHub.java`. Starters reference these where they are the clean-room entry for a WP.

## When to add new Java

Only when a starter hole needs **behavior** (not path coverage). After each new/edited Java file:

```bash
export JAVA_HOME=$HOME/tools/jdk-25
mvn -q -pl <module> compile
mvn -q test -pl <module>   # when tests exist
```

Keep touched method **CRAP &lt; 20** (team SCORE notes: CC from IDE/coverage tooling; split branches if needed).

## Phase

- **Now (tutorial backlog):** markdown starters under `docs/weasis-spec/starters/` (this commit).
- **After tutorial skips only:** optional `package-info.java` or small command facades only if WORK-PACKAGES names a missing command surface — still no nroduit body copy.
