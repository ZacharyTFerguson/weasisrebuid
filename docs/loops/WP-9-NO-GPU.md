# WP-9 — N/A: no GPU

**Clone:** `ZacharyTFerguson/weasisrebuid`  
**When:** 2026-09-11  
**Builder:** headless Cloud Agent (linux-x86-64). `GlGate.probe()` returns empty: no JOGL context, no `DISPLAY` GPU, `weasis.gl.renderer` unset.

## Verdict

| Have | Clone | Test | GUI |
|---|---|---|---|
| OpenGL 3.3+ gate | PASS (unit) | `Viewer3dHaveTest.refuseLlvmpipeAndRequireGl33` | N/A: no GPU |
| Refuse llvmpipe | PASS (unit) | same | N/A: no GPU |
| JOGL native fragment @121 | FAIL / skipped | smoke skip missing `jogamp-linux-x86-64` jar | N/A: no GPU |
| Live 3D VR paint | not run | none | **N/A: no GPU** |
| MX-15 active vs peer | PASS (unit) | `Viewer3dHaveTest.mx15ActiveAlwaysLocalPeersHonorMap` | not run |
| Rendering types + 18 cuts | PASS (unit) | `Viewer3dHaveTest.renderingPerspectiveCutAndMinImages` | not run |

CHECKLIST §6 **DICOM 3D Viewer** stays **unticked**. This is not Pass. It is **N/A: no GPU** for the live tutorial plus Have for the gates that can be unit-tested without a context.

Do not tick dicomlight boxes.
