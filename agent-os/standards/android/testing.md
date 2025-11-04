## Testing best practices

- **Rely on the Test Pyramid**: Prefer cheaper, more stable, faster unit tests over slower, bigger,
  less stable forms of testing like integration tests and acceptance/end-to-end tests.
- **Test Behavior, Not Implementation**: Focus tests on what the code does, not how it does it, to
  reduce brittleness.
- **Fast Execution**: Keep unit tests fast (milliseconds) so developers run them frequently during
  development.
- **Test Doubles**: Use an appropriate test double type for each need within a given test. When
  fakes are best, create custom ones in the `testFixtures` source set of the module. When mocks or
  spies are best, use MockK to create them.
- **Isolate Tests**: Ensure each test is independent and does not rely on shared state or the order
  of execution.
- **Single Responsibility**: Each test should verify one specific behavior or outcome. If an "if" or
  "when" is needed to configure the test, the test should be split into multiple tests.
- **Prefer Robolectric over instrumented tests**: Unless something is too complex or not achievable
  with Robolectric, prefer it to instrumented tests for the sake of test speed.
- **Use Compose UI Testing for UI testing**: Composables that are public in a module should be
  tested with Compose UI tests.
- **Use Kotest for unit tests (instrumented or not)**: Use Kotest as the main testing framework for
  unit tests, taking advantage of its expressive syntax and powerful features:
    - **Use assertions with nullability contracts** instead of !! to assert non-null values in
      tests, (e.g. result.shouldNotBeNull().shouldBeGreaterThan(5) instead of result!!
      .shouldBeGreaterThan(5))
    - **Leverage Kotest's matchers** (both the core assertions and extension library assertions) for
      more expressive assertions, chaining them when it makes sense (e.g. to assert something is not
      null *and* to assert something else about it)
    - **Create custom matchers when they might be generally useful across a project** to improve
      readability and reduce duplication.
- **Use Turbine to test Flows**: For testing Kotlin Flows, use the Turbine library to collect and
  assert on emissions in a structured way.
- **Use the Gherkin format tests**:
    - Most tests should have clear "given", "when", and "then" sections where:
        - "Given" sets up the specific preconditions for the test.
        - "When" performs the action being tested. This might be a function call or property
          read/write. If a function's return value or a property's read value is being tested, then
          a "val result" should be created to hold the value that then gets asserted on in the
          "then", e.g. "val result = subject.someFunction()"
        - "Then" performs assertions on the "result" of the "when"
    - Use present tense for verbs in test function names, e.g. "Given something is true, when action
      is taken, then outcome occurs"
    - For instrumented tests, use the format "givenX_whenY_thenZ" for test function names
    - For non-instrumented tests, use backticks in the format `Given X, when Y, then Z` for test
      function names