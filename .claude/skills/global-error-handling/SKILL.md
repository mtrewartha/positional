---
name: Global Error Handling
description: Implement error handling with user-friendly messages, fail-fast validation, specific exception types, centralized error handling at boundaries, graceful degradation, retry strategies with exponential backoff, and proper resource cleanup. Use this skill when implementing error handling logic, throwing or catching exceptions, validating input and preconditions, designing error messages for users, implementing retry logic for transient failures, ensuring resources are cleaned up in finally blocks, or working with any code that needs robust error handling. Use this when writing error handling in any .kt files.
---
# Global Error Handling
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle global error handling.

## When to use this skill

- When providing clear, actionable error messages to users without exposing technical details or security information
- When validating input and checking preconditions early to fail fast with clear error messages
- When using specific exception/error types instead of generic ones for targeted handling
- When implementing centralized error handling at appropriate boundaries (controllers, API layers)
- When designing systems to degrade gracefully when non-critical services fail
- When implementing exponential backoff retry strategies for transient failures in external service calls
- When cleaning up resources (file handles, connections) in finally blocks or equivalent mechanisms
- When writing any error handling code in .kt files

## Instructions
For details, refer to the information provided in this file:
[global error handling](../../../agent-os/standards/global/error-handling.md)
