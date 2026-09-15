# Source-surface oracle

Goal: a reviewer who knows Weasis 4.7.3 **source layout** should recognize this tree as Weasis (packages, types, bundles), while method bodies remain independently written.

## Fixtures

- [fixtures/weasis-4.7.3-main-java.txt](fixtures/weasis-4.7.3-main-java.txt) — 985 `src/main/java/**/*.java` paths from `nroduit/Weasis` **v4.7.3**.
- [fixtures/weasis-4.7.3-poms.txt](fixtures/weasis-4.7.3-poms.txt) — POM paths from the same tag.

## Invariants (tested)

1. Every `src/main/java/**/*.java` in this clone is either in the 4.7.3 fixture **or** listed in `fixtures/clone-only-main-java.txt` (documented extras such as SCORE helpers).
2. Required Have types for a closed WP exist at the Weasis path.
3. The gap list is generated (`scripts/source-surface-report.py`) and must not shrink the fixture — we fill paths, we do not invent parallel package names.

## Honesty

Matching a path does **not** mean the type is feature-complete. Empty `buildShape()` is a WP-5 hole even if `AngleToolGraphic.java` exists. Module jars that install but do not decode a SOP are WP-10 holes.

Third-party “cannot tell the difference from source” is **false** until:

- POM module set equals 4.7.3 feature bundles (archetype/snap optional).
- Main Java path coverage is complete for shipping plugins (or remaining paths are explicitly deferred in the gap file with a WP id).
- Public command/pref/start-level surface matches the 4.7 docs.
- `mvn test` and Gogo smoke stay green.
