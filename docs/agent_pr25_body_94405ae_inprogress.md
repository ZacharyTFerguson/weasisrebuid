<!-- CURSOR_AGENT_PR_BODY_BEGIN -->
## Status

Draft on `cursor/weasis-spec-rebuild-26c5`. **Do not merge.** **Do not tick Pass.**

Staffing until 2026-09-17T23:26Z: Grok writer + Composer verifier only.

**HEAD:** `94405ae` (`Show P ImagePrint live named img-print-state chrome.`). **Flip** `004f59d`, **Sharpen** `3844bd4`, **Window** `3091f5e`, **Crop** `f88ec68`, **Brightness** `e9ef165`, **AutoLevels** `18f9802`, **Mask** `3355cda`, **Shutter** `b00cd4d`, **DcmHeader Dump** `0d2f39f`, **DicomFieldsView** `eefe975`, **Export DICOM** `363f172`, **Help shortcuts/resources** `e88e780`, **Screenshot** `fc115d3`, **Import DICOM** `a569b23`, **Help About** `17df6d0`, **File Preferences** `fe8df27`, **Histogram** `9d00ffa`, **Mini Tool** `5497cf5`, **3D cursor H/PixelInfo** `e54747f`, **2D lens ZoomWin** `ceb0de9`, **FoR vs manual** `0e7a548`, **central-panel docking keys** `0d01de8`, **Display dock / InfoLayer** `22491cc`, **Display LayerItem / LayerAnnotation** `be35118`, **ImageRegionStatistics** `987649e`, **GraphicsPane** `3699b45`, **Key Object Star/Filter** `4bf92c6`, **KO/PR objects** `a781895`, **PrGraphicUtil** `9b9c9dc`, **InterpolatedPath2D** `f753214`, **Lut/Reset** `20b07af`, **Explorer filter** `267135c`, **VOILUTSequence / SIGMOID** / `name=voi-lut-state`, **select/delete map** / `name=sel-state`, **send C-STORE/STOW** / `name=send-state`, **DICOM Print** / `name=print-state`, **ISO writer** / `name=iso-state`, **Q/R import C-FIND / C-MOVE** / `name=qr-state`, **CalibrationView session mm/pixel ≠ monitor pitch** / `name=cal-state`, and **Freeze Parameters ≠ Freeze Image** / `name=freeze-state` headed PASS (prior chrome not re-scored). **P ImagePrint page ≠ printable** / `name=img-print-state` — Composer headed proof **in progress**. Full TUTORIALS.md walkthrough is still **not** Pass.





## Headed `94405ae` — P ImagePrint page ≠ printable / `name=img-print-state` (Composer)

**Jar:** `/tmp/weasis-headed-94405ae.jar` · **SHA:** `94405ae527d30c37f09fb77ba1b11101a9b17361` · **Jar file:** `18535cef7b7032be26d7dfa3d1b167770f296df225f5000b5de31c00a499fa3e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-94405ae-fresh`; prior headed JVMs killed; **did not reuse** Freeze/Calibration/Q/R/ISO/print/send/select-delete/VOI/filter/Reset/interp/apply-PR/KO jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`img-page`**, **`img-print`**, **`img-print-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`img-print-state`** `getText` expect `none`. Click **`img-page`** expect `img-print-state` `page` (PrintOptions showingAnnotations false; lastPrint raster non-null; no PrinterJob). Click **`img-print`** expect `img-print-state` `printable` (showingAnnotations true; lastPrint raster). Confirm `print-state` still `none` (do not click `print-film` / `print-action` — DICOM Print ≠ ImagePrint), `freeze-state` still `none` (do not click `freeze-params` / `freeze-image`), `cal-state` still `none` (do not click `cal-line` / `cal-apply`), `qr-state` still `none` (do not click `qr-find` / `qr-move`), `iso-state` still `none` (do not click `iso-write` / `iso-dicomdir`), `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow` / `print-film` / `print-action` / `iso-write` / `iso-dicomdir` / `qr-find` / `qr-move` / `cal-line` / `cal-apply` / `freeze-params` / `freeze-image`. Do **not** persist calling-AE preference keys. No optical burn / Print SCP / PACS I/O.

| Check | Stills-based result |
|---|---|
| WeasisWin `img-page` / `img-print` / `img-print-state` | **pending** — `name=img-print-state`; after Reed load `img-print-state` `getText` `none` |
| Page (annotations off) | **pending** — after `img-page`, `img-print-state` `page` (jattach `getText`, not OCR); lastPrint raster; showingAnnotations false; no PrinterJob |
| Printable ≠ page (annotations on) | **pending** — after `img-print`, `img-print-state` `printable`; showingAnnotations true; lastPrint raster |
| print-state / freeze-state / cal-state / qr-state / iso-state / send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **pending** — ImagePrint-only jattach; `print-state` still `none` (DICOM Print untouched); `freeze-state` still `none`; `cal-state` still `none`; `qr-state` still `none`; `iso-state` still `none`; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** Composer headed verify **in progress** on `94405ae`. **Next:** remaining TUTORIALS after P ImagePrint — **not** Pass.

Still path: `/opt/cursor/artifacts/headed_94405ae_imgprint.png`


## Headed `4dd370e` — Freeze Parameters ≠ Freeze Image / `name=freeze-state` (Composer)

**Jar:** `/tmp/weasis-headed-4dd370e.jar` · **SHA:** `4dd370ef3ffe2142a3898d0a75b40632c128d4bd` · **Jar file:** `0d81ef410155252380feea3e5d9a908251d34121d83ccbaf30ed8cc639968eca` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-4dd370e-fresh`; prior headed JVMs killed; **did not reuse** Calibration/Q/R/ISO/print/send/select-delete/VOI/filter/Reset/interp/apply-PR/KO jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`freeze-params`**, **`freeze-image`**, **`freeze-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`freeze-state`** `getText` expect `none`. Click **`freeze-params`** expect `freeze-state` `parameters` (freezeParameters true; freezeImage false). Click **`freeze-image`** expect `freeze-state` `image` (freezeImage true; freezeParameters stays true; MX-07 flags distinct). Confirm `cal-state` still `none` (do not click `cal-line` / `cal-apply`), `qr-state` still `none` (do not click `qr-find` / `qr-move`), `iso-state` still `none` (do not click `iso-write` / `iso-dicomdir`), `print-state` still `none` (do not click `print-film` / `print-action`), `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow` / `print-film` / `print-action` / `iso-write` / `iso-dicomdir` / `qr-find` / `qr-move` / `cal-line` / `cal-apply`. Do **not** persist calling-AE preference keys. No optical burn / Print SCP / PACS I/O.

| Check | Stills-based result |
|---|---|
| WeasisWin `freeze-params` / `freeze-image` / `freeze-state` | **OK** — `name=freeze-state`; after Reed load `freeze-state` `getText` `none` |
| Freeze Parameters | **OK** — after `freeze-params`, `freeze-state` `parameters` (jattach `getText`, not OCR); freezeImage still false |
| Freeze Image ≠ Freeze Parameters (MX-07) | **OK** — after `freeze-image`, `freeze-state` `image`; freezeParameters stays true |
| cal-state / qr-state / iso-state / print-state / send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — Freeze-only jattach; `cal-state` still `none`; `qr-state` still `none`; `iso-state` still `none`; `print-state` still `none`; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** Freeze Parameters ≠ Freeze Image / `freeze-state` **headed OK** on `4dd370e`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_4dd370e_freeze.png" width="720" />


## Headed `f8f66da` — CalibrationView session mm/pixel / `name=cal-state` (Composer)

**Jar:** `/tmp/weasis-headed-f8f66da.jar` · **SHA:** `f8f66daad0bd505be8389f241333bfdaaab4fd9b` · **Jar file:** `d200181530fc0a610c4049d781ce9276f693f029c8586265d74ca5c25c085557` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-f8f66da-fresh`; prior headed JVMs killed; **did not reuse** Q/R/ISO/print/send/select-delete/VOI/filter/Reset/interp/apply-PR/KO jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`cal-line`**, **`cal-apply`**, **`cal-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`cal-state`** `getText` expect `none`. Click **`cal-line`** expect `cal-state` `line` (10 px distance sample; known 20 mm; **do not** add explorer graphics). Click **`cal-apply`** expect `cal-state` `session` (session mm/pixel 2.0; monitor pitch stays 0.2; MX-07 session ≠ monitor). Confirm `qr-state` still `none` (do not click `qr-find` / `qr-move`), `iso-state` still `none` (do not click `iso-write` / `iso-dicomdir`), `print-state` still `none` (do not click `print-film` / `print-action`), `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow` / `print-film` / `print-action` / `iso-write` / `iso-dicomdir` / `qr-find` / `qr-move`. Do **not** persist calling-AE preference keys. No optical burn / Print SCP / PACS I/O.

| Check | Stills-based result |
|---|---|
| WeasisWin `cal-line` / `cal-apply` / `cal-state` | **OK** — `name=cal-state`; after Reed load `cal-state` `getText` `none` |
| Distance sample | **OK** — after `cal-line`, `cal-state` `line` (jattach `getText`, not OCR); graphics-count still `0` |
| Session mm/pixel ≠ monitor pitch (MX-07) | **OK** — after `cal-apply`, `cal-state` `session`; session 2.0 mm/pixel; monitor pitch 0.2 |
| qr-state / iso-state / print-state / send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — Calibration-only jattach; `qr-state` still `none`; `iso-state` still `none`; `print-state` still `none`; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** CalibrationView session mm/pixel / `cal-state` **headed OK** on `f8f66da`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_f8f66da_cal.png" width="720" />


## Headed `a17cff1` — Q/R import C-FIND / C-MOVE / `name=qr-state` (Composer)

**Jar:** `/tmp/weasis-headed-a17cff1.jar` · **SHA:** `a17cff1e0dc5ceceb7040926f457dcb62c1d596b` · **Jar file:** `dcf7afd202162ffd0caf837df27eec0ab0ac45b5cf10b6d9ea057f794aaeca2b` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-a17cff1-fresh`; prior headed JVMs killed; **did not reuse** ISO/print/send/select-delete/VOI/filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`qr-find`**, **`qr-move`**, **`qr-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`qr-state`** `getText` expect `none`. Click **`qr-find`** expect `qr-state` `C-FIND` (C-FIND keys; no PACS). Click **`qr-move`** expect `qr-state` `C-MOVE` (C-MOVE plan; no PACS). Confirm `iso-state` still `none` (do not click `iso-write` / `iso-dicomdir`), `print-state` still `none` (do not click `print-film` / `print-action`), `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow` / `print-film` / `print-action` / `iso-write` / `iso-dicomdir`. Do **not** persist calling-AE preference keys.

| Check | Stills-based result |
|---|---|
| WeasisWin `qr-find` / `qr-move` / `qr-state` | **OK** — `name=qr-state`; after Reed load `qr-state` `getText` `none` |
| C-FIND keys | **OK** — after `qr-find`, `qr-state` `C-FIND` (jattach `getText`, not OCR); no PACS |
| C-MOVE plan | **OK** — after `qr-move`, `qr-state` `C-MOVE`; no PACS |
| iso-state / print-state / send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — Q/R-only jattach; `iso-state` still `none`; `print-state` still `none`; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** Q/R import C-FIND / C-MOVE / `qr-state` **headed OK** on `a17cff1`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_a17cff1_qr.png" width="720" />


## Headed `693601d` — ISO writer manifest / DICOMDIR / `name=iso-state` (Composer)

**Jar:** `/tmp/weasis-headed-693601d.jar` · **SHA:** `693601d7960b7ad27104b8fa44a08604ecaf2915` · **Jar file:** `1ea9ba3b50b5877abdc430c494ea5c836449d68f06ef6efb84370e0a568396ad` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-693601d-fresh`; prior headed JVMs killed; **did not reuse** print/send/select-delete/VOI/filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`iso-write`**, **`iso-dicomdir`**, **`iso-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`iso-state`** `getText` expect `none`. Click **`iso-write`** expect `iso-state` `manifest` (local `.weasis-iso-manifest`; no optical burn). Click **`iso-dicomdir`** expect `iso-state` `DICOMDIR`. Confirm `print-state` still `none` (do not click `print-film` / `print-action`), `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow` / `print-film` / `print-action`.

| Check | Stills-based result |
|---|---|
| WeasisWin `iso-write` / `iso-dicomdir` / `iso-state` | **OK** — `name=iso-state`; after Reed load `iso-state` `getText` `none` |
| ISO writeManifest | **OK** — after `iso-write`, `iso-state` `manifest` (jattach `getText`, not OCR); no optical burn |
| DICOMDIR include | **OK** — after `iso-dicomdir`, `iso-state` `DICOMDIR` |
| print-state / send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — ISO-only jattach; `print-state` still `none`; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** ISO writer manifest / DICOMDIR / `iso-state` **headed OK** on `693601d`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_693601d_iso.png" width="720" />


## Headed `ea11210` — DICOM Print film-session / N-ACTION / `name=print-state` (Composer)

**Jar:** `/tmp/weasis-headed-ea11210.jar` · **SHA:** `ea112104f419012ac9c4a9515b924d3f2209f835` · **Jar file:** `b1da1e60566c8233ce8cba000876ec56f05c089209db62794a442229a8f2a196` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-ea11210-fresh`; prior headed JVMs killed; **did not reuse** send/select-delete/VOI/filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`print-film`**, **`print-action`**, **`print-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`print-state`** `getText` expect `none`. Click **`print-film`** expect `print-state` `film-session` (N-CREATE Film Session; no print SCP). Click **`print-action`** expect `print-state` `print` (N-ACTION Print type 1; no print SCP). Confirm `send-state` still `none` (do not click `send-cstore` / `send-stow`), `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic` / `send-cstore` / `send-stow`.

| Check | Stills-based result |
|---|---|
| WeasisWin `print-film` / `print-action` / `print-state` | **OK** — `name=print-state`; after Reed load `print-state` `getText` `none` |
| Film Session N-CREATE | **OK** — after `print-film`, `print-state` `film-session` (jattach `getText`, not OCR); no print SCP |
| N-ACTION Print type 1 | **OK** — after `print-action`, `print-state` `print`; no print SCP |
| send-state / sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — print-only jattach; `send-state` still `none`; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** DICOM Print film-session / N-ACTION / `print-state` **headed OK** on `ea11210`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_ea11210_print.png" width="720" />


## Headed `090fa16` — send C-STORE/STOW / `name=send-state` (Composer)

**Jar:** `/tmp/weasis-headed-090fa16.jar` · **SHA:** `090fa16d83f284d6f24de4119e8a6df1d02aadad` · **Jar file:** `a00a0cef375c013cbd437b46a046b360b70de85c8d5b25a0018bd947f7245f63` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-090fa16-fresh`; prior headed JVMs killed; **did not reuse** select-delete/VOI/filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`send-cstore`**, **`send-stow`**, **`send-state`** (WeasisWin north toolbar, live `name=`, not OCR). After load, **`send-state`** `getText` expect `none`. Click **`send-cstore`** expect `send-state` `C-STORE`. Click **`send-stow`** expect `send-state` `STOW-RS`. Confirm `sel-state` still `none` (do not click `sel-sample` / `select-graphic` / `delete-graphic`), `graphics-count` still `0` (do not click `measure-rect`), `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect` / `sel-sample` / `select-graphic` / `delete-graphic`.

