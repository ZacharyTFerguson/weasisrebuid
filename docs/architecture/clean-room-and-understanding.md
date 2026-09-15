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
