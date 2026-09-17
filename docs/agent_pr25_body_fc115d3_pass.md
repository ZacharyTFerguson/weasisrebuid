<!-- CURSOR_AGENT_PR_BODY_BEGIN -->
## Status

Draft on `cursor/weasis-spec-rebuild-26c5`. **Do not merge.** **Do not tick Pass.**

Staffing until 2026-09-17T23:26Z: Grok writer + Composer verifier only.

**HEAD:** `fc115d3` (`Show Screenshot toolbar PNG/JPEG save of the bound 2D view.`). **Flip** `004f59d`, **Sharpen** `3844bd4`, **Window** `3091f5e`, **Crop** `f88ec68`, **Brightness** `e9ef165`, **AutoLevels** `18f9802`, **Mask** `3355cda`, **Shutter** `b00cd4d`, **DcmHeader Dump** `0d2f39f`, **DicomFieldsView** `eefe975`, **Export DICOM** `363f172`, and **Help shortcuts/resources** `e88e780` headed PASS (prior chrome not re-scored). **Screenshot** / `name=screenshot` — Composer headed proof **in progress**. Full TUTORIALS.md walkthrough is still **not** Pass.





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

<sub>To show artifacts inline, <a href="https://cursor.com/dashboard/cloud-agents#my-pull-requests">enable</a> in settings.</sub>
<!-- CURSOR_AGENT_PR_BODY_END -->

