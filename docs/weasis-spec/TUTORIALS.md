# Tutorials → work packages

Source: weasis.org **Tutorials** index (4.7) and GUI overview.

Default DICOM workspace: **Explorer left**, **viewers as tabs right**. Menus/toolbars follow the focused tab. One patient per multi-view tab; the same patient may occupy several tabs. Tabs dock/split (WP-6).

SOP routing (docs): MPR, MIP, 3D VR, ECG, SR, audio, PDF (system app), video (system player). Overlay: SEG, RT, PR/GSPS.

Non-DICOM workspace: `non-dicom-explorer.json`. Dicomizer is a separate workspace.

| Tutorial | WP | Have (clone) | Pass |
|---|---|---|---|
| GUI overview / prefs | 0–1 | Felix + dummy viewer | headed UI |
| DICOM Import / Explorer | 3 | DICOMDIR + filters + CD detect; File>Export files/ZIP/DICOMDIR; thumbnail multi-select; PatientPane/StudyPane hierarchy | tutorial |
| DICOM 2D / LUT / Zoom | 4 | W/L paints; zoom −200/−100; screenshot PNG/JPEG; T/W/S/Z/R/C/Q/P keys; series/image nav; Ctrl-drag accelerate; Alt+R/L/F; Ctrl+Space; Ctrl+/-; Tab layout; Lut/Reset chrome | tutorial |
| Draw & Measure | 5 | graphic geometry + shortcuts D/A/Y; select/delete map; ROI stats | tutorial |
| Build KO and PR | 5 | KO/PR objects with root UID 2.25; star/filter + K | tutorial |
| Docking / sync / 3D cursor / histogram / lens | 6 | docking start + FoR vs manual; histogram bind/RGB/ROI; 3D cursor H/PixelInfo; Mini Tool + panner; central-panel docking keys | tutorial |
| MPR / CPR / MIP | 7 | isotropic axes, MIP types; MPR Alt+X/C/V and Ctrl+Alt+B / Alt+wheel; Basic3DToolBar opens MPR | tutorial |
| Fusion / SUV | 8 | FusionController + SUVbw | tutorial |
| 3D VR | 9 | GL 3.3; refuse llvmpipe | tutorial |
| SR / AU / ECG / RT / SEG | 10 | factories + MIME; SR Content Sequence display; ECG millivolt leads | tutorial |
| Q/R, DICOMweb, send, ISO, print | 11 | DIMSE/QIDO/STOW Have tests; Q/R import chrome; send C-STORE/STOW chrome | tutorial |
| Dicomizer | 12 | acquire explorer/editor, Gogo 17181 smoke | tutorial |
| `weasis://` `$dicom:get` `$dicom:rs` | 13 | protocol parser | tutorial |
| i18n ≥30 %, native zip | 14 | fragment @13 + compressXZ zip | tutorial |
| ViewerHub / IID | 15 | `/display` 302 `weasis://` | tutorial |

Import sources: DnD, file association, Local Device, DICOMDIR, Q/R, commands. 4.7.0 skip-unsupported SOP **Information** popup (silence checkbox).
