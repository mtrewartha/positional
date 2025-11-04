---
name: Android Instrumentation Tests
description: Write and optimize Android instrumentation tests with a preference for Robolectric over emulator-based tests for better speed and testability. Use this skill when writing instrumentation tests, converting instrumented tests to Robolectric tests, refactoring classes to improve testability by isolating Android framework dependencies, working with androidTest source sets, or making decisions about whether a test should run on an emulator/device versus using Robolectric. Use this when working with test files that require Android framework components or when evaluating test performance and suggesting optimizations.
---
# Android Instrumentation Tests
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android instrumentation tests.

## When to use this skill

- When writing tests in the androidTest source set that run on emulators or devices
- When converting slow instrumented tests to faster Robolectric tests
- When refactoring classes to isolate Android framework dependencies for better testability
- When evaluating whether a test requires actual device/emulator execution or can use Robolectric
- When working with tests that interact with Android framework components
- When optimizing test execution speed by reducing reliance on instrumentation
- When suggesting architectural improvements to make code more testable

## Instructions
For details, refer to the information provided in this file:
[android instrumentation tests](../../../agent-os/standards/android/instrumentation-tests.md)
