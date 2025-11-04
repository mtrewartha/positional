---
name: Android Dependency Injection
description: Implement dependency injection using Hilt in Android applications following best practices for constructor injection, module scoping, and testability. Use this skill when setting up Hilt modules, configuring dependency injection, writing @Inject constructors, creating @Provides or @Binds methods in Hilt modules, scoping dependencies with @Singleton or other scopes, using qualifiers like @Named for ambiguous types, deferring expensive dependencies with Provider or Lazy, or creating test bindings with @TestInstallIn. Use this when working with DI-related files, module classes annotated with @Module and @InstallIn, or any code that needs to inject or provide dependencies.
---
# Android Dependency Injection
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android dependency injection.

## When to use this skill

- When creating or modifying Hilt modules to provide dependencies
- When adding constructor injection to classes with @Inject annotation
- When configuring scopes for modules, bindings, and providers to manage dependency lifetimes
- When using qualifiers to distinguish between multiple bindings of the same type
- When deferring expensive dependency creation using Provider<T> or Lazy<T>
- When creating test bindings with @TestInstallIn to swap dependencies during testing
- When deciding between field injection and constructor injection
- When working with any files that involve Hilt annotations like @Module, @InstallIn, @Provides, @Binds

## Instructions
For details, refer to the information provided in this file:
[android dependency injection](../../../agent-os/standards/android/dependency-injection.md)
