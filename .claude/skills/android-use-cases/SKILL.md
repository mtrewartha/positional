---
name: Android Use Cases
description: Implement single-purpose use case classes that encapsulate specific business logic representing user actions with a single invoke operator function. Use this skill when creating use cases for business logic triggered by or on behalf of users, naming use cases with verb phrases like CopyCoordinatesToClipboard or UpdateSettings, implementing the operator fun invoke function, extracting complex business logic from ViewModels, or working with any .kt files that contain use case classes. Use this when building domain layer components that represent distinct user actions.
---
# Android Use Cases
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android use cases.

## When to use this skill

- When creating use case classes to encapsulate specific business logic for user actions
- When naming use case classes with verb phrases that clearly indicate the action (e.g., CopyCoordinatesToClipboard)
- When implementing the single public function operator fun invoke() in use cases
- When extracting complex or reusable business logic from ViewModels into separate use cases
- When working with files containing use case implementations in the domain layer
- When deciding whether business logic should be in a use case versus a ViewModel
- When writing suspend functions for use cases that perform asynchronous operations

## Instructions
For details, refer to the information provided in this file:
[android use cases](../../../agent-os/standards/android/use-cases.md)