| Check | Stills-based result |
|---|---|
| WeasisWin `send-cstore` / `send-stow` / `send-state` | **OK** — `name=send-state`; after Reed load `send-state` `getText` `none` |
| C-STORE | **OK** — after `send-cstore`, `send-state` `C-STORE` (jattach `getText`, not OCR) |
| STOW-RS | **OK** — after `send-stow`, `send-state` `STOW-RS` |
| sel-state / graphics-count / voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — send-only jattach; `sel-state` still `none`; `graphics-count` still `0`; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** send C-STORE/STOW / `send-state` **headed OK** on `090fa16`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_090fa16_send.png" width="720" />


## Headed `da1e703` — select/delete map / `name=sel-state` (Composer)

**Jar:** `/tmp/weasis-headed-da1e703.jar` · **SHA:** `da1e703bfbbdbb0d342e35d3a637b90039fe8c4a` · **Jar file:** `e1ed44243c750e6bd69ceece1e1c22168016f8d4ee04de69c8e4ce80c3c079fc` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-da1e703-fresh`; prior headed JVMs killed; **did not reuse** VOI/filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`sel-sample`**, **`select-graphic`**, **`delete-graphic`**, **`sel-state`** (GraphicsPane on ViewerToolBar, live `name=`, not OCR). After load, **`sel-state`** `getText` expect `none`; **`graphics-count`** `0`. Click **`sel-sample`** expect `graphics-count` `1` and `sel-state` `none`. Click **`select-graphic`** expect `sel-state` `selected`. Click **`delete-graphic`** expect `sel-state` `deleted` and `graphics-count` `0`. Confirm `voi-lut-state` still `none` (do not click `voi-table` / `voi-sigmoid`), `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `voi-table` / `voi-sigmoid` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| GraphicsPane `sel-sample` / `select-graphic` / `delete-graphic` / `sel-state` | **OK** — `name=sel-state`; after Reed load `sel-state` `getText` `none`; `graphics-count` `0` |
| Sample then select | **OK** — after `sel-sample`, `graphics-count` `1` and `sel-state` `none`; after `select-graphic`, `sel-state` `selected` (jattach `getText`, not OCR) |
| Delete | **OK** — after `delete-graphic`, `sel-state` `deleted` and `graphics-count` `0` |
| voi-lut-state / explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — select/delete-only jattach; `voi-lut-state` still `none`; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** select/delete map / `sel-state` **headed OK** on `da1e703`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_da1e703_sel.png" width="720" />


## Headed `0ae5248` — VOILUTSequence table + SIGMOID VOI / `name=voi-lut-state` (Composer)

**Jar:** `/tmp/weasis-headed-0ae5248.jar` · **SHA:** `0ae5248e8a58a0aa4c743dd152d3e7a21a6338f6` · **Jar file:** `8ec7292b13da24bf307c200d7ea8ad5d775524561e5a04d041281d1643172289` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-0ae5248-fresh`; prior headed JVMs killed; **did not reuse** filter/Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`voi-table`**, **`voi-sigmoid`**, **`voi-lut-state`** (Lut toolbar, live `name=`, not OCR). After load, **`voi-lut-state`** `getText` expect `none`. Click **`voi-table`** expect `voi-lut-state` `table`. Click **`voi-sigmoid`** expect `voi-lut-state` `SIGMOID`. Confirm `explorer-filter-hits` still named (do not type query), `reset-state` still `none` (do not click `reset-winLevel` / `reset-all`), `interp-shape` still `none`, `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `inverseLut` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Lut `voi-table` / `voi-sigmoid` / `voi-lut-state` | **OK** — `name=voi-lut-state`; after Reed load `voi-lut-state` `getText` `none` |
| VOILUTSequence table | **OK** — after `voi-table`, `voi-lut-state` `table` (jattach `getText`, not OCR) |
| SIGMOID VOI | **OK** — after `voi-sigmoid`, `voi-lut-state` `SIGMOID` |
| explorer-filter-hits / reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — VOI-only jattach; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `graphics-count` still `0`; `explorer-filter-hits` named; prior docks unchanged (not re-scored) |

**Route:** VOILUTSequence table + SIGMOID VOI / `voi-lut-state` **headed OK** on `0ae5248`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_0ae5248_voi.png" width="720" />


## Headed `267135c` — explorer filter TEXT/DATE/MODALITY / `name=explorer-filter-hits` (Composer)

**Jar:** `/tmp/weasis-headed-267135c.jar` · **SHA:** `267135c89ed9720b01f003afc05aa8baba54c12c` · **Jar file:** `3c58abc498c14548054ced4ade4456518253e4c02199be4a8ef4c44a99c028e2` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-267135c-fresh`; prior headed JVMs killed; **did not reuse** Reset/interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. DummyViewerPlugin **off**. jattach **`explorer-filter-mode`**, **`explorer-filter-query`**, **`explorer-filter-hits`** (explorer filter bar, live `name=`, not OCR). After load, default TEXT, blank query, **`explorer-filter-hits`** `getText` expect `1`. Set **`explorer-filter-query`** to `zzz` expect hits `0`. Set query `Reed` expect hits `1`. Set **`explorer-filter-mode`** `DATE`, query `zzz` expect `0`; clear query expect `1`. Set mode `MODALITY`, query `CT` expect `0`; query `DX` expect `1`. Confirm `reset-state` still `none` (do not click `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation`), `interp-shape` still `none` (do not click `apply-interp`), `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / `reset-winLevel` / `reset-all` / `reset-zoom` / `reset-rotation` / Zoom `2x` / `90°` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Explorer `explorer-filter-mode` / `explorer-filter-query` / `explorer-filter-hits` | **OK** — `name=explorer-filter-hits`; after Reed load `explorer-filter-hits` `getText` `1`; mode TEXT |
| TEXT miss then Reed | **OK** — query `zzz` → hits `0`; query `Reed` → hits `1` (jattach `getText`, not OCR) |
| DATE miss then blank | **OK** — mode `DATE`; query `zzz` → hits `0`; blank query → hits `1` |
| MODALITY CT then DX | **OK** — mode `MODALITY`; query `CT` → hits `0`; query `DX` → hits `1` |
| reset-state / interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — filter-only jattach; `reset-state` still `none`; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `graphics-count` still `0`; prior docks unchanged (not re-scored) |

**Route:** Explorer filter TEXT/DATE/MODALITY / `explorer-filter-hits` **headed OK** on `267135c`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_267135c_filter.png" width="720" />


## Headed `20b07af` — Lut/Reset chrome / `name=reset-state` (Composer)

