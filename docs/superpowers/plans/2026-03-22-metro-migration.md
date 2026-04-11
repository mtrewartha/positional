# Hilt to Metro Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace Hilt dependency injection with Metro across all modules.

**Architecture:** Single `AppScope` graph with Anvil-style aggregation. Feature/core modules contribute bindings via `@ContributesTo`/`@ContributesBinding`. MetroX provides Android and ViewModel integration.

**Tech Stack:** Metro 0.11.4+, MetroX Android/ViewModel/ViewModel-Compose, Kotlin compiler plugin

**Spec:** `docs/superpowers/specs/2026-03-22-metro-migration-design.md`

---

## File Structure

### Files to Create

- `core/di/build.gradle.kts` — minimal pure Kotlin module for shared DI markers
- `core/di/src/main/kotlin/io/trewartha/positional/AppScope.kt` — scope marker object
- `app/src/main/java/io/trewartha/positional/AppProviders.kt` — `@ContributesTo` provider interface (Clock, CoroutineContext, Locale, WindowManager)
- `app/src/main/java/io/trewartha/positional/AppGraph.kt` — `@DependencyGraph` interface
- `feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorBinding.kt` — AOSP-flavor binding of `AospLocator` as `Locator`

### Files to Modify

**Build configuration:**
- `gradle/libs.versions.toml` — remove Hilt/Dagger/KSP/javapoet/javax entries, add Metro entries
- `build.gradle.kts` — remove javapoet classpath
- `settings.gradle.kts` — remove Hilt/Dagger Kover exclusion patterns, add `:core:di` module
- `build-logic/convention/src/main/kotlin/io/trewartha/positional/library.android.gradle.kts` — minSdk 24 to 28
- `app/build.gradle.kts` — remove Hilt plugin/KSP/deps, add Metro plugin/deps, minSdk 24 to 28
- `app/src/main/AndroidManifest.xml` — add `android:appComponentFactory` for MetroX
- `core/ui/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin, add `core:di` dep
- `feature/compass/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin, add `core:di` dep
- `feature/compass/ui/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin/metrox deps
- `feature/location/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin, add `core:di` dep
- `feature/location/ui/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin/metrox deps
- `feature/settings/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin, add `core:di` dep
- `feature/settings/ui/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin/metrox deps
- `feature/sun/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin, add `core:di` dep
- `feature/sun/ui/build.gradle.kts` — remove KSP/Hilt deps, add Metro plugin/metrox deps

**Source files — app module:**
- `app/src/main/java/io/trewartha/positional/PositionalApplication.kt` — `@HiltAndroidApp` to `MetroApplication`
- `app/src/main/java/io/trewartha/positional/MainActivity.kt` — `@AndroidEntryPoint` to Metro constructor injection
- `app/src/main/java/io/trewartha/positional/MainView.kt` — `hiltViewModel()` to `metroViewModel()`

**Source files — core module:**
- `core/ui/src/main/kotlin/io/trewartha/positional/core/ui/format/SystemDateTimeFormatter.kt` — add `@ContributesBinding`

**Source files — feature/compass:**
- `feature/compass/src/main/kotlin/io/trewartha/positional/compass/AospCompass.kt` — remove `@Inject`
- `feature/compass/src/main/kotlin/io/trewartha/positional/compass/CompassModule.kt` — rewrite as `CompassProviders.kt`
- `feature/compass/ui/src/main/kotlin/io/trewartha/positional/compass/ui/CompassViewModel.kt` — `@HiltViewModel` to Metro ViewModel annotations
- `feature/compass/ui/src/main/kotlin/io/trewartha/positional/compass/ui/CompassView.kt` — `hiltViewModel()` to `metroViewModel()`

**Source files — feature/location:**
- `feature/location/src/main/kotlin/io/trewartha/positional/location/AospLocator.kt` — `javax.inject.Inject` to `dev.zacsweers.metro.Inject`
- `feature/location/src/main/kotlin/io/trewartha/positional/location/LocationModule.kt` — rewrite as `LocationProviders.kt`
- `feature/location/src/gms/kotlin/io/trewartha/positional/location/GmsLocator.kt` — `javax.inject.Inject` to `dev.zacsweers.metro.Inject`
- `feature/location/src/gms/kotlin/io/trewartha/positional/location/GmsLocatorModule.kt` — rewrite as `GmsLocationProviders.kt`
- `feature/location/ui/src/main/kotlin/io/trewartha/positional/location/ui/LocationViewModel.kt` — `@HiltViewModel` to Metro ViewModel annotations
- `feature/location/ui/src/main/kotlin/io/trewartha/positional/location/ui/LocationView.kt` — `hiltViewModel()` to `metroViewModel()`

