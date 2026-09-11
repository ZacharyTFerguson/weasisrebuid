# weasis-i18n-dist

Translation **fragment** packs. They attach at **distribution packaging**, not as a `felix.auto.start.*` line in `base.json`.

Each folder is one OSGi fragment (`Fragment-Host` = the UI host bundle BSN). The native packager jars these into `resources/i18n/` and `BundleInstaller.installI18nFragments` installs them without starting.

Launcher i18n (`org.weasis.launcher.Messages`) cannot hot-swap; restart after replacing launcher resources.

Do not add this tree to the root Maven reactor.
