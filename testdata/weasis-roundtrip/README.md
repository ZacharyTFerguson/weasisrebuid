# Weasis round-trip conformance pack (synthetic only)

Five EVR LE MONOCHROME2 Part-10 files shared with [Dicomlight-TS](https://github.com/ZacharyTFerguson/Dicomlight-TS) `testdata/weasis-roundtrip/`. No PHI.

- `manifest.json` — study list and SHA-256 fingerprints
- `cross-oracle-report.json` — pinned two-oracle agreement (contract reference; regenerate from Dicom Light TS `npm run cross-oracle:write`)

JUnit: `WeasisRoundtripOracleTest` asserts the Java oracle’s W/L samples match the Weasis column in the pinned report.

CLI: `scripts/dicom-oracle.sh <path>` exits **0** only when the file is understood; **1** when opened but not decoded; **2** when the path cannot be opened.