**Source files — feature/settings:**
- `feature/settings/src/main/kotlin/io/trewartha/positional/settings/DataStoreSettingsRepository.kt` — remove `@Inject` (provided via `@Provides` instead)
- `feature/settings/src/main/kotlin/io/trewartha/positional/settings/SettingsModule.kt` — rewrite as `SettingsProviders.kt`
- `feature/settings/ui/src/main/kotlin/io/trewartha/positional/settings/ui/SettingsViewModel.kt` — `@HiltViewModel` to Metro ViewModel annotations
- `feature/settings/ui/src/main/kotlin/io/trewartha/positional/settings/ui/SettingsView.kt` — `hiltViewModel()` to `metroViewModel()`

**Source files — feature/sun:**
- `feature/sun/src/main/kotlin/io/trewartha/positional/sun/MainSolarTimesRepository.kt` — `javax.inject.Inject` to `dev.zacsweers.metro.Inject`, add `@ContributesBinding`
- `feature/sun/ui/src/main/kotlin/io/trewartha/positional/sun/ui/SunViewModel.kt` — `@HiltViewModel` to Metro ViewModel annotations
- `feature/sun/ui/src/main/kotlin/io/trewartha/positional/sun/ui/SunView.kt` — `hiltViewModel()` to `metroViewModel()`

### Files to Delete

- `app/src/main/java/io/trewartha/positional/AppModule.kt` — replaced by `AppProviders.kt`
- `core/ui/src/main/kotlin/io/trewartha/positional/core/ui/FormatterModule.kt` — replaced by `@ContributesBinding` on `SystemDateTimeFormatter`
- `feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorModule.kt` — replaced by `AospLocatorBinding.kt`
- `feature/sun/src/main/kotlin/io/trewartha/positional/sun/SunModule.kt` — replaced by `@ContributesBinding` on `MainSolarTimesRepository`

---

## Task 1: Update Version Catalog

**Files:**
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Add Metro entries**

Add to `[versions]`:
```toml
metro = "0.11.4"
```

Add to `[plugins]`:
```toml
metro = { id = "dev.zacsweers.metro", version.ref = "metro" }
```

Add to `[libraries]`:
```toml
metro-runtime = { group = "dev.zacsweers.metro", name = "metro-runtime", version.ref = "metro" }
metrox-android = { group = "dev.zacsweers.metro", name = "metrox-android", version.ref = "metro" }
metrox-viewmodel = { group = "dev.zacsweers.metro", name = "metrox-viewmodel", version.ref = "metro" }
metrox-viewmodel-compose = { group = "dev.zacsweers.metro", name = "metrox-viewmodel-compose", version.ref = "metro" }
```

- [ ] **Step 2: Remove Hilt/Dagger/KSP/javapoet/javax entries**

Remove from `[versions]`: `google-dagger`, `google-ksp`, `javapoet`, `javax-inject`, `androidx-hilt`

Remove from `[plugins]`: `google-dagger-hilt-android`, `google-ksp`

Remove from `[libraries]`: `google-dagger-android`, `google-dagger-compiler`, `google-dagger-core`, `google-hilt-android`, `google-hilt-core`, `google-hilt-compiler`, `google-ksp`, `javapoet`, `javax-inject`, `androidx-hilt-navigation-compose`, `androidx-hilt-navigation-fragment`

- [ ] **Step 3: Commit**

```bash
git add gradle/libs.versions.toml
git commit -m "build: replace Hilt/Dagger catalog entries with Metro"
```

---

## Task 2: Update Root Build Files

**Files:**
- Modify: `build.gradle.kts`
- Modify: `settings.gradle.kts`

- [ ] **Step 1: Remove javapoet classpath from `build.gradle.kts`**

Remove the entire `buildscript` block (lines 1-4):
```kotlin
buildscript {
    dependencies {
        classpath(libs.javapoet.get()) // https://github.com/google/dagger/issues/3068
    }
}
```

- [ ] **Step 2: Update `settings.gradle.kts` Kover exclusions**

Remove all Dagger/Hilt patterns. The `kover` block becomes:

