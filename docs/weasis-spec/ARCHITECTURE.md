# Architecture (Weasis 4.7 docs)

Source: [Weasis Architecture](https://weasis.org/en/basics/architecture/) and [Building Weasis](https://weasis.org/en/getting-started/building-weasis/) for version **4.7**.

## Runtime

Weasis is an **OSGi** application on **Apache Felix**. Bundles (plugins) register factories with **Declarative Services** (`@Component(service = …)`). Factories are created at startup; viewer instances are created on demand.

Plugin categories:

1. **Media Viewer and Editor** — central panel. Implements `ViewerPlugin` or `ImageViewerPlugin`. Factory: `SeriesViewerFactory`. Special SOPs may also bind via `DicomSpecialElementFactory`.
2. **UI Aggregator** — `weasis.main.ui` = `weasis-base-ui`. Menus, toolbars, and tools follow the focused viewer.
3. **Data Explorer** — load/retrieve/filter patients, studies, series.
4. **Codec** — decode DICOM and other media; may attach **native library fragments**.
5. **Utility and Core Services** — `weasis-core` SDK (Insertable, OpManager, prefs, Gogo `weasis:*`).

## Fragment bundles

- **i18n fragments** (name ends in `i18n`) attach translations to a host bundle (WP-14). Language list in File > Preferences shows locales with **≥ 30 %** coverage.
- **Native codec fragments** (OpenCV per `native.library.spec`) attach JNI binaries. The launcher loads only the fragment for the running OS/arch. Install, do not always start (`felix.auto.install.23`).

## Framework start levels (shipping `base.json`)

| Level | Bundles |
|---|---|
| beginning | **130** |
| new bundle | **300** |
| 1 | Gogo runtime |
| 3 | Gogo command + shell |
| 4 | Bundle repository + OSGi promise |
| 5 | DS component API, configadmin, prefs, SCR |
| 7 | MigLayout (+ JAXB-OSGi when graphics XML needs it) |
| 10 | `weasis-core-img`, zip4j, JOML, Docking Frames (WP-6) |
| 12 | `weasis-core` |
| 15 | `weasis-imageio-codec` (directory `weasis-imageio/`) |
| 23 | OpenCV native fragment (install) |
| 30 | `weasis-dicom-codec` |
| 35 | Jackson |
| 40 | `weasis-dicom-explorer` |
| 60 | `weasis-base-ui` |
| 70 | `weasis-dicom-viewer2d` + `weasis-dicom-sr` |
| 75 | `weasis-dicom-au` + `weasis-dicom-wave` |
| 100 | `weasis-base-viewer2d` (non-DICOM profile) |
| 110 | send + isowriter + qr |
| 115 | `weasis-dicom-rt` |
| 120 | `weasis-dicom-viewer3d` |
| 121 | JOGL native (install) |

Gogo telnet: **17179** (`-Dgosh.port=17179`, not a JSON key). Dicomizer profile: **17181**.

`felix.config.properties` locates `base.json`. `felix.extended.config.properties` extends/overrides it (Dicomizer `dicomizer.json`, non-DICOM explorer profile).

## Plugin factory pattern (docs)

```java
@org.osgi.service.component.annotations.Component(service = SeriesViewerFactory.class)
public class View2dFactory implements SeriesViewerFactory { /* createInstance on demand */ }
```

The same pattern applies to `PreferencesPageFactory`, toolbars, explorer factories, and codecs (`Codec.class`).

## Native zip / installers

Since 4.0 only the **native installer** is maintained. `mvn -P compressXZ -f weasis-distributions clean package` writes `weasis-native.zip`. Production versions must not be SNAPSHOT.
