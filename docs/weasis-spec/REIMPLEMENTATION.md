# Reimplementation rules

## What “prove knowledge of Weasis” means here

A third-party reviewer comparing **this repository** to **Weasis 4.7.3 source layout** should see the same:

1. Root reactor modules and feature bundle directories.
2. Maven `groupId` / `artifactId` / OSGi `Bundle-SymbolicName` for shipping bundles.
3. Public Java package trees (`org.weasis.*`) and the documented type names for viewers, codecs, commands, and graphics.
4. Felix start levels and `felix.auto.start.*` / `felix.auto.install.*` catalog.
5. Gogo command names, POSIX usage, and preference keys from the 4.7 docs.
6. Observable Have behavior (decode EVR LE CT, W/L paint, import DICOMDIR, measure geometry, KO/PR objects, …).

They should **not** see copied method bodies from `nroduit/Weasis`. Clean-room: implement from this spec.

## Have vs Pass

| Token | Evidence |
|---|---|
| **Have** | Unit test and/or Gogo smoke for a documented Done criterion. Headless Cloud Agent may tick Have. |
| **Pass** | A human (or headed GUI agent) completes the matching weasis.org tutorial. Tests and `weasis:info` never tick Pass. |

## Loops

Independent teams SCORE the clone against this spec. A SCORE may only change Have boxes. Composer/Grok review seasons must not collapse remaining WPs into a fake WP-0–4 Pass.

## Forbidden shortcuts

- Ticking §6 Pass from CI.
- Skipping unsupported SOP classes silently without the 4.7.0 Information popup contract (Have: notifier exists).
- Shipping PHI or patient DICOM in git.
- Hard-coding `${revision}${changelist}` in child POMs.
- Putting clone code under a Go `cmd/viewer` tree (this is Weasis-as-Weasis).

## MX identifiers (from prior SCORE / prefs docs)

| Id | Contract |
|---|---|
| MX-03 | Shipping `weasis-distributions/etc/config/base.json`: INFO, stack 3, `felix.log.level` 1. Launcher `conf/base.json` may be DEBUG. |
| MX-06 | Direct vs Manual proxy: switching Direct does not clear in-session hosts; Manual has no user/password fields. |
| MX-07 | Monitor calibration ≠ session Manual calibration; Freeze Parameters ≠ Freeze Image. |
| MX-10 | `download.concurrent.series` = 3 |
| MX-11 | `download.concurrent.series.images` = 4 |
| MX-14 | FoR sync and manual sync are distinct (WP-6). |
| MX-15 | 3D VR requires OpenGL 3.3+; refuse `llvmpipe`; N/A when no GPU. |
| MX-16 | Gogo `gosh.port` **17179**. Dicomizer **17181**. |
| MX-21 | No MPR types in WP-4; MPR is WP-7. |

## Build gates (not Pass)

```bash
export JAVA_HOME=$HOME/tools/jdk-25
mvn -q test
mvn -q spotless:check
bash scripts/gogo-smoke.sh
```

JDK **25+**, Maven **3.8.1+**. JUnit Jupiter **6.1.2**, Mockito **5.23.0**, no AssertJ.
