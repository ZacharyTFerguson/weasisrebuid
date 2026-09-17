# Weasis 4.7.3 specification (this repository)

This tree is a **clean-room reconstruction** of [Weasis](https://weasis.org) **4.7.3** (Java, Apache Felix 7.0.5, Swing, OSGi Declarative Services). It is **not** a fork of `nroduit/Weasis` and must not paste upstream Java bodies.

The specification in this directory is built from **public Weasis 4.7.3 documentation** (weasis.org, pin `4.7`) plus the **public module/file layout** of tag `v4.7.3` (paths and artifact IDs only). Implementation follows these documents.

| Document | Role |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | OSGi plugin categories, fragments, Felix start levels |
| [MODULES.md](MODULES.md) | Maven reactor / bundle symbolic names matching 4.7.3 |
| [COMMANDS.md](COMMANDS.md) | Gogo / `weasis://` command surface |
| [SHORTCUTS.md](SHORTCUTS.md) | Default keyboard/mouse map (customizable since 4.7.0) |
| [PREFERENCES.md](PREFERENCES.md) | `base.json` keys, F/A/AP types, load order |
| [TUTORIALS.md](TUTORIALS.md) | User-visible viewers and Have vs Pass mapping |
| [WORK-PACKAGES.md](WORK-PACKAGES.md) | WP-0…WP-15 rebuild slices |
| [CHECKLIST.md](CHECKLIST.md) | Have (unit/smoke) vs Pass (live GUI tutorials) |
| [REIMPLEMENTATION.md](REIMPLEMENTATION.md) | Clean-room rules, honesty, MX-ids |
| [SOURCE-SURFACE.md](SOURCE-SURFACE.md) | Path/class oracle vs `nroduit/Weasis` `v4.7.3` |
| [SRS.md](SRS.md) | Functional requirements extracted from the docs |
| [TUTORIALS-SPRINT-INVENTORY.md](TUTORIALS-SPRINT-INVENTORY.md) | PR #25 tutorial-order backlog vs headed PASS |
| [SPEC-STARTER-PLAN.md](SPEC-STARTER-PLAN.md) | WP entry-point map |
| [starters/](starters/) | One starter doc per WP (main Java path) |

**Pin:** Weasis **4.7.3**. Docs site version **4.7**. License of this clone: EPL-2.0 OR Apache-2.0.

Do **not** tick CHECKLIST §6 Pass from `weasis:info`, JUnit, or headless smoke.
