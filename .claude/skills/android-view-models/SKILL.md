---
name: Android View Models
description: Implement ViewModels following MVI architecture with unidirectional data flow, exposing state via StateFlow, handling actions through sealed interfaces, and managing coroutines in viewModelScope. Use this skill when creating ViewModel classes, exposing UI state with StateFlow, defining sealed Action interfaces, implementing onAction functions, combining multiple state flows, launching coroutines in viewModelScope, extracting business logic into use cases, or working with any ViewModel.kt files. Use this when building the presentation layer that connects UI to business logic.
---
# Android View Models
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle android view models.

## When to use this skill

- When creating ViewModel classes that follow MVI architecture with unidirectional data flow
- When exposing UI state using a single StateFlow<State> with an immutable data class
- When defining sealed Action interfaces to represent all possible user actions
- When implementing the onAction(action: Action) function to handle user actions
- When combining multiple StateFlows declaratively using the combine operator
- When launching coroutines in viewModelScope for proper lifecycle management
- When extracting complex business logic into use cases that get injected into ViewModels
- When ensuring ViewModels never hold references to Views, Activities, or Fragments

## Instructions
For details, refer to the information provided in this file:
[android view models](../../../agent-os/standards/android/view-models.md)
