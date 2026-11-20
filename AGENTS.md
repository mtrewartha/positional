# AGENTS.md

> **Project:** Positional — a minimalist, free, open-source Android app for location, compass, and
> solar times.
> **Core constraint:** Privacy. The user's location is shown in the app and then discarded; it is
> never stored or transmitted. The only data that leaves the device is anonymous crash reports, plus
> whatever the user explicitly chooses to share or export.

## Toolchain

| Intent | Command | Notes |
|---|---|---|
| Lint (Kotlin) | `./gradlew detekt` | Config: `detekt.yml` over Detekt defaults; per-module `detekt-baseline.xml` |
| Lint (Android) | `./gradlew :<module>:lint<Variant>` per variant | Plain `lint` checks only the default variant. List a module's lint tasks with `./gradlew :<module>:tasks --group verification`. Pure-Kotlin modules have no Android Lint |
| Unit tests | `./gradlew test` | Runs every flavor/build type; scope with e.g. `:feature:sun:test` |
| Coverage | `./gradlew koverXmlReport` | |
| Instrumented tests | `./gradlew aospGroupAospDebugAndroidTest` (or `gms…`) | Gradle Managed Devices |

CI (`.github/workflows/checks.yml`) runs all of the above as separate jobs.

## Module layout

- `core/<x>`: low-level building blocks shared across features. Never depends on `feature/`.
- `feature/<x>`: a feature's domain code (models, repositories). Prefer pure Kotlin.
- `feature/<x>/ui`: that feature's UI (Composables, ViewModels). May depend on any `feature/<y>`
  domain module, never on another `feature/<y>/ui`.
- `app`: composes the `ui` modules and owns navigation and the DI graph.
- Flavors `aosp`/`gms` differentiate one app (Google Play Services or not); never use them to build
  separate apps.

## Build gotchas

- AGP 9 built-in Kotlin: there is no `org.jetbrains.kotlin.android` plugin. Convention plugins in
  `build-logic` hook on `com.android.library` / `com.android.application`.
- `android.newDsl=false` does not work in precompiled convention plugin scripts.
- testFixtures compile classpaths don't inherit `implementation(platform(bom))`. If a module exposes
  a BOM-managed `api(...)` dependency, declare the BOM with `api(platform(...))`.
- Formatted coordinate output (e.g. `UtmCoordinates.toString()`, `MgrsFormatter`) uses non-breaking
  spaces (`\u00A0`). Test assertions must use them too.

## Judgment boundaries

**NEVER**
- Add code that persists or transmits location, or any analytics or tracking.
- Commit directly to `main`.

**ASK**
- Before adding a dependency, Android permission, or network access.
- Before adding entries to a `detekt-baseline.xml` or suppressing a Detekt finding.

**ALWAYS**
- Before calling a change done, run `detekt` and `test` for each touched module, plus
  `lint<Variant>` for every variant of each touched Android module. Modules with product flavors
  (e.g. `app`: `lintAospDebug`, `lintAospRelease`, `lintGmsDebug`, `lintGmsRelease`) need all of
  them, not just `lint`.
- Keep comments evergreen: describe the code as it is, not the change that produced it.
- Skip backward-compatibility shims unless asked; all modules ship together in one app.
- Use conventional commits for commit messages and PR titles.
- Before changing a feature, read its spec in `specs/` (see Specs). When a change alters behavior a
  spec describes, update the spec in the same commit, including removing any _(pending POS-n)_
  marker the change resolves.

## Specs

Living feature specs live in `specs/`, mirroring the module layout (e.g. `specs/location/spec.md`
for `feature/location`, `specs/location/ui/spec.md` for `feature/location/ui`). Start new specs from
`specs/TEMPLATE.md`. A spec states intended behavior; items marked _(pending POS-n)_ (or tagged
`@pending` in Gherkin) aren't implemented yet, and the linked Linear issue tracks the change.

- **Adding intended behavior:** create the Linear issue first, linking the spec section and the
  scenarios it will make pass, then mark each unimplemented
  sentence and Definition of Done item with _(pending [POS-n](link))_ and tag each unimplemented
  scenario `@pending @POS-n`. A marker covers only the sentence it follows; keep implemented
  behavior in separate sentences and untagged scenarios.
- **Implementing it:** in the same commit, remove the issue's markers and tags, and check each
  Definition of Done item that a test now proves.
- **Checking boxes:** check a Definition of Done item only when a test proves exactly that claim.
  An item is in one of three states: checked (proven by a test), unchecked without a marker
  (believed implemented, not yet proven), or unchecked with a marker (not implemented yet).
- **Reviewing:** treat `@pending` scenarios as expected to fail; report them as tracked gaps, not
  regressions. Report an untagged scenario the code doesn't satisfy as a defect.
