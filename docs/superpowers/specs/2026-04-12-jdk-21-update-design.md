# POS-40: Update JDK Version from 17 to 21

## Problem

Metro 0.13.2's Gradle plugin requires JVM 21 at minimum. The project currently targets JDK 17
everywhere — CI, build-logic, app module, and shared constants — causing the build to fail:

```
Could not resolve dev.zacsweers.metro:gradle-plugin:0.13.2
> Dependency requires at least JVM runtime version 21. This build uses a Java 17 JVM.
```

## Scope

Update all JDK 17 references to JDK 21 across the project, with targeted cleanup of duplicated
configuration in the convention plugin.

## Changes

### 1. Update shared constants

**File:** `build-logic/convention/src/main/kotlin/io/trewartha/positional/KotlinConfigurationConstants.kt`

- `JVM_SOURCE_COMPATIBILITY`: `JavaVersion.VERSION_17` → `JavaVersion.VERSION_21`
- `JVM_TARGET`: `JvmTarget.JVM_17` → `JvmTarget.JVM_21`

This automatically propagates to all convention plugin consumers:
- `library.android.gradle.kts` (lines 98-99, 105)
- `library.jvm.gradle.kts` (lines 15-16, 22)

### 2. Update hardcoded values that cannot use the shared constants

These files can't reference `KotlinConfigurationConstants` — the first because it builds the
project that defines them, the second because it doesn't apply convention plugins from build-logic.

**File:** `build-logic/convention/build.gradle.kts`
- `JavaVersion.VERSION_17` → `JavaVersion.VERSION_21` (lines 11-12)
- `JvmTarget.JVM_17` → `JvmTarget.JVM_21` (line 17)

**File:** `app/build.gradle.kts`
- `JavaVersion.VERSION_17` → `JavaVersion.VERSION_21` (lines 58-59)
- `JvmTarget.JVM_17` → `JvmTarget.JVM_21` (line 130)

### 3. Update CI workflow

**File:** `.github/workflows/tests.yml`
- `java-version: '17'` → `java-version: '21'` (line 19)

### 4. Clean up duplicates in library.android.gradle.kts

**File:** `build-logic/convention/src/main/kotlin/io/trewartha/positional/library.android.gradle.kts`

Replace hardcoded `JavaVersion.VERSION_17` (lines 18-19) with the shared `JVM_SOURCE_COMPATIBILITY`
constant, matching what the same file already does on lines 98-99.

### 5. Remove stale version comments

Remove the "Up to Java 17 APIs are available through desugaring" comment from:
- `library.android.gradle.kts` (line 16)
- `app/build.gradle.kts` (line 56)

Keep the desugaring support table URL as a standalone comment.

## Out of Scope

- Creating an `application.android` convention plugin to DRY up shared config between the app and
  library modules. Tracked in POS-41.

## Verification

- `./gradlew test` passes locally
- CI workflow passes with JDK 21
