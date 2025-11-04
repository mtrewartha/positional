---
name: Kotlin Models
description: Create Kotlin models using immutable data classes with val properties, wrapping ambiguous primitives with unit types like Duration or Instant, using pure Kotlin without platform-specific types, and avoiding transport/storage concerns in models. Use this skill when creating data classes for domain models, using val for immutable properties, wrapping primitive types with inline value classes for type safety, ensuring models use platform-independent Kotlin types, separating models from serialization/database concerns, or working with any model definitions. Use this when creating model classes in .kt files.
---
# Kotlin Models
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle kotlin models.

## When to use this skill

- When creating data classes for domain models using Kotlin's data class feature
- When using val for properties to ensure immutability
- When wrapping ambiguous primitives with unit types like Duration or Instant instead of Long/Int
- When using inline value classes to add type safety to primitive types
- When ensuring models use pure Kotlin types without platform-specific dependencies
- When avoiding transport/storage concerns like JSON or database annotations in models
- When using separate mappers/adapters for serialization and database conversions
- When defining any model or domain classes in .kt files

## Instructions
For details, refer to the information provided in this file:
[kotlin models](../../../agent-os/standards/kotlin/models.md)