```kotlin
kover {
    enableCoverage()

    reports {
        excludesAnnotatedBy.addAll(
            "*.Generated*"
        )
        excludedClasses.addAll(
            "io.trewartha.positional.BuildConfig",
        )
    }
}
```

- [ ] **Step 3: Add `:core:di` to module includes in `settings.gradle.kts`**

Add `:core:di` to the `include(...)` block, after `:core:error`:

```kotlin
include(
    ":app",
    ":core:di",
    ":core:error",
    // ... rest unchanged
)
```

- [ ] **Step 4: Commit**

```bash
git add build.gradle.kts settings.gradle.kts
git commit -m "build: remove Hilt build workarounds, Kover exclusions, add core:di module"
```

---

## Task 3: Create core/di Module with AppScope

**Files:**
- Create: `core/di/build.gradle.kts`
- Create: `core/di/src/main/kotlin/io/trewartha/positional/AppScope.kt`

- [ ] **Step 1: Create `core/di/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.positional.library.jvm)
}
```

- [ ] **Step 2: Create `AppScope.kt`**

File: `core/di/src/main/kotlin/io/trewartha/positional/AppScope.kt`

```kotlin
package io.trewartha.positional

/**
 * Scope marker for the application-level dependency graph
 */
public object AppScope
```

- [ ] **Step 3: Commit**

```bash
git add core/di/
git commit -m "feat: add core/di module with AppScope marker"
```

---

## Task 4: Update Build Configuration — Convention Plugin & App Module

**Files:**
- Modify: `build-logic/convention/src/main/kotlin/io/trewartha/positional/library.android.gradle.kts`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Raise minSdk to 28 in convention plugin**

In `library.android.gradle.kts`, change `minSdk = 24` to `minSdk = 28`.

- [ ] **Step 2: Update `app/build.gradle.kts`**

In plugins block:
- Remove `alias(libs.plugins.google.ksp)`
- Remove `alias(libs.plugins.google.dagger.hilt.android)`
- Add `alias(libs.plugins.metro)`

Change `minSdk = 24` to `minSdk = 28`.

In dependencies block:
- Remove `ksp(libs.google.hilt.compiler)`
- Remove `implementation(libs.androidx.hilt.navigation.compose)`
- Remove `implementation(libs.google.hilt.android)`
- Add `implementation(projects.core.di)`
- Add `implementation(libs.metrox.android)`
- Add `implementation(libs.metrox.viewmodel)`
- Add `implementation(libs.metrox.viewmodel.compose)`

Remove the entire `hilt {}` block.

- [ ] **Step 3: Add `appComponentFactory` to `AndroidManifest.xml`**

Add `android:appComponentFactory` attribute to the `<application>` tag. MetroX provides `MetroAppComponentFactory`:

```xml
<application
    android:name=".PositionalApplication"
    android:appComponentFactory="dev.zacsweers.metrox.android.MetroAppComponentFactory"
    android:dataExtractionRules="@xml/backup_rules"
    ...
```

Note: The exact class name for MetroX's `AppComponentFactory` should be verified against Metro's documentation. If MetroX generates a project-specific one, the approach may differ.

- [ ] **Step 4: Commit**

```bash
git add build-logic/ app/build.gradle.kts app/src/main/AndroidManifest.xml
git commit -m "build: configure Metro plugin/deps in app module, raise minSdk to 28"
```

---

## Task 5: Update Build Configuration — All Other Modules

**Files:**
- Modify: `core/ui/build.gradle.kts`
- Modify: `feature/compass/build.gradle.kts`
- Modify: `feature/compass/ui/build.gradle.kts`
- Modify: `feature/location/build.gradle.kts`
- Modify: `feature/location/ui/build.gradle.kts`
- Modify: `feature/settings/build.gradle.kts`
- Modify: `feature/settings/ui/build.gradle.kts`
- Modify: `feature/sun/build.gradle.kts`
- Modify: `feature/sun/ui/build.gradle.kts`

- [ ] **Step 1: Update non-UI modules**

For each of `core/ui`, `feature/compass`, `feature/location`, `feature/settings`, `feature/sun`:

1. Remove `alias(libs.plugins.google.ksp)` from plugins block
2. Add `alias(libs.plugins.metro)` to plugins block
3. Remove `ksp(libs.google.hilt.compiler)` from dependencies
4. Remove `implementation(libs.google.hilt.android)` (or `libs.google.hilt.core` for `feature/sun`)
5. Remove `implementation(libs.javax.inject)` where present
6. Add `implementation(projects.core.di)` to dependencies

