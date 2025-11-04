---
name: Kotlin Coroutines
description: Use Kotlin coroutines with well-defined scopes for proper lifecycle management, inject dispatchers for testability, avoid catching CancellationException unless rethrowing, prefer Dispatchers.Default over IO for most operations, and never run blocking operations on Dispatchers.Main. Use this skill when launching coroutines, choosing coroutine scopes like viewModelScope or lifecycleScope, injecting CoroutineDispatchers with qualifiers, handling cancellation, using withContext to switch dispatchers, or working with any suspend functions or coroutine code. Use this when writing asynchronous code in .kt files.
---
# Kotlin Coroutines
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle kotlin coroutines.

## When to use this skill

- When launching coroutines in well-defined scopes with proper beginning and end (e.g., viewModelScope, lifecycleScope)
- When injecting CoroutineDispatcher instances with qualifier annotations for test overrides
- When handling CancellationException (never catch unless rethrowing)
- When choosing between dispatchers (prefer Dispatchers.Default, only use Dispatchers.IO for blocking I/O)
- When avoiding blocking operations on Dispatchers.Main (use background dispatchers instead)
- When using withContext to switch dispatchers for specific operations
- When writing suspend functions or any coroutine-based asynchronous code
- When working with Flow, async/await, or other coroutine constructs in .kt files

## Instructions
For details, refer to the information provided in this file:
[kotlin coroutines](../../../agent-os/standards/kotlin/coroutines.md)
