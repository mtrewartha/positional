---
name: Global API Design
description: Design APIs that avoid platform-specific types in models, leverage language type systems with proper types for magnitudes and values, use concurrency constructs naturally, prefer immutable data types, and account for domain-specific edge cases. Use this skill when designing public APIs, creating model classes, choosing between primitive types and wrapper classes like Duration or custom types, implementing concurrent operations, ensuring thread safety with immutable types, handling edge cases in domain models, or working with any code that defines interfaces or models used across modules. Use this when making API design decisions.
---
# Global API Design
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle global API design.

## When to use this skill

- When designing public APIs and model classes that will be used across modules or platforms
- When choosing to model types with magnitude and value using classes instead of primitives (e.g., Duration instead of Int milliseconds)
- When implementing concurrent operations using language concurrency constructs naturally
- When using immutable data types to provide thread safety across threads
- When accounting for domain-specific edge cases in model validation (e.g., allowing null names but rejecting blank names)
- When avoiding platform-specific types in models to keep them reusable
- When making decisions about API surface area and what to expose publicly

## Instructions
For details, refer to the information provided in this file:
[global API design](../../../agent-os/standards/global/api-design.md)
