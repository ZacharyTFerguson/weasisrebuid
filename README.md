# Weasis 4.7.3 (clean-room)

Independent reconstruction of **[Weasis](https://weasis.org)** **4.7.3** (Java, Apache Felix 7.0.5, Swing, OSGi). Spec lives in `ZacharyTFerguson/dicomlight` (`docs/weasis-spec/`). This repository is Track A. It is **not** a fork of `nroduit/Weasis`.

**License:** EPL-2.0 OR Apache-2.0 (`ORIGIN.md`). No PHI.

## Status

- **WP-0:** `AppLauncher` boots Felix **7.0.5**. Gogo `gosh.port` **17179**. `weasis:info -v`; `lb` lists Felix + core.
- **WP-1:** Core SDK — dummy `SeriesViewerFactory` opens a blank `ViewerPlugin`.
- **WP-2 (this branch):** OpenCV fragment (linux-x86-64), `weasis-core-img`, `weasis-imageio-codec`, `weasis-dicom-codec`. Explicit VR LE MONOCHROME2 CT paints with W/L.

Shipping prefs (MX-03) are `weasis-distributions/etc/config/base.json` (INFO, stack **3**, `felix.log.level` **1**). `weasis-launcher/conf/base.json` is IDE/DEBUG.

## Build

JDK **25+**, Maven **3.8.1+**.

```bash
mvn clean install
mvn spotless:apply
```

`weasis-distributions` is **not** in the root reactor. Native zip is a later WP (`-P compressXZ`).

## Run (after `mvn install`)

```bash
mvn -pl weasis-launcher -am exec:java
# another terminal:
telnet localhost 17179
g! weasis:info -v
g! lb
g! weasis:ui -q
```

Non-headless: File > Preferences (Alt+P) and a blank dummy viewer in the center.

IDE: main `org.weasis.launcher.AppLauncher`, VM `-Xms64m -Xmx768m -Dgosh.port=17179`, working directory `weasis-launcher`.

## Tests

JUnit Jupiter **6.1.2** + Mockito **5.23.0**. No AssertJ. Surefire `<parallel>all</parallel>`. Felix boot is a smoke (`weasis:info`), not a unit test. Do **not** tick CHECKLIST §6 Pass from tests or `weasis:info`.

```bash
mvn -q test
bash scripts/gogo-smoke.sh
```

## Reactor locks

- `weasis-base-parent` / `weasis-dicom-parent` exist; feature bundles parent them.
- Acquire aggregator is `weasis-acquire` (no `weasis-acquire-parent`).
- Directory `weasis-imageio/` vs artifact `weasis-imageio-codec`.
- `${revision}${changelist}` — do not hard-code versions in child POMs.
- `.internal` packages are bnd-private.
