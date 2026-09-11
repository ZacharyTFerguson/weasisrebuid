# WP-14 i18n + native zip (Have, not §6 Pass)

Pin: Weasis **4.7.3**. Clean-room. GUI: not run.

## i18n

Each host bundle has `Messages.java` + `messages.properties`. Fragments live in `weasis-i18n-dist/` and attach at **packaging** (`resources/i18n/`). They are **not** a `felix.auto.start.*` line.

Launcher `org.weasis.launcher.Messages` uses a `static final ResourceBundle` — **cannot hot-swap**.

File > Preferences > General > Language lists only Transifex coverage **≥ 30%**. `locale.lang.code` default **en**. `locale.format.code` default **system**. Language and regional format are independent.

## Native zip

`maven.local.repo` is **DEV only**. Documented no-m2 packager path:

1. `mvn install` (reactor)
2. `bash weasis-distributions/script/stage-from-reactor.sh` (copies `*/target/*.jar`, not `~/.m2`)
3. `bash weasis-distributions/script/package-weasis.sh --jdk "$JAVA_HOME" --from-target weasis-distributions/target/native-stage`

The zip contains `Weasis.jar`, `resources/bundle`, rewritten `resources/conf/base.json` (`file:${weasis.resources.path}/bundle/…`), `resources/i18n`, portable CD dirs, and `Weasis.sh`.

`mvn -P compressXZ -f weasis-distributions package` is the second Maven invocation (enforces script + `base.json`).

GH Actions outline: `.github/workflows/native-zip.yml`.

## Honesty

JUnit + packager tests are **Have**. Live GUI language switch and a Team B click of the unzipped app are **not** CHECKLIST §6 Pass.
