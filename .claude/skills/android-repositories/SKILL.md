---
name: Android Repositories
description: Design and implement repository pattern abstractions for data access following collection-like interfaces with standard CRUD operations and Flow-based reactive patterns. Use this skill when creating repository interfaces, implementing repositories with data sources, defining getter functions that return Flow or suspend functions, creating add/update/remove functions, abstracting local and remote data sources, building fake repository implementations for testing, or working with data layer code. Use this when working with files that contain repository interfaces, repository implementations, data source abstractions, or any code that provides collection-like access to domain models.
---
# Android Repositories
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android repositories.

## When to use this skill

- When creating repository interfaces that provide collection-like abstractions for domain types
- When implementing repositories that abstract local and remote data sources
- When defining getter functions that return Flow for reactive observation or suspend functions for one-time retrieval
- When creating add, update, or remove functions following naming and signature conventions
- When building fake repository implementations for testing
- When deciding how to structure data source abstractions behind repositories
- When working with files named like FooRepository.kt or FakeFooRepository.kt
- When ensuring repositories use proper Flow operators like distinctUntilChanged() and flowOn()

## Instructions
For details, refer to the information provided in this file:
[android repositories](../../../agent-os/standards/android/repositories.md)
