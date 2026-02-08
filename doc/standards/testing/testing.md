### Testing Standards

- **Test Behavior, Not Implementation**: Focus tests on what the code does, not how it does it, to
  reduce brittleness.
- **Clear Test Names**: Use descriptive names that explain what's being tested and the expected
  outcome.
- **Avoid Kotlin Symbol Names**: Instead of referring to functions/properties/classes by name in
  test names, use plain English descriptions, e.g. use "When user ID is set to an empty string"
  instead of "When `setUserId` is called with an empty string".
- **Test Doubles:** Use an appropriate test double type for each need within a given test.
- **Fast Execution**: Keep unit tests fast (milliseconds) so developers run them frequently during
  development.
- **Kotest Assertions**: Use Kotest assertions for clear, concise, expressive tests.
- **Turbine for Flows**: Use the Turbine library to test Kotlin Flows.
- **Custom Matchers**: Create custom Kotest matchers and corresponding extension functions when they
  improve test readability and reuse.
- **BehaviorSpec Style**: When using Kotest's BehaviorSpec style, place "arrange" phase code in the
  Given block, "act" phase code in the When block, and "assert" phase code in the Then block. Ensure
  `IsolationMode.InstancePerLeaf` is used.
- **Test Fixtures**: Ensure each model exposed by a module has a test fixture factory function in
  `<module>/src/testFixtures/kotlin/<top module package path>/Fixtures.kt`:
    - Name the function `random` followed by the model name and specify the type explicitly, e.g.
      `fun randomUser(): User`
    - Implement the function by returning an instance of the model populated with random data. You
      may use other fixture functions from your module or from modules like `:core:test` to help
      build the model, e.g.:
      `fun randomUser(): User = User(id = randomId(), name = randomName(), email = randomEmail())`
    - Do not add function parameters that map to model properties; use the data class `copy`
      function for that, e.g. `randomUser().copy(email = "bob@example.com")`
    - Parameters may be used to tweak random fixture generation, e.g.
      `randomSentence(wordCount: IntRange): String`
    - You can use Kotest property-based generators to help generate random data, e.g.
      `Arb.email().next(randomSource)`
    - ALWAYS use the `TEST_RANDOM` and `TEST_RANDOM_SOURCE` from `:core:test` to make sure that
      random fixture data is generated with the same seed for reproducibility when hard-coding the
      seed for debugging flaky tests.
    - If you need lower-level primitive fixture types (e.g. `randomWord(): String`) to help build
      higher level fixtures, look for them in `:core:test`. If they don't exist and are generally
      useful, add them there for others to use.
