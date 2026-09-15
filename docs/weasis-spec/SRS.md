# Software requirements (from Weasis 4.7 documentation)

Normative user/integrator behavior. Implementation is Java 25 / Felix / Swing as in ARCHITECTURE.

## Boot and identity

- Application name default **Weasis**; version **4.7.3**.
- Main class `org.weasis.launcher.AppLauncher`.
- VM example: `-Xms64m -Xmx768m -Dgosh.port=17179`.
- Two memory pools: JVM heap (UI + metadata) and native image memory (decoded pixels). Help > System resources is diagnostic only.

## Ingest

- Local files, folders, DICOM ZIP (zip4j `char[]` password), DICOMDIR, portable directories beside the executable, WADO XML manifest, DICOMweb, DIMSE Q/R.
- Patient join: name **and** ID.
- Series sort: SR/DOC last (4.7.3 comparator).
- Skip unsupported SOP with Information popup (4.7.0).

## 2D viewing

- Op chain: WindowAndPresets → Filter → PseudoColor → Shutter → Overlay → Affine.
- Overlays 60xx, shutter, padding, LOSSY flag.
- Zoom presets; Best Fit default; magic zoom −200 / −100.

## Measurement and presentation

- Distance, angle, polyline, draw, textbox; graphics selection/delete map.
- KO (Key Object Selection) and PR (GSPS) export using `weasis.dicom.root.uid`.
- Apply latest PR is opt-in (`weasis.apply.latest.pr`).

## Other viewers

ECG, SR, audio, RT, SEG, MPR/CPR/MIP, fusion with SUVbw, 3D VR (real GL, not llvmpipe).

## Integration

- `weasis://?commands` from v3.6.0.
- ViewerHub manages server-side prefs and native zip.
- dcm4chee IID / `/display` launches the viewer via the protocol.