**Jar:** `/tmp/weasis-headed-20b07af.jar` · **SHA:** `20b07afdc68df8d8e184ac86c80070fb550b566e` · **Jar file:** `2c88fe6bf36894c0da91b50ab5066777f1b8e8558209b0f238e047ac2c67694e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-20b07af-fresh`; prior headed JVMs killed; **did not reuse** interp/apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach **`reset`** (Reset toolbar, live `name=`, not OCR). Read **`reset-state`** `getText` expect `none`. Click **`reset-winLevel`** expect `reset-state` `winLevel`. Click **`reset-all`** expect `reset-state` `-a`. Confirm `interp-shape` still `none` (do not click `apply-interp`), `pr-mapped` still `none`, `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `apply-interp` / Zoom `2x` / `90°` / `reset-zoom` / `reset-rotation` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Toolbar `reset` / `reset-winLevel` / `reset-all` / `reset-state` | **OK** — `name=reset`; after Reed load `reset-state` `getText` `none` |
| `dcmview2d:reset` winLevel then `-a` | **OK** — after `reset-winLevel`, `reset-state` `winLevel`; after `reset-all`, `reset-state` `-a` (jattach `getText`, not OCR) |
| interp-shape / pr-mapped / ko-uid / pr-uid / ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — Reset-only jattach; `interp-shape` still `none`; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `graphics-count` still `0`; prior docks unchanged (not re-scored) |

**Route:** Lut/Reset chrome / `reset-state` **headed OK** on `20b07af`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_20b07af_reset.png" width="720" />


## Headed `f753214` — InterpolatedPath2D INTERPOLATED spline / `name=interp-shape` (Composer)

**Jar:** `/tmp/weasis-headed-f753214.jar` · **SHA:** `f753214c77cec2980747055d5c4bd463b5ff7f61` · **Jar file:** `38fe77124cfe72ca0330d83a63581dcb59280d92797d3bcf8702b6c32e647680` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-f753214-fresh`; prior headed JVMs killed; **did not reuse** apply-PR/KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach **`apply-interp`** (explorer filter bar, live `name=`, not OCR). Read **`interp-shape`** `getText` expect `none`. Read **`interp-through`** expect `none`. Click **`apply-interp`** expect `interp-shape` `InterpolatedPath2D` and `interp-through` `through`. Confirm `pr-mapped` still `none` (do not click `apply-pr`), `ko-uid` / `pr-uid` still `none`, `ko-state` still `none`, `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `star` / `ko-filter` / `save-ko` / `save-pr` / `apply-pr` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Explorer `apply-interp` / `interp-shape` / `interp-through` | **OK** — `name=apply-interp`; after Reed load `interp-shape` / `interp-through` `getText` `none` |
| GSPS INTERPOLATED Catmull-Rom through controls | **OK** — after `apply-interp`, `interp-shape` `InterpolatedPath2D` and `interp-through` `through` (jattach `getText`, not OCR) |
| pr-mapped / ko-uid / pr-uid / ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — InterpolatedPath2D-only jattach; `pr-mapped` still `none`; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `graphics-count` still `0`; `region-stats` named; `display-visibility-value` `FULL`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** InterpolatedPath2D INTERPOLATED spline / `interp-shape` **headed OK** on `f753214`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_f753214_interp.png" width="720" />


## Headed `9b9c9dc` — PrGraphicUtil GSPS → Weasis Graphic / `name=pr-mapped` (Composer)

**Jar:** `/tmp/weasis-headed-9b9c9dc.jar` · **SHA:** `9b9c9dc13568fc7bfcf790520870e678361515da` · **Jar file:** `f6a85aa1c7fca906ffad969871e5113e728d45ccf45c9ca8aa7e3d187bfc6a24` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-9b9c9dc-fresh`; prior headed JVMs killed; **did not reuse** KO-PR/KO-Star/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach **`apply-pr`** (explorer filter bar, live `name=`, not OCR). Read **`pr-graphics`** `getText` expect `0`. Read **`pr-mapped`** expect `none`. Click **`apply-pr`** expect `pr-graphics` `1` and `pr-mapped` `LineGraphic`. Confirm `ko-uid` / `pr-uid` still `none` (do not click `save-ko` / `save-pr`), `ko-state` still `none` (do not click `star` / `ko-filter`), `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `star` / `ko-filter` / `save-ko` / `save-pr` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Explorer `apply-pr` / `pr-graphics` / `pr-mapped` | **OK** — `name=apply-pr`; after Reed load `pr-graphics` `getText` `0` and `pr-mapped` `none` |
| GSPS POLYLINE → Weasis Graphic | **OK** — after `apply-pr`, `pr-graphics` `1` and `pr-mapped` `LineGraphic` (jattach `getText`, not OCR) |
| ko-uid / pr-uid / ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — PrGraphicUtil-only jattach; `ko-uid` / `pr-uid` still `none`; `ko-state` still `none`; `graphics-count` still `0`; `region-stats` named; `display-visibility-value` `FULL`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** PrGraphicUtil GSPS → Weasis Graphic / `pr-mapped` **headed OK** on `9b9c9dc`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_9b9c9dc_pr.png" width="720" />


## Headed `a781895` — KO/PR objects / `name=ko-uid` / `name=pr-uid` (Composer)

**Jar:** `/tmp/weasis-headed-a781895.jar` · **SHA:** `a781895ee0172066ce5878ce7c5969402236fac7` · **Jar file:** `a259c6e7ee519b1eaa4c06e239608abda4ab3cf6ff61cb7ab8a6c576f31f137e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-a781895-fresh`; prior headed JVMs killed; **did not reuse** KO/GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach **`key-object`**. Read **`ko-uid`** `getText` (not OCR) expect `none`. Read **`pr-uid`** expect `none`. Click **`save-ko`** expect `ko-uid` starts with `2.25`. Click **`save-pr`** expect `pr-uid` starts with `2.25`. Confirm `ko-state` still `none` (do not click `star` / `ko-filter`), `graphics-count` still `0`, `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `star` / `ko-filter` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Toolbar `save-ko` / `save-pr` / `ko-uid` / `pr-uid` | **OK** — `name=save-ko` / `save-pr`; after Reed load `ko-uid` / `pr-uid` `getText` `none` |
| KO SOP UID root 2.25 | **OK** — after `save-ko`, `ko-uid` `getText` `2.25.335546465642202192552943960517963652814` |
| PR SOP UID root 2.25 | **OK** — after `save-pr`, `pr-uid` `getText` `2.25.98027046273967338659257370496426740844` |
| ko-state / graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — KO/PR-uid-only jattach; `ko-state` still `none`; `graphics-count` still `0`; `region-stats` named; `display-visibility-value` `FULL`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** KO/PR objects / `ko-uid` / `pr-uid` **headed OK** on `a781895`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_a781895_kopr.png" width="720" />


## Headed `4bf92c6` — Key Object Star/Filter / `name=ko-state` (Composer)

**Jar:** `/tmp/weasis-headed-4bf92c6.jar` · **SHA:** `4bf92c61f730ace706be09bd020c9ef0669723ce` · **Jar file:** `079ea2e02ce21eca57cacd4dd0aa049b54909229e318f560bcede1d256d40beb` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-4bf92c6-fresh`; prior headed JVMs killed; **did not reuse** GraphicsPane/region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach **`key-object`**. Read **`ko-state`** `getText` (not OCR) expect `none`. Click **`star`** (not Lut Sharpen `filter`) expect `starred`. Click **`ko-filter`** expect `filtered`. Click **`ko-filter`** restore `starred`. Click **`star`** restore `none`. Confirm `graphics-count` still `0` (do not draw G), `region-stats` named, `display-visibility-value` `FULL`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click Lut Sharpen `filter` / `display-full` / `display-minimal` / `display-hidden` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields / Measure D/A/Y / `measure-rect`.

| Check | Stills-based result |
|---|---|
| Toolbar `key-object` / `star` / `ko-filter` / `ko-state` | **OK** — `name=key-object`; after Reed load `ko-state` `getText` `none` |
| Star / Filter cycle | **OK** — `star` → `starred`; `ko-filter` → `filtered`; `ko-filter` off → `starred`; `star` off → `none` (jattach `getText`, not OCR) |
| `star` vs Lut `filter` distinct | **OK** — `ko-filter` ≠ Lut `name=filter`; Lut Sharpen not clicked |
| graphics-count / region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — KO-only jattach; `graphics-count` still `0`; `region-stats` named; `display-visibility-value` `FULL`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** Key Object Star/Filter / `ko-state` **headed OK** on `4bf92c6`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_4bf92c6_ko.png" width="720" />


## Headed `3699b45` — GraphicsPane / `name=graphics-count` (Composer)

**Jar:** `/tmp/weasis-headed-3699b45.jar` · **SHA:** `3699b45445aea277dc078273d42f935cae0e9d0f` · **Jar file:** `322f2011ba8028de48c21ac622daf23a2c2583915e2b6fedb401999b5722b180` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-3699b45-fresh`; prior headed JVMs killed; **did not reuse** region-stats/LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach Viewer **`graphics-pane`**. Read **`graphics-count`** `getText` (not OCR) expect `0`. Click **`measure-rect`** (G) and click-drag a closed drawing on the bound View2d. Read **`graphics-count`** expect `1`. Confirm **`region-stats`** still named, **`display-visibility-value`** still `FULL`, **`display-layers-value`** still includes `CROSSLINES`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click `display-full` / `display-minimal` / `display-hidden` / `display-layer-crosslines` / `display-ann-patient` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields. Do **not** re-score region-stats `n=` or Measure D/A/Y.

| Check | Stills-based result |
|---|---|
| Viewer `graphics-pane` / `graphics-count` | **OK** — `name=graphics-pane`; after Reed load `graphics-count` `getText` `0` |
| Bound drawing count after G | **OK** — after `measure-rect` (live bounds `727,408,40×32`) click-drag, `graphics-count` `getText` `1` |
| region-stats / LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — GraphicsPane-only jattach; `region-stats` still named; `display-visibility-value` `FULL`; `display-layers-value` includes `CROSSLINES`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** GraphicsPane / `graphics-count` **headed OK** on `3699b45`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_3699b45_graphics.png" width="720" />


## Headed `987649e` — ImageRegionStatistics / `name=region-stats` (Composer)

**Jar:** `/tmp/weasis-headed-987649e.jar` · **SHA:** `987649ef2b1052b38802e31a423f28b5c408bdc2` · **Jar file:** `ec8e315fc8399bbf238a9492445d67abe58ae45e3e2082e1c6040ee6c5cab3b1` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-987649e-fresh`; prior headed JVMs killed; **did not reuse** LayerItem/Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach Viewer **`region-stats`** (next to `pixel-info`). Read **`region-stats`** `getText` (not OCR) expect whole-image `n=4257303` plus `min=` / `max=` / `mean=` / `stdev=`. Click **`measure-rect`** (G) and click-drag a closed ROI on the bound View2d. Read **`region-stats`** again: `n=` smaller than `4257303`, still includes `min=` / `max=` / `mean=` / `stdev=`. Confirm **`display-visibility-value`** still `FULL`, **`display-layers-value`** still includes `CROSSLINES`, `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click `display-full` / `display-minimal` / `display-hidden` / `display-layer-crosslines` / `display-ann-patient` / `window-close` / docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields. Do **not** re-score Measure D/A/Y.

| Check | Stills-based result |
|---|---|
| Viewer `region-stats` whole image | **OK** — `name=region-stats`; after Reed load `getText` `n=4257303` with `min=` / `max=` / `mean=` / `stdev=` |
| Selected closed graphic (G ROI) | **OK** — after `measure-rect` (live bounds `727,408,40×32`) click-drag, `region-stats` `n=1548781` (< `4257303`) with `min=` / `max=` / `mean=` / `stdev=` |
| LayerItem / Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — region-stats-only jattach; `display-visibility-value` `FULL`; `display-layers-value` includes `CROSSLINES`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** ImageRegionStatistics / `region-stats` **headed OK** on `987649e`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_987649e_region.png" width="720" />


