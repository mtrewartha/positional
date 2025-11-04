# Tech Stack

## Language & Platform
- **Kotlin** - Primary development language for all modules
- **Android SDK** - Target SDK 36 (Android 16), Minimum SDK 24 (Android 7.0)
- **Java 17** - JVM target for Kotlin compilation

## UI Framework
- **Jetpack Compose** - Declarative UI framework for all screens and components
- **Material Design 3** - Design system for consistent, modern Android UI
- **Compose Navigation** - In-app navigation between screens
- **Accompanist Permissions** - Runtime permission handling in Compose

## Architecture
- **Clean Architecture** - Separation of concerns with domain, data, and presentation layers
- **Multi-Module Structure** - Feature-based modules organized as `:feature:<name>` (domain) and `:feature:<name>:ui` (UI)
- **State Pattern** - Custom `State<Data, Error>` sealed interface for explicit loading/success/failure handling
- **Feature Modules** - Independent features with `src/testFixtures/` for reusable test doubles
- **Single Activity Architecture** - MainActivity with Compose-based navigation

## Dependency Injection
- **Hilt** - Compile-time dependency injection built on Dagger
- **Component Hierarchy**:
  - `SingletonComponent` - App-wide dependencies (Clock, CoroutineContext, Locale, SettingsRepository)
  - `ViewModelComponent` - ViewModel-scoped dependencies (Compass, Locator)
  - `ActivityRetainedComponent` - Activity-retained dependencies (DateTimeFormatter)

## State Management
- **Kotlin Flows** - Reactive data streams for continuous data updates
- **StateFlow** - Hot Flow for exposing UI state from ViewModels
- **Coroutines** - Structured concurrency for asynchronous operations
- **State<D, C> Sealed Interface** - Custom pattern from `:core:ui` enforcing explicit handling of:
  - `State.Loading` - Initial or transitional loading state
  - `State.Loaded<D>` - Success state with data
  - `State.Failure<C>` - Error state with cause

## Data Persistence
- **Proto DataStore** - Type-safe, schema-versioned settings storage
- **Protocol Buffers** - Schema definition for settings (`:feature:settings/src/main/proto/settings.proto`)
- **No Database** - Intentionally ephemeral; location data not persisted

## Location Services
- **AOSP Flavor**: Android `LocationManager` - Native Android location APIs (no Google dependencies)
- **GMS Flavor**: `FusedLocationProviderClient` - Google Play Services for enhanced accuracy
- **Unified Interface**: `Locator` interface abstracts platform-specific implementations

## Sensors & Hardware
- **Magnetometer** - Device compass sensor via Android Sensor framework
- **Accelerometer** - For compass tilt compensation
- **GNSS/GPS** - Satellite positioning via location services

## Domain Libraries
- **WorldWind Android** - Coordinate system conversions (UTM, MGRS)
- **Sunrise-Sunset** - Solar time calculations (sunrise, sunset, twilight phases)
- **Kotlin DateTime** - Date and time handling

## Error Reporting & Analytics
- **Firebase Crashlytics** - Anonymous crash diagnostics (GMS release builds only)
- **No Analytics** - Zero user behavior tracking or usage analytics

## Build System
- **Gradle 9.x** - Build automation with Kotlin DSL
- **Version Catalog** - Centralized dependency version management (`gradle/libs.versions.toml`)
- **Typesafe Project Accessors** - Type-safe module references (`projects.core.ui`)
- **Convention Plugins** - Shared build configuration in `build-logic/convention/`:
  - `library.android.gradle.kts` - Android library configuration
  - `library.jvm.gradle.kts` - Pure Kotlin/JVM library configuration
  - `kotest.android.gradle.kts` - Android test configuration with Kotest
  - `kotest.jvm.gradle.kts` - JVM test configuration with Kotest
  - `testLogger.gradle.kts` - Test output formatting
  - `dependencyAnalysis.gradle.kts` - Dependency validation

## Testing Framework
- **Kotest** - Assertion library for expressive test syntax
- **Turbine** - Flow testing library for asserting Flow emissions
- **Robolectric** - Android framework mocking for unit tests
- **JUnit 5** - Test runner and framework
- **Test Fixtures** - Reusable test doubles in `src/testFixtures/` (e.g., `TestLocator`, `TestCompass`)
- **Kover** - Code coverage reporting
- **Managed Devices** - Gradle-managed emulators for instrumented tests

## Product Flavors
- **AOSP** - Uses native Android APIs (no Google Play Services dependencies)
- **GMS** - Uses Google Play Services for enhanced location accuracy
- **Shared Codebase** - Platform-specific implementations via source sets

## Code Quality
- **Android Lint** - Static analysis for Android-specific issues
- **Dependency Analysis Plugin** - Validates module dependencies and detects unused/undeclared dependencies
- **Strict Visibility** - Implementation details marked `internal`, minimal public API surface

## CI/CD
- **GitHub Actions** - Automated testing and checks
- **Codecov** - Code coverage tracking and reporting
- **Automated Workflows**:
  - `tests.yml` - Run all tests on pull requests
  - Coverage reporting to Codecov

## Module Structure
```
:app                          # Main application module (MainActivity, app-level config)
:core:ui                      # Shared UI components, State pattern, navigation contracts
:core:measurement             # Pure Kotlin value types (Angle, Distance, Coordinates)
:feature:location             # Location domain logic (Locator interface, implementations)
:feature:location:ui          # Location UI (ViewModel, screens, formatters)
:feature:compass              # Compass/magnetometer domain logic
:feature:compass:ui           # Compass UI
:feature:sun                  # Solar time calculations
:feature:sun:ui               # Sun times UI
:feature:settings             # Settings repository (Proto DataStore)
:feature:settings:ui          # Settings screens
build-logic/convention        # Convention plugins for build configuration
```

## Key Design Decisions

### Why Multi-Module?
- Enforces architectural boundaries at compile-time
- Enables parallel builds and faster incremental compilation
- Facilitates independent feature development and testing
- Reduces coupling between features

### Why Flows Over LiveData?
- Better composability with operators (combine, map, filter)
- Natural integration with Kotlin coroutines
- Type-safe backpressure handling
- Testable with Turbine

### Why Proto DataStore Over SharedPreferences?
- Type-safe schema with versioning support
- Efficient binary serialization
- Asynchronous API preventing main thread blocking
- Migration path from deprecated SharedPreferences

### Why Clean Architecture?
- Clear separation between business logic and framework code
- Testable domain logic independent of Android framework
- Flexible for future platform expansion (potential Wear OS, desktop)
- Maintainable codebase as features grow

### Why Both AOSP and GMS Flavors?
- Supports privacy-conscious users on de-Googled devices
- Accommodates users in regions without Google Play Services
- Demonstrates commitment to open Android ecosystem
- Maintains same UX regardless of platform variant

### Why No Remote Backend?
- Eliminates privacy concerns from server-side data collection
- Reduces operational costs (no server infrastructure)
- Ensures offline functionality
- Aligns with privacy-first mission

### Why Open Source (GPL-3.0)?
- Verifiable privacy guarantees through code transparency
- Community contributions improve quality
- Prevents proprietary forks while allowing redistribution
- Aligns with free software principles
