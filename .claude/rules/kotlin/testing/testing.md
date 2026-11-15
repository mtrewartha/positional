---
paths:
  - "**/src/**/androidTest*/**"
  - "**/src/**/test*/**"
---

# Testing Rules

General test design lives in the `kotlin-testing` skill. Project-specific conventions:

- **Spec style**: Kotest `DescribeSpec`. The Robolectric tests stay JUnit 4 because the Kotest
  Robolectric extension doesn't support Kotest 6.
- **Fixture factories**: Each model a module exposes gets a factory in
  `<module>/src/testFixtures/kotlin/<package path>/RandomFixtures.kt`:
    - Name it `random<Model>()` with an explicit return type, e.g. `fun randomUser(): User`.
    - Randomize **every** property, including enum and unit fields.
    - Don't add parameters that mirror model properties; callers use `copy(...)`. Parameters that
      tune generation (e.g. `wordCount: IntRange`) are fine.
    - Generate all randomness from `TEST_RANDOM` / `TEST_RANDOM_SOURCE` in `:core:test` so a seed
      reproduces a run. Put generally useful primitive fixtures in `:core:test`.
- **Expected values in change tests**: When a test sets an initial value and asserts that a new
  value is emitted, hardcode both values so they are guaranteed to differ.
