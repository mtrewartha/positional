# Hilt to Metro Migration Design

## Overview

Migrate Positional's dependency injection from Hilt to Metro, a Kotlin compiler plugin-based DI
framework. This is a single atomic conversion — no incremental interop period.

## Context

### Current Hilt Setup

- 8 Hilt modules across app, core, and feature modules
- 4 ViewModels using `@HiltViewModel` with constructor injection
- Flavor-specific DI for AOSP/GMS location providers (separate source sets)
- No assisted injection, no custom scopes, no Hilt test infrastructure
- Components used: `SingletonComponent`, `ActivityRetainedComponent`, `ViewModelComponent`
- Tests use manual test doubles — no dependency on Hilt at test time
- `feature/sun` is a pure JVM module (uses `hilt-core`, not `hilt-android`)

### Why Metro

- Kotlin compiler plugin (FIR + IR) — not KSP-based, significantly faster incremental builds
- Anvil-style aggregation (`@ContributesTo`, `@ContributesBinding`) for decoupled modular DI
- Simpler API surface with fewer concepts than Hilt
- Active development by Zac Sweers (creator of Anvil)

## Design Decisions

### Single Scope

Simplify from three Hilt scopes (`SingletonComponent`, `ActivityRetainedComponent`,
`ViewModelComponent`) to a single `AppScope`. All bindings live at the app-level scope.

Rationale: The `ActivityRetainedComponent` binding (DateTimeFormatter) and `ViewModelComponent`
binding (CompassModule) don't benefit from their narrower scopes in this project. Unifying
simplifies the mental model.

### Anvil-Style Aggregation (Approach A)

Feature and core modules contribute bindings via `@ContributesTo(AppScope::class)` and
`@ContributesBinding(AppScope::class)`. The app module's `@DependencyGraph(AppScope::class)`
automatically merges all contributions at compile time.

This preserves the decoupled architecture: feature modules don't know about the graph, and adding a
new module doesn't require touching the app module's graph definition.

### minSdk Raised to 28

Required for MetroX Android's `AppComponentFactory` integration, which enables constructor injection
of Android components (Activities).

## Architecture

### Dependency Graph

A single top-level graph defined in the app module:

```kotlin
@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph
```

Graph creation in the Application class:

```kotlin
class PositionalApplication : Application(), MetroApplication {
    override val appGraph by lazy { createGraph<AppGraph>(this) }
}
```

### Module-to-Metro Mapping

#### `@Binds` modules become `@ContributesBinding`

No separate module file needed — the annotation goes on the implementation class:

| Hilt Module | Metro Replacement |
|---|---|
| `SunModule` (`@Binds SolarTimesRepository`) | `@ContributesBinding(AppScope::class)` on `MainSolarTimesRepository` |
| `FormatterModule` (`@Binds DateTimeFormatter`) | `@ContributesBinding(AppScope::class)` on `SystemDateTimeFormatter` |
| `AospLocatorModule` (`@Binds Locator`) | `@ContributesBinding(AppScope::class)` on `AospLocator` (+ `@SingleIn(AppScope::class)`) |
| `GmsLocatorModule` (`@Binds Locator`) | `@ContributesBinding(AppScope::class)` on `GmsLocator` |

#### `@Provides` modules become `@ContributesTo` interfaces

| Hilt Module | Metro Replacement |
|---|---|
| `AppModule` (Clock, CoroutineContext, Locale, WindowManager) | `@ContributesTo(AppScope::class) interface AppProviders` |
| `CompassModule` (Compass?, SensorManager) | `@ContributesTo(AppScope::class) interface CompassProviders` |
| `LocationModule` (LocationManager) | `@ContributesTo(AppScope::class) interface LocationProviders` |
| `GmsLocatorModule` companion (FusedLocationProviderClient) | `@ContributesTo(AppScope::class) interface GmsLocationProviders` |
| `SettingsModule` (SettingsRepository) | `@ContributesTo(AppScope::class) interface SettingsProviders` (with `@SingleIn` on provider) |

#### `AospCompass` is not graph-constructable

`AospCompass` has an `@Inject constructor` but takes specific `Sensor` instances (accelerometer,
magnetic field, rotation vector) that are not individually provided as graph bindings. It must remain
manually instantiated inside the `CompassProviders` `@Provides` function (as it is today in
`CompassModule.compass()`). The `@Inject` annotation on `AospCompass` should be removed since the
graph never constructs it directly.

#### `SettingsModule` stays as a `@Provides` provider

`DataStoreSettingsRepository` has an `@Inject constructor(context: Context)`, which would make
`@ContributesBinding` possible. However, we keep it as a `@ContributesTo` provider interface to
apply `@SingleIn(AppScope::class)` scoping on the provider function, matching the current singleton
behavior.

#### Context injection

Hilt's `@ApplicationContext Context` is replaced by a `@DependencyGraph.Factory` parameter that
provides `Context` as a binding via `@Provides`. All provider interfaces that need `Context` receive
it from the graph.

#### CoroutineContext default parameters

`AospLocator` and `GmsLocator` both have `coroutineContext: CoroutineContext = Dispatchers.Default`
as a default constructor parameter. The `AppProviders` interface provides a `CoroutineContext`
binding (`Dispatchers.Default`). With Metro, the graph binding takes precedence over the default
value. The defaults can be kept as a safety net or removed — either works since the graph supplies
the value.