- [ ] **Step 2: Update UI modules**

For each of `feature/compass/ui`, `feature/location/ui`, `feature/settings/ui`, `feature/sun/ui`:

1. Remove `alias(libs.plugins.google.ksp)` from plugins block
2. Add `alias(libs.plugins.metro)` to plugins block
3. Remove `ksp(libs.google.hilt.compiler)` from dependencies
4. Remove `implementation(libs.androidx.hilt.navigation.compose)`
5. Remove `implementation(libs.google.hilt.android)`
6. Add `implementation(libs.metrox.viewmodel)`
7. Add `implementation(libs.metrox.viewmodel.compose)`

Note: UI modules get `core:di` transitively through their `api(projects.feature.xxx)` dependencies.

- [ ] **Step 3: Verify Metro plugin works with JVM module (`feature/sun`)**

`feature/sun` uses `positional.library.jvm` (no Android plugin). Metro's Gradle plugin should work with pure Kotlin/JVM modules. If it requires the Android plugin, this is a blocker — check Metro docs or test compilation.

- [ ] **Step 4: Commit**

```bash
git add core/ui/build.gradle.kts feature/*/build.gradle.kts feature/*/ui/build.gradle.kts
git commit -m "build: migrate all modules from Hilt/KSP to Metro plugin"
```

---

## Task 6: Create AppGraph and AppProviders

**Files:**
- Create: `app/src/main/java/io/trewartha/positional/AppGraph.kt`
- Create: `app/src/main/java/io/trewartha/positional/AppProviders.kt`
- Delete: `app/src/main/java/io/trewartha/positional/AppModule.kt`

- [ ] **Step 1: Create `AppGraph.kt`**

```kotlin
package io.trewartha.positional

import android.app.Application
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
public interface AppGraph : MetroAppComponentProviders, ViewModelGraph {

    @DependencyGraph.Factory
    public interface Factory {
        public fun create(@Provides application: Application): AppGraph
    }
}
```

- [ ] **Step 2: Create `AppProviders.kt`**

```kotlin
package io.trewartha.positional

import android.app.Application
import android.view.WindowManager
import androidx.core.os.LocaleListCompat
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import java.util.Locale
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers

@ContributesTo(AppScope::class)
internal interface AppProviders {

    @Provides
    fun clock(): Clock = Clock.System

    @Provides
    fun coroutineContext(): CoroutineContext = Dispatchers.Default

    @Provides
    fun locale(): Locale = checkNotNull(LocaleListCompat.getDefault()[0])

    @Provides
    fun windowManager(application: Application): WindowManager =
        application.getSystemService(WindowManager::class.java)
}
```

- [ ] **Step 3: Delete `AppModule.kt`**

```bash
rm app/src/main/java/io/trewartha/positional/AppModule.kt
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/io/trewartha/positional/AppGraph.kt app/src/main/java/io/trewartha/positional/AppProviders.kt app/src/main/java/io/trewartha/positional/AppModule.kt
git commit -m "feat: add Metro AppGraph and AppProviders, remove Hilt AppModule"
```

---

## Task 7: Convert Application, Activity, and MainView

**Files:**
- Modify: `app/src/main/java/io/trewartha/positional/PositionalApplication.kt`
- Modify: `app/src/main/java/io/trewartha/positional/MainActivity.kt`
- Modify: `app/src/main/java/io/trewartha/positional/MainView.kt`

- [ ] **Step 1: Update `PositionalApplication.kt`**

```kotlin
package io.trewartha.positional

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroApplication
import timber.log.Timber

public class PositionalApplication : Application(), MetroApplication {

    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree())
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
```

- [ ] **Step 2: Update `MainActivity.kt`**

```kotlin
package io.trewartha.positional

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.compose.LocalMetroViewModelFactory
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter

public class MainActivity @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
    private val viewModelFactory: MetroViewModelFactory
) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalDateTimeFormatter provides dateTimeFormatter,
                LocalMetroViewModelFactory provides viewModelFactory
            ) {
                val widthSizeClass = calculateWindowSizeClass(activity = this).widthSizeClass
                MainView(windowWidthSizeClass = widthSizeClass)
            }
        }
    }
}
```

- [ ] **Step 3: Update `MainView.kt`**

Replace `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` with `import dev.zacsweers.metrox.viewmodel.compose.metroViewModel`.