## Headed `be35118` — Display LayerItem / LayerAnnotation / `name=display-layers-value` (Composer)

**Jar:** `/tmp/weasis-headed-be35118.jar` · **SHA:** `be351187a1940a0e094f79f721b79b36977f1f1d` · **Jar file:** `91c8e065060797d8328fa2e8cfbd95b2199c805c0729b3fc49a74ace8619d1b1` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-be35118-fresh`; prior headed JVMs killed; **did not reuse** Display/docking/FoR/Lens/PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach north **`display`**. **`display-layers-value`** `IMAGE,CROSSLINES,ANNOTATION,DRAW,MEASURE` → toggle **`display-layer-crosslines`** → `IMAGE,ANNOTATION,DRAW,MEASURE` → restore CROSSLINES. **`display-ann-value`** `patient,study,series,windowLevel,orientation` → **`display-ann-patient`** → `study,series,windowLevel,orientation` → restore patient. **`display-visibility-value`** stays `FULL`. `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged. Do **not** click visibility 3-state / docking keys / lens / etc.

| Check | Stills-based result |
|---|---|
| Dock `display` / `display-layers-value` | **OK** — default `IMAGE,CROSSLINES,ANNOTATION,DRAW,MEASURE`; off/on crosslines via `display-layer-crosslines` |
| `display-ann-value` / `display-ann-patient` | **OK** — patient toggled off/on in ann-value `getText` |
| Display 3-state / docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — `display-visibility-value` `FULL`; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** Display LayerItem / LayerAnnotation **headed OK** on `be35118`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_be35118_layers.png" width="720" />


## Headed `22491cc` — Display dock / InfoLayer / `name=display-visibility-value` (Composer)

**Jar:** `/tmp/weasis-headed-22491cc.jar` · **SHA:** `22491ccbae194cd159a5f3831131a9e774d7fcbf` · **Jar file:** `320c5b6b4fd7c533da38abc7883829768b898280e7f41b28b89eaf3735ce825f` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-22491cc-fresh`; prior headed JVMs killed; **did not reuse** docking/FoR/Lens/PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach north-of-viewer **`display`**. Read **`display-visibility-value`** `getText` `FULL` → click **`display-minimal`** `MINIMAL` → **`display-hidden`** `HIDDEN` → **`display-full`** `FULL`. Confirm `docking-state` `NORMAL`, `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click docking keys / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Dock `display` / Display | **OK** — `name=display` north of DICOM 2D; buttons `display-full` / `display-minimal` / `display-hidden` |
| `display-visibility-value` `getText` | **OK** — `FULL` → `MINIMAL` → `HIDDEN` → `FULL` (jattach `getText`, not OCR) |
| Docking keys / FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — Display-only jattach; `docking-state` `NORMAL`; `synch-kind` `FoR`; docks unchanged |

**Route:** Display dock / InfoLayer **headed OK** on `22491cc`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_22491cc_display.png" width="720" />


## Headed `0d01de8` — central-panel docking keys / `name=docking-state` (Composer)

**Jar:** `/tmp/weasis-headed-0d01de8.jar` · **SHA:** `0d01de84167410dfc22cb110fc76055cf45cd86e` · **Jar file:** `2419e15de4ae697b518edaeea911c099179482b0339da2ce0dafacb1854a2695` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-0d01de8-fresh`; prior headed JVMs killed; **did not reuse** FoR/Lens/PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. Read **`docking-state`** `getText` `NORMAL` → **`window-maximize`** `MAXIMIZED` → **`window-normalize`** `NORMAL` → **`window-externalize`** `EXTERNALIZED` → **`window-normalize`** `NORMAL`. **`window-docking-list`** → dialog **`docking-list`**; **`docking-list-items`** `DICOM 2D`; close **`docking-list-close`**. Confirm `synch-kind` `FoR`, `lens` east, `mini-tool` west, `histogram` south, `pixel-info` present. Do **not** click `window-close` / lens / `synch-manual` / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Toolbar `window-maximize` / `window-normalize` / `window-externalize` | **OK** — `docking-state` `NORMAL` → `MAXIMIZED` → `NORMAL` → `EXTERNALIZED` → `NORMAL` (jattach `getText`) |
| Dialog `docking-list` | **OK** — title `Docking List`; `docking-list-items` `getText` `DICOM 2D`; close `docking-list-close` |
| FoR / Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — docking-keys-only jattach; `synch-kind` `FoR`; docks unchanged |

**Route:** Central-panel docking keys **headed OK** on `0d01de8`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_0d01de8_docking.png" width="720" />


## Headed `0e7a548` — FoR vs manual / `name=synch-kind` (Composer)

**Jar:** `/tmp/weasis-headed-0e7a548.jar` · **SHA:** `0e7a548dd9cc44feea9142e2bbaca0bef00d14b5` · **Jar file:** `52a20608a154061f6ff8bfca78adc3fa75369fff0928b21a686aeca07d7f9748` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-0e7a548-fresh`; prior headed JVMs killed; **did not reuse** Lens/PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach Viewer **`synch-kind`** (initial `FoR`). Click **`synch-manual`** → `Manual`. Click **`synch-for`** → `FoR`. Confirm `lens` east, `mini-tool` west, `histogram` south, `pixel-info` on Viewer. Do **not** click lens / `crosshair` / Mini Tool / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Viewer `synch-for` / `synch-manual` | **OK** — `name=synch-for` / `name=synch-manual`; manual selected after click |
| `synch-kind` `getText` | **OK** — `FoR` → `Manual` → `FoR` (jattach `getText`, not OCR) |
| Lens east / Mini Tool west / Histogram south / PixelInfo untouched | **OK** — FoR/manual-only jattach; docks unchanged |

**Route:** FoR vs manual **headed OK** on `0e7a548`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_0e7a548_synch.png" width="720" />


## Headed `ceb0de9` — 2D lens ZoomWin / `name=lens` (Composer)

**Jar:** `/tmp/weasis-headed-ceb0de9.jar` · **SHA:** `ceb0de9a48ed75e03b1cfa35eab9d2aa93bf3869` · **Jar file:** `0d63d468ba1ffa6c238c3e467fe9d99f0abd4d81c50cb36a40f3728ee7aae445` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-ceb0de9-fresh`; prior headed JVMs killed; **did not reuse** PixelInfo/Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach east-of-viewer **`lens`**. Read **`lens-factor-value`** `getText` (not OCR). Set **`lens-factor`** to `400`. Confirm Mini Tool still west, Histogram still south, and `pixel-info` still on Viewer. Do **not** click Viewer `crosshair` / Mini Tool sliders / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Dock `lens` / Lens | **OK** — `name=lens` east of DICOM 2D; slider `lens-factor`; preview `lens-panel` |
| Factor `lens-factor` → `4x` | **OK** — `lens-factor-value` `getText` `2x` default; `4x` after jattach set `lens-factor` 400 |
| PixelInfo / Mini Tool west / Histogram south untouched | **OK** — Lens-only jattach; `mini-tool` west; `histogram` south; `pixel-info` present |

**Route:** 2D lens ZoomWin **headed OK** on `ceb0de9`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_ceb0de9_lens.png" width="720" />


## Headed `e54747f` — 3D cursor H/PixelInfo / `name=pixel-info` (Composer)

**Jar:** `/tmp/weasis-headed-e54747f.jar` · **SHA:** `e54747f2a9494ab576031c37863f1853e625585c` · **Jar file:** `63e9e3a572dd1eb8f583c2aa58204c257459ad6a71967cd7c6e411aab23a4b89` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-e54747f-fresh`; prior headed JVMs killed; **did not reuse** Mini Tool/Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach Viewer **`crosshair`** (H). Click the bound View2d. Read **`pixel-info`** `getText` (not OCR). Confirm Mini Tool still west and Histogram still south. Do **not** click Mini Tool sliders / Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Viewer `crosshair` | **OK** — `name=crosshair` selected; `mouse_left=crosshair` after jattach click |
| `pixel-info` `getText` | **OK** — `964,1097 v=179 HU=179.0` after View2d click on Reed |
| Mini Tool west / Histogram south untouched | **OK** — PixelInfo-only jattach; `mini-tool` west of view; `histogram` south of `mini-tool` |

**Route:** 3D cursor H/PixelInfo **headed OK** on `e54747f`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_e54747f_pixelinfo.png" width="720" />


## Headed `5497cf5` — Mini Tool / `name=mini-tool` (Composer)

**Jar:** `/tmp/weasis-headed-5497cf5.jar` · **SHA:** `5497cf5006edd0f9bf241d197f5e66b041588d2d` · **Jar file:** `0c3b6667f0292b3417c2a86ce02008a6655bd33f73f92e342e16d0fe1cef7265` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-5497cf5-fresh`; prior headed JVMs killed; **did not reuse** Histogram/Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach west-of-viewer **`mini-tool`**. Read **`mini-zoom-value`** `getText`. Set **`mini-zoom`** to `200`. Confirm **`mini-panner`** / **`mini-rotation`**. Do **not** click Histogram RGB / Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Dock `mini-tool` / Mini Tool | **OK** — `name=mini-tool` west of DICOM 2D; sliders `mini-zoom` / `mini-rotation` / `mini-series` |
| Zoom `mini-zoom` → `200%` | **OK** — `mini-zoom-value` `getText` `200%` after jattach set `mini-zoom` 200 |
| Panner `mini-panner` | **OK** — `name=mini-panner` present |
| Histogram / Preferences / About / Import / Screenshot / Export / ImageTool / Dump / Fields untouched | **OK** — Mini Tool-only jattach; `histogram` south (`histogram-channels` not toggled) |

**Route:** Mini Tool **headed OK** on `5497cf5`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_5497cf5_minitool.png" width="720" />

## Headed `9d00ffa` — Histogram dock / `name=histogram` (Composer)

**Jar:** `/tmp/weasis-headed-9d00ffa.jar` · **SHA:** `9d00ffab036db2a4d4616978ac968f1892cad512` · **Jar file:** `5a0ff8333124f3366c35d28cd22b93514cbd22c1816506492793b21af6e66603` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-9d00ffa-fresh`; prior headed JVMs killed; **did not reuse** Preferences/About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D**. jattach south-of-viewer **`histogram`**. Read **`histogram-stats`** `getText` (not OCR). Click **`histogram-rgb`**. Confirm **`histogram-gray`** / **`histogram-hsv`** / **`histogram-hls`**. Do **not** click Preferences / About / Import / Screenshot / Help / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Dock `histogram` / Histogram | **OK** — `name=histogram` south of DICOM 2D; panel `histogram-panel` |
| Stats `histogram-stats` | **OK** — `getText` `n=4257303 min=0.0 max=255.0 mean=132.0 peak=49126` |
| RGB chrome `histogram-rgb` | **OK** — click RGB; `histogram-channels` visible; `histogram-gray` / `histogram-hsv` / `histogram-hls` present |
| Preferences / About / Import / Screenshot / Export / ImageTool / Dump / Fields untouched | **OK** — Histogram-only jattach |

