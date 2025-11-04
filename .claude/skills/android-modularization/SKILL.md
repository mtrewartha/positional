---
name: Android Modularization
description: Structure Android applications into modular Gradle subprojects organized by app, feature, and core directories with appropriate build configurations. Use this skill when creating new Gradle modules, organizing module directories, setting up convention plugins for build logic, deciding between Android Gradle Plugin and Kotlin JVM plugin, creating UI submodules, structuring feature modules, designing core modules for reusable functionality, or working with settings.gradle.kts and build.gradle.kts files. Use this when making decisions about module boundaries, dependencies between modules, or any structural organization of the codebase into separate Gradle subprojects.
---
# Android Modularization
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android modularization.

## When to use this skill

- When creating new Gradle subprojects and deciding their location (app, feature, or core directory)
- When organizing modules to separate concerns and optimize build performance
- When creating UI-related code that should be placed in nested ui submodules
- When setting up Gradle convention plugins to keep build logic DRY
- When deciding whether to use the Kotlin JVM plugin or Android Gradle Plugin for a module
- When working with settings.gradle.kts to include new modules
- When defining module dependencies and ensuring proper dependency direction
- When structuring features to be modular and potentially reusable across apps

## Instructions
For details, refer to the information provided in this file:
[android modularization](../../../agent-os/standards/android/modularization.md)
