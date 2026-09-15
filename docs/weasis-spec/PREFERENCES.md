# Preferences (Weasis 4.7 docs)

Source: [Preferences](https://weasis.org/en/basics/customize/preferences/). Keys live in `base.json` (`weasisPreferences[]` with `code`, `value`, Java type, F/A/AP).

## Load order

1. Java system property (`weasis:config` / launch URI `pro=`).
2. `weasis/conf/*.json`.
3. Documented default.

Type badges: **F** first launch only (then client UI), **A** every launch, **AP** always from JSON only.

## Keys this clone must honor

| Key | Default | Notes |
|---|---|---|
| `weasis.dicom.root.uid` | `2.25` | KO/PR SOP UIDs |
| `weasis.download.immediately` | `true` | |
| `weasis.dicom.explorer.filter.mode` | `TEXT` | TEXT / DATE / MODALITY |
| `download.concurrent.series` | `3` | MX-10 |
| `download.concurrent.series.images` | `4` | MX-11 |
| `weasis.main.ui` | `weasis-base-ui` | |
| `weasis.name` | `Weasis` | AP |
| `weasis.profile` | `default` | AP |
| `felix.log.level` | `1` | shipping |
| `org.apache.sling.commons.log.level` | `INFO` | shipping |
| `org.apache.sling.commons.log.stack.limit` | `3` | shipping |
| `locale.lang.code` | `en` | F; UI list ≥ 30 % i18n |
| `weasis.i18n.min.percent` | `30` | F; language list threshold |
| `weasis.theme` | `org.weasis.launcher.FlatWeasisTheme` | recommended Core Dark |
| `weasis.level.inverse` | `true` | |
| `weasis.color.wl.apply` | `true` | |
| `weasis.apply.latest.pr` | `false` | |
| `weasis.force.3d` | `false` | |
| `weasis.toolbar.mouse.left` | `winLevel` | also middle/right/wheel |
| `weasis.portable.dicom.directory` | `dicom,DICOM,IMAGES,images` | CD/DVD next to executable |
| `weasis.import.dicom` / `.qr` / export / send | `true` | UI visibility |
| Dicomizer `weasis.acquire.dest.*` | host null, AET `DCM4CHEE`, port `11112` | WP-12 |

File > Preferences is **Alt+P**. Language and theme are first-run comfort settings in the tutorials.