Replace `viewModel: SettingsViewModel = hiltViewModel(),` with `viewModel: SettingsViewModel = metroViewModel(),`.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/io/trewartha/positional/PositionalApplication.kt app/src/main/java/io/trewartha/positional/MainActivity.kt app/src/main/java/io/trewartha/positional/MainView.kt
git commit -m "feat: convert Application and Activity to Metro"
```

---

## Task 8: Convert core/ui Module

**Files:**
- Modify: `core/ui/src/main/kotlin/io/trewartha/positional/core/ui/format/SystemDateTimeFormatter.kt`
- Delete: `core/ui/src/main/kotlin/io/trewartha/positional/core/ui/FormatterModule.kt`

- [ ] **Step 1: Add `@ContributesBinding` to `SystemDateTimeFormatter`**

Replace `import javax.inject.Inject` with:
```kotlin
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.trewartha.positional.AppScope
```

Add `@ContributesBinding` annotation:
```kotlin
@ContributesBinding(AppScope::class)
internal class SystemDateTimeFormatter @Inject constructor(locale: Locale) : DateTimeFormatter {
```

- [ ] **Step 2: Delete `FormatterModule.kt`**

```bash
rm core/ui/src/main/kotlin/io/trewartha/positional/core/ui/FormatterModule.kt
```

- [ ] **Step 3: Commit**

```bash
git add core/ui/
git commit -m "feat: convert FormatterModule to @ContributesBinding on SystemDateTimeFormatter"
```

---

## Task 9: Convert feature/compass Module

**Files:**
- Modify: `feature/compass/src/main/kotlin/io/trewartha/positional/compass/AospCompass.kt`
- Rewrite: `feature/compass/src/main/kotlin/io/trewartha/positional/compass/CompassModule.kt` as `CompassProviders.kt`
- Modify: `feature/compass/ui/src/main/kotlin/io/trewartha/positional/compass/ui/CompassViewModel.kt`
- Modify: `feature/compass/ui/src/main/kotlin/io/trewartha/positional/compass/ui/CompassView.kt`

- [ ] **Step 1: Remove `@Inject` from `AospCompass`**

Remove `import javax.inject.Inject` and the `@Inject` annotation from the constructor. `AospCompass` is manually instantiated in `CompassProviders`, not graph-constructed.

- [ ] **Step 2: Rewrite `CompassModule.kt` as `CompassProviders.kt`**

Rename file and replace contents:

```kotlin
package io.trewartha.positional.compass

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
internal interface CompassProviders {

    @Provides
    fun compass(sensorManager: SensorManager): Compass? =
        try {
            AospCompass(
                sensorManager,
                requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)),
                requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)),
                requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR))
            )
        } catch (_: IllegalArgumentException) {
            null
        }

    @Provides
    fun sensorManager(application: Application): SensorManager =
        application.getSystemService(SensorManager::class.java)
}
```

- [ ] **Step 3: Update `CompassViewModel.kt`**

Remove `@HiltViewModel` annotation. Remove `import dagger.hilt.android.lifecycle.HiltViewModel` and `import javax.inject.Inject`. Add:

```kotlin
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.trewartha.positional.AppScope
```

Replace annotations:
```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey(CompassViewModel::class)
public class CompassViewModel @Inject constructor(
```

- [ ] **Step 4: Update `CompassView.kt`**

Replace `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` with `import dev.zacsweers.metrox.viewmodel.compose.metroViewModel`.

Replace `hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current))` with `metroViewModel(checkNotNull(LocalViewModelStoreOwner.current))`.

- [ ] **Step 5: Commit**

```bash
git add feature/compass/
git commit -m "feat: convert compass module from Hilt to Metro"
```

---

## Task 10: Convert feature/location Module

**Files:**
- Modify: `feature/location/src/main/kotlin/io/trewartha/positional/location/AospLocator.kt`
- Rewrite: `feature/location/src/main/kotlin/io/trewartha/positional/location/LocationModule.kt` as `LocationProviders.kt`
- Delete: `feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorModule.kt`
- Create: `feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorBinding.kt`
- Modify: `feature/location/src/gms/kotlin/io/trewartha/positional/location/GmsLocator.kt`
- Rewrite: `feature/location/src/gms/kotlin/io/trewartha/positional/location/GmsLocatorModule.kt` as `GmsLocationProviders.kt`
- Modify: `feature/location/ui/src/main/kotlin/io/trewartha/positional/location/ui/LocationViewModel.kt`
- Modify: `feature/location/ui/src/main/kotlin/io/trewartha/positional/location/ui/LocationView.kt`

- [ ] **Step 1: Update `AospLocator.kt`**

Replace `import javax.inject.Inject` with `import dev.zacsweers.metro.Inject`. Keep the `@Inject` annotation — this class is graph-constructable (used by both AOSP and GMS flavors). No `@ContributesBinding` here since this file is in `src/main/` (shared by both flavors).

- [ ] **Step 2: Create `AospLocatorBinding.kt` in AOSP source set**

File: `feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorBinding.kt`

```kotlin
package io.trewartha.positional.location

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
internal interface AospLocatorBinding {

