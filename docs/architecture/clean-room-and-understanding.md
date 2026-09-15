# Clean-room layout and DICOM understanding path

This tree is a **spec-driven** reconstruction of Weasis 4.7.3. Spec sources live in
`ZacharyTFerguson/dicomlight` (`docs/weasis-spec/`). There is **no** `nroduit/Weasis` source in git.

## Reactor (what runs today)

| Layer | Maven module | Role |
| --- | --- | --- |
| Boot | `weasis-launcher` | Felix 7.0.5, Gogo shell |
| Core SDK | `weasis-core`, `weasis-base-ui` | Media APIs, dummy viewer |
| Imaging | `weasis-imageio` (artifact `weasis-imageio-codec`) | Codec adapters |
| DICOM | `weasis-dicom-codec` | Part-10 read, W/L paint, **understanding oracle** |
| Viewer | `weasis-dicom-viewer2d` | View2d + W/L integration tests |
| Native | `weasis-opencv/*` | OpenCV fragment (not required for uncompressed W/L) |

WP-0–2 status and commands: root `README.md`. CHECKLIST §6 **Pass** is not inferred from unit tests or `weasis:info`.

## One understanding path (pixels)

All “does Weasis understand this DICOM?” pixel checks funnel through the same code:

```text
Part-10 file
  → DicomMediaIO.open
  → DicomUnderstandingLimits.canPaintWindowLevel(ts, dataset)   ← single gate
  → WindowLevelPainter.paintMonochrome2 (via DicomMediaIO.paintWindowLevel)
  → 8-bit grey BufferedImage (corner + center samples for oracle JSON)
```

Headless contract for Dicom Light TS / Composer loops:

```bash
scripts/dicom-oracle.sh <part-10-path>   # JSON on stdout; exit 0 = understood
```

Implementation: `org.weasis.dicom.codec.DicomUnderstandingOracle`.

## One measurement path (millimetres and HU)

Added by the 2026-09-15 measurement run (slices 1–5, PRs #27, #26, #28, #29 on this branch). Same
default-deny posture as the pixel path: no tag, no number.

```text
instance Attributes (the dataset at View2d.getFrameIndex(), re-read on every page)
  → InstanceSpacing.resolve(Attributes)                      ← (0028,0030) PixelSpacing row\col
      CT/MR: exactly two finite values > 0, else empty       ← never ImagerPixelSpacing as a CT fallback
      CR/DX: PixelSpacing wins (PIXEL_SPACING_CALIBRATED, never ÷ M again)
             else ImagerPixelSpacing ÷ M  (M = ERMF, or SID/SOD)  → IMAGER_OBJECT_ESTIMATE + caveat
             ERMF and SID/SOD disagree > 1e-2                     → IMAGER_DETECTOR + MAG_CONFLICT
             no M                                                  → IMAGER_DETECTOR + detector-plane warning
  → LineGraphic.getLengthMm(ImageSpacing) = hypot(Δx·col, Δy·row); getLength() stays pixels
  → MeasurementLabel.formatLine(...)   "5.0 mm" | "2.0 mm (detector plane)" | "1.25 mm (estimate)" | "10.0 px"
  → View2d.getGeometryWarning()        resolver warning text, not tag presence

  → RoiStatistics.ellipse(Attributes, Ellipse2D)             ← stored PixelData ints → LutPipeline.modalityValue
      ModalityLUTSequence present → empty (refused); unit = RescaleType or "stored"; padding excluded and counted
      no BufferedImage overload exists — the painted 0–255 raster can never be sampled as HU
  → MeasurementLabel.formatEllipse(RoiStats)  "-1000.0 HU (n=12)"
```

Stack paging (`View2d.loadStack`) is **instance** paging when several files share one
`SeriesInstanceUID`, sorted by `InstanceNumber`. A **lone** multi-frame object
(`NumberOfFrames > 1`) scrolls by sample offset `frame · rows · columns` in `PixelData` via
`WindowLevelPainter.paintMonochrome2(dcm, frame, w, l)`; multi-frame cannot be mixed with other
stack files in one load.

Fixture note: both round-trip CTs carry `PixelSpacing 0.80\0.80`, so the tests that prove spacing is read
(not hard-coded) are the synthetic anisotropic / two-file cases in `InstanceSpacingTest`,
`LineGraphicMmTest`, `View2dMeasureLabelTest`. No spacing or rescale literal lives in `src/main`.

## Explicit limits (default deny)

Documented in code: `DicomUnderstandingLimits`. Summary:

| Requirement | Understood when |
| --- | --- |
| Transfer syntax | `1.2.840.10008.1.2.1` Explicit VR LE, **uncompressed** |
| Photometric | `MONOCHROME2` only (not `MONOCHROME1`, not color) |
| Pixel data | Native `PixelData` in dataset; Java W/L path (no JPEG/RLE/J2K decode in oracle) |
| PHI in oracle JSON | Never — synthetic fixtures only in tests |

Opened but **not understood** (exit 1): implicit VR, JPEG TS, RGB, wrong photometric, etc.  
**Skipped** disposition: encap PDF/video/KO/PR/SEG mime classes (see `DicomUnderstandingOracle.disposition`).

Widening limits requires new tests in `DicomUnderstandingLimitsTest` and `DicomUnderstandingOracleTest`, plus a WP note in this file — not silent behavior changes.

## Related docs

- Origin / license: `ORIGIN.md`
- Loop scorecard: `docs/loops/TEAM-B-SCORE-wp0-4.md`
