# Module catalog (Weasis 4.7.3 layout)

Oracle: public tree of `nroduit/Weasis` tag **v4.7.3** (paths / POM artifact IDs). Fixture: [fixtures/weasis-4.7.3-poms.txt](fixtures/weasis-4.7.3-poms.txt).

## Root reactor (`weasis-framework`)

Same modules as upstream `pom.xml`:

`weasis-parent`, `weasis-launcher`, `weasis-opencv`, `weasis-core`, `weasis-imageio`, `weasis-base`, `weasis-dicom`, `weasis-acquire`, `tests`.

`weasis-distributions` is **not** in the root reactor. Package native zip from that aggregator with `-P compressXZ`.

`weasis-i18n` **is** in the root reactor (WP-14). Upstream keeps translations in the sister repo `nroduit/weasis-i18n`; this clone vendors i18n fragments (name ends in i18n) for every documented `Messages.java` host so File &gt; Preferences can attach locales with ≥ 30 % coverage.

`weasis-pacs-connector` **is** in the root reactor (WP-15). It is the ViewerHub / dcm4chee IID Have sibling (`nroduit/viewer-hub`), **not** a Felix auto-start bundle and **not** in the Weasis 4.7.3 main POM fixture.

## Feature aggregators

| Directory | groupId | artifactId | Children (4.7.3) |
|---|---|---|---|
| `weasis-base/` | `org.weasis.base` | `weasis-base` | parent, `weasis-base-ui`, `weasis-base-explorer`, `weasis-base-viewer2d` |
| `weasis-dicom/` | `org.weasis.dicom` | `weasis-dicom` | parent, codec, explorer, viewer2d, sr, wave, au, send, qr, rt, isowriter, 3d |
| `weasis-acquire/` | `org.weasis.acquire` | `weasis-acquire` | explorer, editor (**no** acquire-parent) |
| `weasis-opencv/` | `org.weasis.opencv` | `weasis-opencv` | linux-x86-64, linux-aarch64, macosx-aarch64, macosx-x86-64, windows-x86-64 |
| `weasis-dicom/weasis-dicom-3d/` | `org.weasis.dicom.3d` | `weasis-dicom-3d` | jogamp, jogamp-native, **viewer3d** (`weasis-dicom-viewer3d`) |

## Shipping bundle IDs

| artifactId | Bundle-SymbolicName |
|---|---|
| `weasis-core` | `org.weasis.core` |
| `weasis-imageio-codec` | `org.weasis.imageio.codec` |
| `weasis-dicom-codec` | `org.weasis.dicom.codec` |
| `weasis-dicom-explorer` | `org.weasis.dicom.explorer` |
| `weasis-dicom-viewer2d` | `org.weasis.dicom.viewer2d` |
| `weasis-dicom-sr` | `org.weasis.dicom.sr` |
| `weasis-dicom-au` | `org.weasis.dicom.au` |
| `weasis-dicom-wave` | `org.weasis.dicom.wave` |
| `weasis-dicom-send` | `org.weasis.dicom.send` |
| `weasis-dicom-qr` | `org.weasis.dicom.qr` |
| `weasis-dicom-rt` | `org.weasis.dicom.rt` |
| `weasis-dicom-isowriter` | `org.weasis.dicom.isowriter` |
| `weasis-dicom-viewer3d` | `org.weasis.dicom.viewer3d` |
| `weasis-base-ui` | `org.weasis.base.ui` |
| `weasis-base-explorer` | `org.weasis.base.explorer` |
| `weasis-base-viewer2d` | `org.weasis.base.viewer2d` |
| `weasis-acquire-explorer` | `org.weasis.acquire.explorer` |
| `weasis-acquire-editor` | `org.weasis.acquire.editor` |
| `weasis-opencv-core-linux-x86-64` | `weasis-opencv-core-linux-x86-64` (fragment-host `weasis-core-img`) |

Directory `weasis-imageio/` **artifact** `weasis-imageio-codec`. OpenCV install name `weasis-opencv-core-${native.library.spec}-${weasis.opencv.pkg.version}.jar`.

## Reactor locks

- Feature DICOM/base bundles parent `weasis-dicom-parent` / `weasis-base-parent`, not `weasis-parent`.
- Acquire has **no** `weasis-acquire-parent`.
- `${revision}${changelist}` — do not hard-code versions in child POMs.
- `.internal` packages are bnd-private.