    @Provides
    @SingleIn(AppScope::class)
    fun locator(aospLocator: AospLocator): Locator = aospLocator
}
```

- [ ] **Step 3: Delete `AospLocatorModule.kt`**

```bash
rm feature/location/src/aosp/kotlin/io/trewartha/positional/location/AospLocatorModule.kt
```

- [ ] **Step 4: Rewrite `LocationModule.kt` as `LocationProviders.kt`**

Rename file and replace contents:

```kotlin
package io.trewartha.positional.location

import android.app.Application
import android.location.LocationManager
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
internal interface LocationProviders {

    @Provides
    fun locationManager(application: Application): LocationManager =
        application.getSystemService(LocationManager::class.java)
}
```

- [ ] **Step 5: Update `GmsLocator.kt`**

Replace `import javax.inject.Inject` with `import dev.zacsweers.metro.Inject`.

- [ ] **Step 6: Rewrite `GmsLocatorModule.kt` as `GmsLocationProviders.kt`**

Rename file and replace contents:

```kotlin
package io.trewartha.positional.location

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
internal interface GmsLocationProviders {

    @Provides
    fun fusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @SingleIn(AppScope::class)
    fun locator(gmsLocator: GmsLocator): Locator = gmsLocator
}
```

Note: `@SingleIn(AppScope::class)` is added on the `locator()` provider to match the singleton semantics of the AOSP flavor and prevent multiple `GmsLocator` instances (which hold long-lived callback flows).

- [ ] **Step 7: Update `LocationViewModel.kt`**

Remove `@HiltViewModel` annotation. Remove Hilt/javax imports, add Metro imports:
```kotlin
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.trewartha.positional.AppScope
```

Replace annotations:
```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey(LocationViewModel::class)
public class LocationViewModel @Inject constructor(
```

- [ ] **Step 8: Update `LocationView.kt`**

Replace `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` with `import dev.zacsweers.metrox.viewmodel.compose.metroViewModel`.

Replace `hiltViewModel(...)` with `metroViewModel(...)`.

- [ ] **Step 9: Commit**

```bash
git add feature/location/
git commit -m "feat: convert location module from Hilt to Metro"
```

---

## Task 11: Convert feature/settings Module

**Files:**
- Modify: `feature/settings/src/main/kotlin/io/trewartha/positional/settings/DataStoreSettingsRepository.kt`
- Rewrite: `feature/settings/src/main/kotlin/io/trewartha/positional/settings/SettingsModule.kt` as `SettingsProviders.kt`
- Modify: `feature/settings/ui/src/main/kotlin/io/trewartha/positional/settings/ui/SettingsViewModel.kt`
- Modify: `feature/settings/ui/src/main/kotlin/io/trewartha/positional/settings/ui/SettingsView.kt`

- [ ] **Step 1: Remove `@Inject` from `DataStoreSettingsRepository`**

Remove `import javax.inject.Inject` and the `@Inject` annotation from the constructor. This class is manually instantiated in `SettingsProviders` (to apply `@SingleIn` scoping), not graph-constructed.

```kotlin
internal class DataStoreSettingsRepository(
    private val context: Context
) : SettingsRepository {
```

- [ ] **Step 2: Rewrite `SettingsModule.kt` as `SettingsProviders.kt`**

Rename file and replace contents:

```kotlin
package io.trewartha.positional.settings

import android.app.Application
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
internal interface SettingsProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun settingsRepository(application: Application): SettingsRepository =
        DataStoreSettingsRepository(application)
}
```

- [ ] **Step 3: Update `SettingsViewModel.kt`**

Remove `@HiltViewModel` annotation. Remove Hilt/javax imports, add Metro imports:
```kotlin
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.trewartha.positional.AppScope
```

Replace annotations:
```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey(SettingsViewModel::class)
public class SettingsViewModel @Inject constructor(
```

- [ ] **Step 4: Update `SettingsView.kt`**

Replace `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` with `import dev.zacsweers.metrox.viewmodel.compose.metroViewModel`.

Replace `hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current))` with `metroViewModel(checkNotNull(LocalViewModelStoreOwner.current))`.

- [ ] **Step 5: Commit**

```bash
git add feature/settings/
git commit -m "feat: convert settings module from Hilt to Metro"
```

---

## Task 12: Convert feature/sun Module

**Files:**
- Modify: `feature/sun/src/main/kotlin/io/trewartha/positional/sun/MainSolarTimesRepository.kt`
- Delete: `feature/sun/src/main/kotlin/io/trewartha/positional/sun/SunModule.kt`
- Modify: `feature/sun/ui/src/main/kotlin/io/trewartha/positional/sun/ui/SunViewModel.kt`
- Modify: `feature/sun/ui/src/main/kotlin/io/trewartha/positional/sun/ui/SunView.kt`

- [ ] **Step 1: Update `MainSolarTimesRepository.kt` with `@ContributesBinding`**

Replace `import javax.inject.Inject` with:
```kotlin
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.trewartha.positional.AppScope
```

Add `@ContributesBinding`:
```kotlin
@ContributesBinding(AppScope::class)
internal class MainSolarTimesRepository @Inject constructor() : SolarTimesRepository {
```

- [ ] **Step 2: Delete `SunModule.kt`**

```bash
rm feature/sun/src/main/kotlin/io/trewartha/positional/sun/SunModule.kt
```

- [ ] **Step 3: Update `SunViewModel.kt`**

Remove `@HiltViewModel` annotation. Remove Hilt/javax imports, add Metro imports:
```kotlin
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.trewartha.positional.AppScope
```

Replace annotations:
```kotlin
@ContributesIntoMap(AppScope::class)
@ViewModelKey(SunViewModel::class)
public class SunViewModel @Inject constructor(
```

- [ ] **Step 4: Update `SunView.kt`**

Replace `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` with `import dev.zacsweers.metrox.viewmodel.compose.metroViewModel`.

Replace `hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current))` with `metroViewModel(checkNotNull(LocalViewModelStoreOwner.current))`.

- [ ] **Step 5: Commit**

```bash
git add feature/sun/
git commit -m "feat: convert sun module from Hilt to Metro"
```

---

## Task 13: Verify Build and Tests

- [ ] **Step 1: Build both flavors**

```bash
./gradlew assembleAospDebug assembleGmsDebug
```

Expected: BUILD SUCCESSFUL. Fix any compilation errors before proceeding.

- [ ] **Step 2: Run all tests**

```bash
./gradlew test
```

Expected: All tests pass (tests use manual test doubles, no Hilt dependency).

- [ ] **Step 3: Validate `internal` visibility on `@ContributesTo` interfaces**

If the build succeeded, `internal` visibility on contributed interfaces is confirmed working. If any `internal` interfaces caused compilation errors, change them to `public` and re-verify.

- [ ] **Step 4: Commit any fixes**

```bash
git add -A
git commit -m "fix: resolve Metro migration build issues"
```

---

## Task 14: Update DI Rules Documentation

**Files:**
- Modify: `.claude/rules/kotlin/android/dependency-injection.md`

- [ ] **Step 1: Update DI rules for Metro conventions**

Replace Hilt-specific annotations and examples with Metro equivalents:
- `@Module` / `@InstallIn` becomes `@ContributesTo(AppScope::class)`
- `@Inject` is now `dev.zacsweers.metro.Inject`
- `@Singleton` becomes `@SingleIn(AppScope::class)`
- `@HiltViewModel` becomes `@ContributesIntoMap` + `@ViewModelKey`
- `Provider<T>` is now `dev.zacsweers.metro.Provider`
- `dagger.Lazy` becomes `kotlin.Lazy`
- `@TestInstallIn` becomes Metro's `excludes` + `replaces` pattern
- `hiltViewModel()` becomes `metroViewModel()`

- [ ] **Step 2: Commit**

```bash
git add .claude/rules/kotlin/android/dependency-injection.md
git commit -m "docs: update DI rules for Metro conventions"
```