### ViewModel Integration

ViewModels use `metrox-viewmodel` and `metrox-viewmodel-compose`:

```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey(CompassViewModel::class)
@Inject
class CompassViewModel(
    compass: Compass?,
    locator: Locator,
    settings: SettingsRepository
) : ViewModel()
```

All 4 ViewModels follow this pattern. `javax.inject.Inject` becomes `metro.Inject`.

In Composables, `hiltViewModel()` becomes `metroViewModel()`:

```kotlin
// Feature views (CompassView, LocationView, SunView, SettingsView)
val viewModel: CompassViewModel = metroViewModel(checkNotNull(LocalViewModelStoreOwner.current))

// MainView (SettingsViewModel) — no explicit ViewModelStoreOwner needed
val viewModel: SettingsViewModel = metroViewModel()
```

`AppGraph` extends `ViewModelGraph`, providing `MetroViewModelFactory`. `MainActivity` sets up
`LocalMetroViewModelFactory` via `CompositionLocalProvider`.

### Flavor-Specific DI

The AOSP/GMS split remains in flavor-specific source sets (`src/aosp/`, `src/gms/`). Each flavor's
`Locator` implementation uses `@ContributesBinding(AppScope::class)` — same pattern, different
source set. The `GmsLocationProviders` interface (providing `FusedLocationProviderClient`) also
lives in `src/gms/`.

In the GMS flavor, `GmsLocator` depends on `AospLocator` directly (as a fallback). `AospLocator`
is injectable via its `@Inject constructor` as a concrete type in the GMS flavor — it just doesn't
have `@ContributesBinding` there (that annotation only exists in `src/aosp/`). This works because
Metro can construct any `@Inject`-annotated class without it needing to be explicitly contributed.

### MainActivity

Currently field-injects `DateTimeFormatter` via `@AndroidEntryPoint`. With MetroX,
`AppComponentFactory` handles Activity construction. `DateTimeFormatter` is accessed from the graph
and provided via `LocalDateTimeFormatter` through `CompositionLocalProvider`.

### JVM Module: `feature/sun`

`feature/sun` is a pure JVM module (not Android). It currently uses `hilt-core` (not
`hilt-android`). Metro's Gradle plugin works with pure JVM modules — only the base Metro compiler
plugin and runtime are needed, not the MetroX Android artifacts. The `@ContributesBinding` on
`MainSolarTimesRepository` and `@Inject` on its constructor work identically in JVM modules.

## Build Configuration

### Remove

- `com.google.dagger:hilt-android`
- `com.google.dagger:hilt-core`
- `com.google.dagger:hilt-compiler` (KSP processor)
- `javax.inject:javax.inject`
- `androidx.hilt:hilt-navigation-compose`
- `com.google.dagger.hilt.android` Gradle plugin
- `hilt { enableAggregatingTask = true }` block in `app/build.gradle.kts`
- `classpath(libs.javapoet.get())` in root `build.gradle.kts` (Dagger workaround)
- All Hilt/Dagger Kover exclusion patterns:
    - `excludesAnnotatedBy`: `dagger.*`
    - `excludedClasses`: `hilt_aggregated_deps.*`, `*Hilt_*`, `*_Hilt*`, `*.*_*Injector*`,
      `*_*Factory*`
    - Evaluate whether Metro generates classes that need their own exclusion patterns

### Add

- `dev.zacsweers.metro` Gradle plugin (v0.11.4 or latest)
- `metrox-android` (for `MetroApplication`, `MetroAppComponentProviders`) — Android modules only
- `metrox-viewmodel` (for `ViewModelGraph`, `@ViewModelKey`, `MetroViewModelFactory`) — Android
  modules only
- `metrox-viewmodel-compose` (for `metroViewModel()`, `LocalMetroViewModelFactory`) — Android
  modules only

### Other Changes

- Metro Gradle plugin applied to all modules participating in DI (including JVM modules like
  `feature/sun`)
- minSdk raised from 24 to 28 in `app/build.gradle.kts` and convention plugin
- KSP `ksp(libs.google.hilt.compiler)` lines removed; KSP itself removed if no other processors
  use it
- Update `.claude/rules/kotlin/android/dependency-injection.md` to reflect Metro conventions instead
  of Hilt

## Testing

### Zero test impact

Tests use manual test doubles (`TestSettingsRepository`, `TestLocator`,
`TestSolarTimesRepository`) — no Hilt test infrastructure exists. All existing tests should pass
without modification.

### Verification strategy

1. Project compiles successfully with Metro
2. All existing tests pass without modification
3. App launches and basic navigation works (manual smoke test)

## Risk: `internal` Visibility with Aggregation

Metro's documentation doesn't explicitly address whether `@ContributesTo` interfaces or
`@ContributesBinding` classes can be `internal`. Since Metro is a compiler plugin (FIR + IR), it
theoretically can handle `internal` declarations across modules.

**Mitigation:** Validate `internal` visibility early — try marking a `@ContributesTo` interface as
`internal` and confirm it compiles. If it doesn't work, contributed interfaces will need to be
`public` (functionally equivalent to current Hilt modules, whose provided types are already public).
