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
| Lint (Android) | `./gradlew lintAospDebug lintAospRelease lintGmsDebug lintGmsRelease` | Every Android module has these four variants; plain `lint` checks only the default one. Pure-Kotlin modules have no Android Lint |
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
- Before calling a change done, run Detekt, unit tests, and Android Lint for all four variants:
  `./gradlew detekt test lintAospDebug lintAospRelease lintGmsDebug lintGmsRelease`. To save time,
  prefix each task with the touched module's path (e.g. `:feature:location:lintGmsRelease`), but
  omit the lint tasks for pure-Kotlin modules.
- Keep comments evergreen: describe the code as it is, not the change that produced it.
- Skip backward-compatibility shims unless asked; all modules ship together in one app.
- Use conventional commits for commit messages and PR titles.

## Skills

Definitions live in `.agents/skills/` (symlinked into `.claude/skills/`): `kotlin-testing`,
`kotlin-kdoc`, `kotlin-libraries`, `kotlin-metro`.