**Route:** Histogram dock **headed OK** on `9d00ffa`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_9d00ffa_histogram.png" width="720" />

## Headed `fe8df27` — File Preferences / `name=file-preferences` (Composer)

**Jar:** `/tmp/weasis-headed-fe8df27.jar` · **SHA:** `fe8df27b039459920f21c9dd01d5aeefb10387aa` · **Jar file:** `4c78ac725540814e891d42f4383883d433df9d7c13d9aaedd35a50497002c842` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-fe8df27-fresh`; prior headed JVMs killed; **did not reuse** About/Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** Menu **File** → jattach **`file-preferences`** (`Preferences`, Alt+P) → dialog **`preferences`**. Read **`pref-tree`** (General first). Confirm **`pref-ok`**, **`pref-cancel`**, **`pref-reset`**. Close **`pref-cancel`**. Do **not** click Import / Screenshot / Help About / Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| File `file-preferences` / `Preferences` | **OK** — dialog title `Preferences`; `name=preferences` |
| Tree `pref-tree` | **OK** — `General` first (catalog tree via jattach `getText`, not OCR) |
| Buttons `pref-ok` / `pref-cancel` / `pref-reset` | **OK** — `OK` / `Cancel` / `Restore defaults`; close `pref-cancel` |
| About / Import / Screenshot / Export / ImageTool / Dump / Fields untouched | **OK** — Preferences-only jattach |

**Route:** File Preferences **headed OK** on `fe8df27`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_fe8df27_preferences.png" width="720" />

## Headed `17df6d0` — Help About / `name=help-about` (Composer)

**Jar:** `/tmp/weasis-headed-17df6d0.jar` · **SHA:** `17df6d0f740694b15d5b7f6033d4bd12b2909e98` · **Jar file:** `b223cab8fc3463f94f4d71b0cdfb23c65bbe426298eb610fa01cbb5cb8faeed9` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-17df6d0-fresh`; prior headed JVMs killed; **did not reuse** Import/Screenshot/Help shortcuts/Export/Fields/Dump/ImageTool jars.

**Flow:** Menu **Help** → jattach **`help-about`** (`About`) → dialog **`about`**. Read **`about-version`** `getText` (not OCR) and **`about-note`**. Close **`help-about-close`**. Do **not** click Import / Screenshot / Export / ImageTool / `dumpHeader` / Fields / Keyboard Shortcuts / System resources.

| Check | Stills-based result |
|---|---|
| Help `help-about` / `About` | **OK** — dialog title `About Weasis`; `name=about` |
| Version `about-version` | **OK** — `getText` `Weasis 4.7.3` |
| Note `about-note` | **OK** — `Clean-room rebuild`; close `help-about-close` |
| Import / Screenshot / Export / ImageTool / Dump / Fields untouched | **OK** — About-only jattach; no Import/Screenshot/Export/`dumpHeader`/Fields/Keyboard Shortcuts/System resources clicks |

**Route:** Help About **headed OK** on `17df6d0`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_17df6d0_about.png" width="720" />

## Headed `a569b23` — File/toolbar Import DICOM / `name=import-dicom` (Composer)

**Jar:** `/tmp/weasis-headed-a569b23.jar` · **SHA:** `a569b23716c8dba6c189e00c623088155d568947` · **Jar file:** `32c308b950e5a7e069ad9c1863b568c89145aeb35947e4958009453eae7f1d5e` · **Gogo:** `17279` · OSGi cache `/tmp/weasis-headed-cache-a569b23-fresh6`; prior headed JVMs killed; **did not reuse** Screenshot/Help/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:close -a` if a series is already open. jattach toolbar **`import-dicom`** (next to `export-dicom`) or File **`file-import-dicom`**. Dialog **`import-dicom-dialog`** title `Import DICOM` with **`import-tabs`** (DICOM / ZIP / DICOMDIR). Type `/opt/cursor/artifacts/chest_pa.dcm` in **`import-path`**. Click **`import-run`**. Read **`import-status`** `getText`. Explorer should show Reed. Do **not** click Screenshot / Export / Help / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Toolbar `import-dicom` / File `file-import-dicom` | **OK** — jattach `name=import-dicom` opens `import-dicom-dialog` title `Import DICOM` |
| Tabs DICOM / ZIP / DICOMDIR | **OK** — `import-tabs` titles **DICOM** / **ZIP** / **DICOMDIR** |
| `import-run` loads chest; `import-status` Imported | **OK** — `Imported 1 instance(s)`; `explorer-series` **Reed^Alice / DX #0 PA chest** |
| Screenshot / Help / Export / ImageTool / Dump / Fields untouched | **OK** — raster **1929×2207** after import; no Screenshot/Help/Export/`dumpHeader`/Fields clicks |

**Route:** Import DICOM **headed OK** on `a569b23`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_a569b23_dialog.png" width="720" />
<img src="/opt/cursor/artifacts/headed_a569b23_imported.png" width="720" />

## Headed `fc115d3` — Screenshot toolbar PNG/JPEG / `name=screenshot` (Composer)

**Jar:** `/tmp/weasis-headed-fc115d3.jar` · **SHA:** `fc115d3e8c18b6016657b86f62128f4445c934f4` · **Jar file:** `4ab1205471b0bdb1b55717d4d1e971e00fe2e5b22981628bea5af034ff934e5a` · **Gogo:** `17279` · OSGi cache `/tmp/weasis-headed-cache-fc115d3-fresh6`; prior headed JVMs killed; **did not reuse** Help/Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D** → jattach toolbar **`screenshot`** (`Screenshot` bar, after Dump) → dialog **`screenshot-dialog`**. Set **`screenshot-scope`** to `NATIVE_PIXELS`. Type dest in **`screenshot-path`** (e.g. `/tmp/weasis-shot-reed.png`). Click **`screenshot-run`**. Read **`screenshot-status`** `getText`. Do **not** click Export / Help / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Toolbar `screenshot` | **OK** — jattach `name=screenshot` opens `screenshot-dialog` title `Screenshot` |
| Format / scope chrome | **OK** — `screenshot-format` **PNG**; `screenshot-scope` **NATIVE_PIXELS** after set |
| `screenshot-run` writes dest; `screenshot-status` Saved | **OK** — `Saved /tmp/weasis-shot-reed-1789610734.png`; PNG **1929×2207** native |
| Help / Export / ImageTool / Dump / Fields untouched | **OK** — viewer raster **1929×2207**; no Help/Export/`dumpHeader`/Fields clicks |

**Route:** Screenshot **headed OK** on `fc115d3`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_fc115d3_dialog.png" width="720" />
<img src="/opt/cursor/artifacts/headed_fc115d3_saved.png" width="720" />

## Headed `e88e780` — Help Keyboard Shortcuts / System resources (Composer)

**Jar:** `/tmp/weasis-headed-e88e780.jar` · **SHA:** `e88e780d3f4d11ae76d4b2917c18f8b7afa4856d` · **product:** `dd74bf4b33586ac518f441cb0e152d5ade6d8674` · **Jar file:** `11fc85fe4db0aa30d2a175cab57cd9fe6dd520c6f411fdc2ded86d3f79668301` · **Gogo:** `17279` · OSGi cache `/tmp/weasis-headed-cache-e88e780-fresh4`; prior headed JVMs killed; **did not reuse** Export/Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D** (optional; Help does not need pixels). Menu **Help** → jattach **`help-keyboard-shortcuts`** (`Keyboard Shortcuts`) → dialog **`keyboard-shortcuts`** / map **`keyboard-shortcuts-map`**. Read **`shortcut-table`** via jattach `getText` (not OCR) and **`shortcut-rows`**. Close **`help-shortcuts-close`**. Then Help → jattach **`help-system-resources`** (`System resources`) → dialog **`system-resources`**. Read **`system-resources-status`** `getText`. Close **`help-resources-close`**. Do **not** click Export / ImageTool / `dumpHeader` / Fields.

| Check | Stills-based result |
|---|---|
| Help `help-keyboard-shortcuts` / `Keyboard Shortcuts` | **OK** — dialog title `Keyboard Shortcuts`; `name=keyboard-shortcuts` / map `keyboard-shortcuts-map` |
| Live shortcut table | **OK** — `shortcut-table` `getText` includes `T pan`, `W winLevel`, `C cine`, `Esc reset`; also `D distance`, `A angle`, `Y polyline`, `B textbox`, `F11 fullscreen`, `S+Alt segmentations`, `0 preset` |
| `shortcut-rows` list | **OK** — **33** rows; matches `shortcut-table` lines |
| Help `help-system-resources` / `System resources` | **OK** — dialog title `System resources`; `name=system-resources` |
| Status `system-resources-status` | **OK** — `Heap 0%  (30859176 / 4198498304)  Native 50%  (decoded 0 / 2099249152)` |
| Export / ImageTool / Dump / Fields untouched | **OK** — Help-only jattach; no Export/ImageTool/`dumpHeader`/Fields clicks |

**Route:** Help shortcuts/resources **headed OK** on `e88e780`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_e88e780_shortcuts.png" width="720" />
<img src="/opt/cursor/artifacts/headed_e88e780_resources.png" width="720" />

## Headed `363f172` — File/toolbar Export DICOM / `name=export-dicom` (Composer)

**Jar:** `/tmp/weasis-headed-363f172.jar` · **SHA:** `363f17231017b86f132dfc4a55ce536d457de7d6` · **Jar file:** `49d71b00b53fe43dafb3402da37dad2f4f47f782acf1d3f2a00779536d9ffc11` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-363f172-fresh4`; prior headed JVMs killed; **did not reuse** Fields/Dump/ImageTool jars.

**Flow:** `dicom:get` Reed chest (`/opt/cursor/artifacts/chest_pa.dcm`) on **DICOM 2D** → jattach toolbar **`export-dicom`** (next to `import-dicom`) or File **`file-export-dicom`** → dialog **`export-dicom-dialog`** with **`export-tree`** / **`export-tabs`** (DICOM / ZIP / DICOMDIR). Type dest in **`export-path`**, click **`export-run`**. Do **not** click ImageTool / `dumpHeader` / uncheck Fields limited.

| Check | Stills-based result |
|---|---|
| Toolbar `export-dicom` / File `file-export-dicom` | **OK** — jattach `name=export-dicom` opens `export-dicom-dialog` |
| Dialog `export-dicom-dialog` tree Reed | **OK** — `export-tree` line `Reed^Alice XR-CHEST-001` |
| Tabs DICOM / ZIP / DICOMDIR | **OK** — `export-tabs` titles **DICOM** / **ZIP** / **DICOMDIR** |
| `export-run` writes dest; `export-status` Exported | **OK** — `Exported /tmp/weasis-export-reed-1789609389`; **1** `.dcm` SOP in dest |
| ImageTool / Dump / Fields untouched | **OK** — raster **1929×2207**; `fields_limited=true`; `dumpHeader_selected=false` |

**Route:** Export DICOM **headed OK** on `363f172`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_363f172_dialog.png" width="720" />
<img src="/opt/cursor/artifacts/headed_363f172_exported.png" width="720" />

## Headed `eefe975` — explorer DicomFieldsView / `name=DICOM Fields` (Composer)

