---
name: Android Tech Stack
description: Leverage the project's Android technology stack including Kotlin, Gradle, Room for databases, DataStore for preferences, JUnit 4/5 for testing, Kotest for assertions, Detekt for linting, GitHub Actions for CI/CD, and Firebase for crash reporting. Use this skill when choosing which libraries or frameworks to use for a task, configuring build tools, setting up testing frameworks, implementing database operations, managing key-value storage, configuring linting and formatting, or making technology decisions that should align with the established stack. Use this when adding new dependencies or working with any technology choices in the Android project.
---
# Android Tech Stack
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android tech stack.

## When to use this skill

- When choosing which database technology to use (should use SQLite with Room ORM)
- When implementing key-value storage (should use Proto-based DataStore)
- When writing tests and choosing test frameworks (JUnit 4 for instrumented, JUnit 5 for unit, Kotest for assertions)
- When setting up linting or formatting (should use Detekt)
- When configuring build automation or CI/CD (should use Gradle and GitHub Actions)
- When integrating crash reporting (should use Firebase)
- When making any technology stack decisions to ensure consistency with established standards
- When adding dependencies to build.gradle.kts files

## Instructions
For details, refer to the information provided in this file:
[android tech stack](../../../agent-os/standards/android/tech-stack.md)