**Jar:** `/tmp/weasis-headed-eefe975.jar` · **SHA:** `eefe9752e1871558bb30229232084b11c2751a8c` · **Jar file:** `5d9cdff727b6b2c3ecf7a7b9986f1f77e30ece8a6e9571f0f703ed49bf19b26e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-eefe975-fresh`; prior headed JVMs killed.

**Flow:** `dicom:get` Reed chest → explorer **`DICOM Fields`** / `explorer-fields-split` (`489,761,664×164`) → jattach **`limited`**, **`tagSearch`**, **`tagTable`**, **`tagDocument`** (not OCR). Limited on → search `Patient` → clear search → uncheck **`limited`**. No ImageTool / `dumpHeader`.

| Check | Stills-based result |
|---|---|
| Explorer dock `DICOM Fields` | **OK** — `name=DICOM Fields`; series `explorer-series` |
| Limited clinical tags (Reed) | **OK** — PatientName `Reed^Alice`; PatientID `XR-CHEST-001`; Modality `DX`; Rows/Columns **2207**/**1929**; WW **65535**; **no PixelData** |
| `tagSearch` `Patient` | **OK** — PatientName/ID/BirthDate/Sex; **Modality hidden** |
| Uncheck `limited` (search cleared) | **OK** — `PixelData (7FE0,0010) OW: [OW]` |
| ImageTool / `dumpHeader` untouched | **OK** — raster **1929×2207**; `dumpHeader_selected=false` |

**Route:** DicomFieldsView **headed OK** on `eefe975`. **Next:** remaining TUTORIALS — **not** Pass.

<img src="/opt/cursor/artifacts/headed_eefe975_limited.png" width="720" />
<img src="/opt/cursor/artifacts/headed_eefe975_search.png" width="720" />
<img src="/opt/cursor/artifacts/headed_eefe975_full.png" width="720" />

## Headed `0d2f39f` — DICOM Header Dump / `name=dumpHeader` (Composer)

**Jar:** `/tmp/weasis-headed-0d2f39f.jar` · **SHA:** `0d2f39ff920c373a3113ef4a0cdc1f153958fb80` · **Jar file:** `411ad365547babd5340e9ddfd45bd3d60434e1bcc0dcc5882ee878b39d334e4e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-0d2f39f-fresh`; prior headed JVMs killed; **did not reuse** prior headed jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`dumpHeader`** (`790,474,52×27`; click **(816, 487)**) → read **`headerDump`** via jattach `getText` (not OCR) + bound View2d crop (`1163,661,134×264`).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`dumpHeader` / Dump** on DICOM Header chrome | **OK** — `name=dumpHeader`; live click Dump |
| **`headerDump`** Reed tag dump | **OK** — `PatientName (0010,0010): Reed^Alice`; `PatientID (0010,0020): XR-CHEST-001`; `Modality (0008,0060): DX`; `WindowWidth (0028,1051): 65535`; `Rows`/`Columns` **2207**/**1929**; `PixelData (7FE0,0010): [OW]` |
| ImageTool shutter/mask/flip/filter untouched | **OK** — chrome false; raster **1929×2207** |

**Route:** DcmHeader Dump **headed OK** on `0d2f39f`. **Next:** explorer DicomFieldsView / cine — **not** Pass.

<img src="/opt/cursor/artifacts/headed_0d2f39f_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_0d2f39f_dump.png" width="720" />

## Headed `b00cd4d` — Image Shutter / `name=shutter` (Composer)

**Jar:** `/tmp/weasis-headed-b00cd4d.jar` · **SHA:** `b00cd4d0df0f0b6d62b273cf74136c22bcf71911` · **Jar file:** `e9be3f791eeea23b745ad5cdc0cb3cc02f25548b7001ece51d700ddadab6586e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-b00cd4d-fresh`; prior headed JVMs killed; **did not reuse** Mask/AutoLevels/Brightness/Crop/Window/Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** (no DICOM ShutterShape tag) → jattach **`shutter`** (`ImageTool`, between **mask** and **flip**) + **bound View2d** crop (`1163,561,134×364`) → Shutter on (1/8-inset blacken) → Shutter off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`shutter` / Shutter** on Image chrome | **OK** — `1250,470,83×25` (between **mask** and **flip** `1337,470,56×25`); click **(1291, 482)** |
| Live toggle **Shutter** on (1/8-inset; full raster) | **OK** — mean abs diff **61.53**; corr **0.0958**; L-band **0.0**; ring w/8…w/4 mean abs Δ **0.0** (not Mask-zeroed); source **1929×2207** |
| Live toggle **Shutter** off (restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0** |
| **mask** / **autolevels** / **brightness** / **crop** / **window** / **flip** / **filter** untouched | **OK** — `maskChrome=false`; other chrome false; `filter=None` |

**Route:** Shutter chrome **headed pixels OK** on `b00cd4d`. **Next:** remaining TUTORIALS.md items — **not** Pass.

<img src="/opt/cursor/artifacts/headed_b00cd4d_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_b00cd4d_shutter.png" width="720" />
<img src="/opt/cursor/artifacts/headed_b00cd4d_restore.png" width="720" />

## Headed `3355cda` — Image Mask / `name=mask` (Composer)

**Jar:** `/tmp/weasis-headed-3355cda.jar` · **SHA:** `3355cda3ca4d3079999b157644f972cb59143aff` · **Jar file:** `fb7afc36c7006974424b55d1a35329484e4f5be2c1b613441f69cb165d67f624` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-3355cda-fresh`; prior headed JVMs killed; **did not reuse** AutoLevels/Brightness/Crop/Window/Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`mask`** (`ImageTool`, between **autolevels** and **flip**) + **bound View2d** crop (`1163,561,134×364`) → Mask on (center-keep, outer blacken) → Mask off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`mask` / Mask** on Image chrome | **OK** — `1178,470,68×25`; **autolevels** `1065,470,109×25`; **flip** `1250,470,56×25` |
| Live toggle **Mask** on (center-keep; outer blacken; full raster) | **OK** — mean abs diff **100.24** (full raster); corr **0.1101**; L-band mean **0.0**; source **1929×2207** |
| Live toggle **Mask** off (restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0** |
| **autolevels** / **brightness** / **crop** / **window** / **flip** / **filter** untouched | **OK** — all chrome flags false; `filter=None` |

**Route:** Mask chrome **headed pixels OK** on `3355cda`. **Next:** Shutter — **not** Pass.

<img src="/opt/cursor/artifacts/headed_3355cda_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3355cda_mask.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3355cda_restore.png" width="720" />

## Headed `18f9802` — Image AutoLevels / `name=autolevels` (Composer)

**Jar:** `/tmp/weasis-headed-18f9802.jar` · **SHA:** `18f9802d06f9ecfe11e3a47f3e77c323eb7a8f23` · **Jar file:** `c7a854068060150cc503eeced993190e3fdfc533a24496a21cf6f2371f09b71e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-18f9802-fresh`; prior headed JVMs killed; **did not reuse** Brightness/Crop/Window/Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`autolevels`** (`ImageTool`, between **brightness** and **flip**) + **bound View2d** crop (`1163,561,134×364`) → AutoLevels on (identity on 0–255 raster) → AutoLevels off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`autolevels` / AutoLevels** on Image chrome | **OK** — `1065,470,109×25`; **brightness** `954,470,107×25`; **flip** `1250,470,56×25` |
| Live toggle **AutoLevels** on (identity on 0–255 raster) | **OK** — mean abs diff **0.0**; corr **1.0**; source **1929×2207** |
| Live toggle **AutoLevels** off (identity restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0** |
| **brightness** / **crop** / **window** / **flip** / **filter** untouched | **OK** — `brightnessChrome=false`; `cropChrome=false`; `windowChrome=false`; `flip=false`; `filter=None` |

**Route:** AutoLevels chrome **headed pixels OK** on `18f9802`. **Next:** Mask/Shutter — **not** Pass.

<img src="/opt/cursor/artifacts/headed_18f9802_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_18f9802_autolevels.png" width="720" />
<img src="/opt/cursor/artifacts/headed_18f9802_restore.png" width="720" />

## Headed `e9ef165` — Image Brightness / `name=brightness` (Composer)

**Jar:** `/tmp/weasis-headed-e9ef165.jar` · **SHA:** `e9ef165daed9d71405751c05281966ddb078fe0c` · **Jar file:** `bcdbe19c754947caac93847a5ea9e10433a50fffc78e7e0a2305b4bff7010929` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-e9ef165-fresh`; prior headed JVMs killed; **did not reuse** Crop/Window/Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`brightness`** (`ImageTool`, between **crop** and **autolevels**, click **(1007, 482)**) + **bound View2d** crop (`1163,561,134×364`) → Brightness on (+48 lift) → Brightness off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`brightness` / Brightness** on Image chrome | **OK** — `954,470,107×25`; **crop** `885,470,65×25`; **flip** `1065,470,56×25` |
| Live toggle **Brightness** on (+48 gray lift; full raster) | **OK** — mean abs diff **46.68**; mean Δ **46.68**; corr **0.9932**; source **1929x2207** |
| Live toggle **Brightness** off (identity restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0** |
| **crop** / **window** / **flip** / **filter** untouched | **OK** — `cropChrome=false`; `windowChrome=false`; `flip=false`; `filter=None` |

**Route:** Brightness chrome **headed pixels OK** on `e9ef165`. **Next:** AutoLevels/Mask/Shutter — **not** Pass.

<img src="/opt/cursor/artifacts/headed_e9ef165_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_e9ef165_brightness.png" width="720" />
<img src="/opt/cursor/artifacts/headed_e9ef165_restore.png" width="720" />

## Headed `f88ec68` — Image Crop / `name=crop` (Composer)

**Jar:** `/tmp/weasis-headed-f88ec68.jar` · **SHA:** `f88ec68ab28b2d3dd18d6ed6d1afa4c459c9d152` · **Jar file:** `44758569d1f9305ee953faec49edd6777915cd5b5b245ff904bcf976e4e9a568` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-f88ec68-fresh`; prior headed JVMs killed; **did not reuse** Window/Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`crop`** (`ImageTool`, between **window** and **flip**, click **(917, 482)**) + **bound View2d** crop (`1163,561,134×364`) → Crop on (center-half + best-fit) → Crop off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`crop` / Crop** on Image chrome | **OK** — `885,470,65×25`; **window** `793,470,88×25`; **flip** `954,470,56×25` |
| Live toggle **Crop** on (mediastinum fills pane; collars reduced) | **OK** — corr(baseline, crop) **0.9098**; center-σ ratio **1.032**; source **1929×2207 → 964×1103** (center half) |
| Live toggle **Crop** off (full raster restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0**; source **1929×2207** |
| **window** / **flip** / **filter** untouched | **OK** — `windowChrome=false`; `flip=false`; `filter=None` through cycle |

**Route:** Crop chrome **headed pixels OK** on `f88ec68`. **Next:** Brightness/AutoLevels/Mask/Shutter — **not** Pass.

<img src="/opt/cursor/artifacts/headed_f88ec68_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_f88ec68_crop.png" width="720" />
<img src="/opt/cursor/artifacts/headed_f88ec68_restore.png" width="720" />

## Headed `3091f5e` — Image Window / `name=window` (Composer)

**Jar:** `/tmp/weasis-headed-3091f5e.jar` · **SHA:** `3091f5e2cc3b2f4ef3372988c81ac9eee94f7cc0` · **Jar file:** `f09bc1321c30eba2da2ffa94fbc54e8114009ee655675b8dc59fac0647c31e8e` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-3091f5e-fresh`; **did not reuse** Flip/Sharpen jars.

**Flow:** `dicom:get` Reed chest on **DICOM 2D** → jattach **`window`** (`ImageTool`, left of **flip**, click **(837, 482)**) + **bound View2d** crop (`1163,561,134×364`) → Window on (WW narrows) → Window off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`window` / Window** on Image chrome | **OK** — bounds via jattach (see log) |
| Live toggle **Window** on (contrast up vs baseline) | **OK** — corr(baseline, window) **0.8888**; mean abs diff **56.62**; L/R spread Δ **16.81** |
| Live toggle **Window** off (file W/L restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0** |
| **flip** / **filter** untouched | **OK** — `flip=false`; `filter=None` through cycle |

**Route:** Window chrome **headed pixels OK** on `3091f5e`. **Next:** Crop/Brightness/AutoLevels/Mask/Shutter — **not** Pass.

<img src="/opt/cursor/artifacts/headed_3091f5e_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3091f5e_window.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3091f5e_restore.png" width="720" />

## Headed `3844bd4` — LUT Sharpen / `name=filter` (Composer)

**Jar:** `/tmp/weasis-headed-3844bd4.jar` · **SHA:** `3844bd464d78a527d66d985a379a52257a32da49` · **Jar file:** `39f1ac3f5bf6a3fb58f8a9db3beb850ee8f88b0665304b7cdec91ce3ae110de0` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-3844bd4-fresh`; prior headed JVMs killed; **did not reuse** Flip jars.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → jattach **`filter`** (`LutToolBar`, click **985, 408**) + **bound View2d** screen crop (`1163,561,134×364`) → Sharpen on → Sharpen off restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`filter` / Sharpen** on LUT chrome | **OK** — `950,395,70×27`; `inverseLut` index **3** at `887,395,63×27` |
| Live toggle **Sharpen** on (edges pop vs baseline) | **OK** — Laplacian var **816→1625** (ratio **1.99**); corr(baseline, sharpen) **0.9969** |
| Live toggle **Sharpen** off (restore) | **OK** — corr(baseline, restore) **1.0**; mean abs diff **0.0**; `filter=None` |
| **Inverse** unchanged (toolbar index 3) | **OK** — `inverseLut=false` / not selected through sharpen cycle |

**Route:** Sharpen/filter **headed pixels OK** on `3844bd4`. **Next:** remaining TUTORIALS.md 2D chrome (Window/Crop/Brightness/AutoLevels/Mask/Shutter) — **not** Pass.

<img src="/opt/cursor/artifacts/headed_3844bd4_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3844bd4_sharpen.png" width="720" />
<img src="/opt/cursor/artifacts/headed_3844bd4_restore.png" width="720" />

## Headed `004f59d` — Image Flip bind + paintImmediately (Composer)

**Jar:** `/tmp/weasis-headed-004f59d.jar` · **SHA:** `004f59deb766dd0ac25ca4231beb889dce450bb0` · **Jar file:** `392d91581a337a728e2b61ada19b216bb0669ec307e94185230b4f564bdc8d93` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-004f59d-fresh`; prior headed JVMs killed; **did not reuse** `weasis-headed-c876041.jar` / `f0fe8f4` / `175892c`.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`flip`** (`ImageTool`) → jattach **bound View2d** `getLocationOnScreen` for still crops (not whole-window band) → click center **(821, 482)** → **1.75s** paint wait → second click restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` (View2d crop `1163,561,134×364`) |
| **`flip` / Flip** on Image chrome | **OK** — `793,470,56×25`; `ImageTool` `481,470,372×25` |
| Live toggle **Flip** on (mirror L/R vs baseline) | **OK** — corr(baseline, flip) **0.9197**; corr(baseline, fliplr(flip)) **0.9916**; heart side swaps in stills |
| Live toggle **Flip** off (restore) | **OK** — `restored` matches `baseline` (mean abs diff **0.0**) |
| vs `c876041` / `f0fe8f4` / `175892c` window-band class | **Fixed** — prior **0.9864** self-corr was chrome-dominated crop; View2d crop shows mirror |

**Route:** Flip P0 **headed pixels OK** on `004f59d`. **Next:** remaining TUTORIALS.md items (W/L, keys, hang/fill not re-scored this run) — **not** Pass.

<img src="/opt/cursor/artifacts/headed_004f59d_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_004f59d_flip.png" width="720" />
<img src="/opt/cursor/artifacts/headed_004f59d_restored.png" width="720" />
## Headed `c876041` — paint-time horizontalMirror buffer (Composer)

**Jar:** `/tmp/weasis-headed-c876041.jar` · **SHA:** `c87604144a9056bd989e6ded39f4f372b16b6100` · **Jar file:** `a73fd483b22ebf02c51862611e8c590f20b0875378d26d9d7fc32d48bb8219d7` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-c876041-fresh`; prior headed JVMs killed; **did not reuse** `weasis-headed-f0fe8f4.jar` / `175892c`.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`flip`** (`ImageTool`) → click center **(821, 482)** (not OCR) → second click restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`flip` / Flip** on Image chrome | **OK** — jattach `flip` bounds (see log) |
| Live toggle **Flip** on (mirror L/R vs baseline) | **FAIL** — corr(baseline, flip) **0.9864**; corr(baseline, fliplr(flip)) **0.173** (same class as `f0fe8f4`) |
| Live toggle **Flip** off (restore) | **OK** — `restored` matches `baseline` (mean abs diff **0.0**) |

**Route:** **Grok stays on `paintFlippedSource` / `applyFlip` / paint-time mirror buffer** for full-size DICOM 2D. Not Pass.

<img src="/opt/cursor/artifacts/headed_c876041_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_c876041_flip.png" width="720" />
<img src="/opt/cursor/artifacts/headed_c876041_restored.png" width="720" />

## Headed `f0fe8f4` — Image Flip dest-X blit (Composer)

**Jar:** `/tmp/weasis-headed-f0fe8f4.jar` · **SHA:** `f0fe8f43c799b093df9c82fbdb12c5db92f3d961` · **Jar file:** `9782429ae779ad0df4536c97fcc315504da912906b339560d7a34aec345bd429` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-f0fe8f4-fresh2`; prior headed JVMs killed; **did not reuse** `weasis-headed-175892c.jar`.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`flip`** `JToggleButton` (`ImageTool`) → click center **(729, 482)** (not OCR) → second click restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` (patch σ≈24) |
| **`flip` / Flip** on Image chrome | **OK** — `701,470,56×25`; `ImageTool` `481,470,280×25` |
| Live toggle **Flip** on (mirror L/R vs baseline) | **FAIL** — corr(baseline, flip) **0.986**; corr(baseline, fliplr(flip)) **0.173**; mean\|Δ\| 5.72 vs fliplr 28.77 |
| Live toggle **Flip** off (restore) | **FAIL** — `restored` ≈ `baseline` (mean\|Δ\| **5.72**) |
| vs `175892c` headed class | **Same failure mode** (high self-corr, low mirror-corr) |

**Route:** **Grok stays on `paintFlippedSource` / dest-X swap blit** for full-size DICOM 2D. Not Pass.

<img src="/opt/cursor/artifacts/headed_f0fe8f4_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_f0fe8f4_flip.png" width="720" />
<img src="/opt/cursor/artifacts/headed_f0fe8f4_restored.png" width="720" />

## Headed `175892c` — Image Flip chrome (Composer)

**Jar:** `/tmp/weasis-headed-175892c.jar` · **SHA:** `175892c25514104292fa4152c0e750fefdd15959` · **Jar file:** `1576015338a8989654ef2687c89f26625900138ca2fa3aebf0ff580df3644add` · **Gogo:** `17179` · OSGi cache `/tmp/weasis-headed-cache-175892c-fresh`; prior headed JVMs killed.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`flip`** `JToggleButton` (`ImageTool`) → click center **(729, 482)** (not OCR) → second click restore.

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`flip` / Flip** on Image chrome | **OK** — `701,470,56×25`; `ImageTool` `481,470,280×25` |
| Live toggle **Flip** on (mirror L/R vs baseline) | **FAIL** — `flip` ≈ `baseline` (corr **0.99**); fliplr corr **0.17**; heart side unchanged |
| Live toggle **Flip** off (restore) | **FAIL** — `restored` ≈ `baseline` (no prior mirror to restore) |
| `view.isFlip()` via jattach after `doClick` | toggles **true/false** (chrome wiring) |

**Route:** **Grok stays on `ImageTool` / `setFlip` / paint-time horizontal flip** for full-size DICOM 2D (Have 8×8 passes; headed chest does not mirror). Not Pass.

<img src="/opt/cursor/artifacts/headed_175892c_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_175892c_flip.png" width="720" />
<img src="/opt/cursor/artifacts/headed_175892c_restored.png" width="720" />

## Headed `eb56b2c` — Inverse LUT chrome (Composer)

**Jar:** `/tmp/weasis-headed-eb56b2c.jar` · **SHA:** `eb56b2cb1d3d8f9b317471150a6caba96e86c887` · **Jar file:** `48de62b2c1d72b36e34aca62299aece507803d4a6a9e2e9532db28e2364b88ed` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`inverseLut`** `JToggleButton` (`LutToolBar`) → click **on (918, 408)** → click **off** restore (not OCR).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`inverseLut` / Inverse** on chrome | **OK** — `887,395,63×27`; `LutToolBar` `737,393,215×31` |
| Live toggle **Inverse** on (complement pixels) | **OK** — `inverse` (`Inverse` highlighted; negative chest) |
| Live toggle **Inverse** off (restore) | **OK** — `restored` (matches baseline polarity) |

**Route:** Inverse LUT chrome + pixel re-render **headed OK** on `eb56b2c`. Next: remaining TUTORIALS.md items (not Pass).

<img src="/opt/cursor/artifacts/headed_eb56b2c_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_eb56b2c_inverse.png" width="720" />
<img src="/opt/cursor/artifacts/headed_eb56b2c_restored.png" width="720" />

## Headed `4d4d04c` — Zoom / Rotation chrome → paint-time transform (Composer)

**Jar:** `/tmp/weasis-headed-4d4d04c.jar` · **SHA:** `4d4d04cb49689dad5a626a3decd16fb1913bf62e` · **Jar file:** `59fab477a0d5347b1306e138c12482751ce2331e42ccdbb36e90f0180a3057d8` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on `ZoomToolBar` / `RotationToolBar` (`Best Fit`, `2x`, `90°`, `0°`) → click **90° (1220, 408)** → **0° (1190, 408)** → **2x (1131, 408)** → **Best Fit (986, 408)** (not OCR).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `baseline` |
| **`90°` / `0°` / `2x` / `Best Fit`** on chrome | **OK** — jattach `rotation-btn` + `zoom-btn` |
| Live **90°** rotates painted pixels vs baseline | **OK** — `rot90` (chest sideways; `90°` highlighted) |
| Live **0°** restores upright | **OK** — `rot0` |
| Live **2x** magnifies vs Best Fit | **OK** — `zoom2x` (`2x` highlighted; cropped viewport) |
| Live **Best Fit** returns fit | **OK** — `best_fit` (`Best Fit` highlighted; full chest in view) |

**Route:** Zoom/rotation chrome + paint-time pixels **headed OK** on `4d4d04c`. Next: remaining TUTORIALS.md items (not Pass).

<img src="/opt/cursor/artifacts/headed_4d4d04c_baseline.png" width="720" />
<img src="/opt/cursor/artifacts/headed_4d4d04c_rot90.png" width="720" />
<img src="/opt/cursor/artifacts/headed_4d4d04c_rot0.png" width="720" />
<img src="/opt/cursor/artifacts/headed_4d4d04c_zoom2x.png" width="720" />
<img src="/opt/cursor/artifacts/headed_4d4d04c_best_fit.png" width="720" />

## Headed `b1a692a` — SEG Show overlay chrome (Composer)

**Jar:** `/tmp/weasis-headed-b1a692a.jar` · **SHA:** `b1a692a9d7a863aae27b559717f8594daddd26e9` · **Jar file:** `0239b7e7a46ade87bd6e40e38b206737ab37aa3be06c22fbd0d0f77174d7d992` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on **`segOverlay`** `JCheckBox` (`Segmentation` toolbar) → toggle **off** then **on** at **(1279, 454)** (not OCR).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `2d_tab` |
| **`segOverlay` / Show overlay** on chrome | **OK** — `1220,443,119×23` |
| Live toggle overlay **off** | **OK** — `overlay_off` |
| Live toggle overlay **on** | **OK** — `overlay_on` |
| SEG mask pixels on image | **Not scored** — no SEG SOP anatomy |
| SR **Print** on SR tab | **Have-only** — not exercised headed |

**Route:** Show overlay chrome **headed OK** on `b1a692a`. Next: remaining TUTORIALS.md items (not Pass).

<img src="/opt/cursor/artifacts/headed_b1a692a_2d_tab.png" width="720" />
<img src="/opt/cursor/artifacts/headed_b1a692a_overlay_off.png" width="720" />
<img src="/opt/cursor/artifacts/headed_b1a692a_overlay_on.png" width="720" />

## Headed `6f19c30` — DICOM 3D VR chrome (Composer)

**Jar:** `/tmp/weasis-headed-6f19c30.jar` · **SHA:** `6f19c3021d106819ec061719d4c1f2559daca40e` · **Jar file:** `8c26e25df30adfb2052a67dda4de91fe31d679ddd48d62d31f6f6fed3261b69a` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame) on **DICOM 2D** → live jattach bounds on Basic 3D **`3d`** `JButton` (beside **MPR**) → click **(1089, 454)** (not OCR).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `2d_tab` |
| **`3d`** control on 2D chrome | **OK** — `1074,441,30×27` |
| Live click opens 3D viewer | **OK** — `3d_click` |
| Tab **DICOM 3D Viewer**; **DICOM 2D** remains | **OK** — `3d_tab` |
| **COMPOSITE / MIP / MINIP / ISO** on `View3DToolbar` | **OK** — `3d_bars` (+ `VolLutToolBar` CT LUT present) |
| Volume canvas pixels / GPU (llvmpipe, N/A) | **Not scored** — single-frame anatomy |

**Route:** 3D chrome **headed OK** on `6f19c30`. Next: remaining TUTORIALS.md items (not Pass).

<img src="/opt/cursor/artifacts/headed_6f19c30_2d_tab.png" width="720" />
<img src="/opt/cursor/artifacts/headed_6f19c30_3d_click.png" width="720" />
<img src="/opt/cursor/artifacts/headed_6f19c30_3d_tab.png" width="720" />
<img src="/opt/cursor/artifacts/headed_6f19c30_3d_bars.png" width="720" />

## Headed `ec522f1` — Fusion Hot Iron / PET chrome (Composer)

**Jar:** `/tmp/weasis-headed-ec522f1.jar` · **SHA:** `ec522f16087c91997978b2c25314ca386438bbaf` · **Jar file:** `ff171924f128d0e7fcc1d197405b231e90a2966d627338625be20f60e463f627` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** `dicom:get` Reed chest (`chest_pa.dcm`, single-frame CR/DX) on **DICOM 2D** → live jattach bounds on `FusionColorBar` (`Fusion`) and LUT buttons **`Hot Iron`** / **`PET`** → click **PET (1164, 454)** then **Hot Iron (1111, 454)** (not OCR).

| Check | Stills-based result |
|---|---|
| 2D tab with chest loaded | **OK** — `2d_tab` |
| `Fusion` toolbar / `FusionColorBar` present | **OK** — toolbar `1076,439,110×31` |
| **Hot Iron** + **PET** buttons on chrome | **OK** — `fusion_toolbar` crop |
| Live click **PET** | **OK** — `pet_click` |
| Live click **Hot Iron** restore | **OK** — `hot_iron_restore` |
| PET overlay / SUV readout on image | **Not scored** — single-frame DX anatomy |

**Route:** Fusion chrome **headed OK** on `ec522f1`. Next: remaining TUTORIALS.md items (not Pass).

<img src="/opt/cursor/artifacts/headed_ec522f1_2d_tab.png" width="720" />
<img src="/opt/cursor/artifacts/headed_ec522f1_fusion_toolbar.png" width="720" />
<img src="/opt/cursor/artifacts/headed_ec522f1_pet_click.png" width="720" />
<img src="/opt/cursor/artifacts/headed_ec522f1_hot_iron_restore.png" width="720" />

## Headed `d1e96de` — Basic 3D MPR on 2D chrome (Composer)

**Jar:** `/tmp/weasis-headed-d1e96de.jar` · **SHA:** `d1e96defa9ac1ff80285e092497a1b12cbe935c5` · **Jar file:** `6c46852f8ca50bf1778e39f3ac2758483a00a8d7f44520f9bfb85cb7862aa17f` · **Gogo:** `17179` · new OSGi cache; single headed JVM.

**Flow:** startup Dummy off (empty viewer) → `dicom:get` Reed chest (`chest_pa.dcm`, single-frame CR/DX) → live jattach bounds on `mpr` `JButton` / `Basic3DToolBar` → click center **(1052, 454)** (not OCR).

| Check | Stills-based result |
|---|---|
| Startup — empty viewer, no fake tab | **OK** — `startup` |
| 2D tab with chest loaded | **OK** — `2d_tab` |
| `mpr` control present on 2D chrome | **OK** — bounds `1030,441,44×27`; toolbar `Basic 3D` |
| Live click opens MPR | **OK** — `mpr_click` |
| New **MPR** tab; **DICOM 2D** tab still present | **OK** — tab strip `DICOM 2D` + `MPR` (`tab_chrome`) |
| Three orthogonal `MprView` cells (1×3) | **OK** — AXIAL / CORONAL / SAGITTAL (`mpr_tab_three_planes`) |
| Coronal/sagittal content on single-frame DX | **Not scored** — empty/black planes expected for CR/DX stack |

**Route:** MPR chrome **headed OK** on `d1e96de`.

<img src="/opt/cursor/artifacts/headed_d1e96de_startup.png" width="720" />
<img src="/opt/cursor/artifacts/headed_d1e96de_2d_tab.png" width="720" />
<img src="/opt/cursor/artifacts/headed_d1e96de_mpr_click.png" width="720" />
<img src="/opt/cursor/artifacts/headed_d1e96de_mpr_tab_three_planes.png" width="720" />
<img src="/opt/cursor/artifacts/headed_d1e96de_tab_chrome.png" width="720" />

## Headed `0831d3d` — remaining-tab `CLocation.working()` split (Composer)

**Jar:** `/tmp/weasis-headed-0831d3d.jar` · **SHA:** `0831d3d6acc650142c2c500c1b7b1fa47fa75319` · **Jar file:** `c6aa5dd984cec1729e887b3a371de260a49233a6f6d5651ba7e46947b1a0c797` · **Gogo:** `17279` · new OSGi cache; single headed JVM.

**Tab setup:** `dicom:close -a` → Reed chest (`chest_pa.dcm`) → `dcmview2d:layout -n 1` → Nystrom knee (`knee_ap.dcm`) → **two `DICOM 2D` viewer tabs** (not hang 1×2). **Tab drag:** jattach `viewer-tabs` bounds; drag tab **1** center **(1311, 519)** → `viewer-work` east **(1427, 714)**.

| Check | Stills-based result |
|---|---|
| Startup Dummy off — empty viewer, no fake tab | **OK** — `startup_empty` |
| Two viewer tabs (Reed vs Nystrom) | **OK** — `two_tabs` (two `DICOM 2D` tab headers) |
| East `CLocation.working()` split | **OK** — `split_east`: side-by-side working panes; chest left / knee east dock |
| Leftover tab on `viewer-tabs` inside working area | **OK** — `leftover_tabs_chrome`: one `Viewer` + `DICOM 2D` tab strip remains on left pane |
| Hang 1×2 contrast (one tab, not working split) | **OK** — `hang_1x2_one_tab`: single tab, chest+knee in one 1×2 grid |
| Explorer series DnD splits working area | **Not exercised** this run (no explorer drag) |
| West / south / north splits | **Not captured** this run |

**Route:** East split + leftover tab **headed OK** on `0831d3d`.

<img src="/opt/cursor/artifacts/headed_0831d3d_startup_empty.png" width="720" />
<img src="/opt/cursor/artifacts/headed_0831d3d_two_tabs.png" width="720" />
<img src="/opt/cursor/artifacts/headed_0831d3d_split_east.png" width="720" />
<img src="/opt/cursor/artifacts/headed_0831d3d_leftover_tabs_chrome.png" width="720" />
<img src="/opt/cursor/artifacts/headed_0831d3d_hang_1x2_one_tab.png" width="720" />

## Headed `ddcc24c` — live-bounds click (prior SHA)

**Jar:** `/tmp/weasis-headed-ddcc24c.jar` · **SHA:** `ddcc24c18a48ea022e62de84cbe7c2ab5e3c6ad0`

| Component | screen (x,y,w,h) | click center |
|---|---|---|
| `measure-angle` | 541, 383, 40×32 | **(561, 399)** |
| `measure-rect` | 637, 383, 40×32 | **(657, 399)** |
| `measure-polyline` (edge mis-click band) | 589, 383, 40×32 | (609, 399) |

| Check | Stills-based result |
|---|---|
| **A** two-ray `N.N°` on BL | **OK** — **90.0°** (`headed_ddcc24c_liveclick_measure_angle.png`) |
| **G** ROI `n=` / min / max / mean | **OK** — **`n=23870`** (`headed_ddcc24c_liveclick_measure_roi_g.png`) |
| Hang / fill / Delete | **OK** |

Edge-detect **(604, 391)** / **(699, 391)** were verifier **Y/B mis-clicks**, not product hit-test failures.

<img src="/opt/cursor/artifacts/headed_ddcc24c_liveclick_toggle_a_toolbar_crop.png" width="520" />
<img src="/opt/cursor/artifacts/headed_ddcc24c_liveclick_measure_angle.png" width="720" />
<img src="/opt/cursor/artifacts/headed_ddcc24c_liveclick_measure_roi_g.png" width="720" />

<!-- CURSOR_AGENT_PR_BODY_END -->
