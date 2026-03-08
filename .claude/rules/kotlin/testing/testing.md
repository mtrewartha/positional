---
paths:
  - "**/src/**/androidTest*/**"
  - "**/src/**/test*/**"
---

# Testing Rules

- **Effective Tests**: Tests must not produce false passes. A test must only pass when the code it
  claims to test is actually performing correctly.
- **Stable Tests**: Tests must not produce false failures, which degrade our trust in them and hence
  reduce their utility. A test must only fail when there is a bug in the code it tests.
- **DescribeSpec Style**: Use Kotest's DescribeSpec style. Use a single `describe` for each behavior
  or aspect of the subject under test. Use plain English to describe the behavior or aspect; when
  the behavior is that of a function, use plain English to briefly describe the function's behavior.
  When the aspect is a property, use plain English to briefly describe getting that which the
  property exposes (example will be provided below). The goal is behavior-first language, not
  implementation-first language. Implementation-first means using a code identifier verbatim as the
  label (e.g. `describe("foosFlow")` or `describe("setLocation")`). Using domain vocabulary that
  coincidentally matches a property or function name is fine as long as the description reads as
  natural English describing the behavior or concept (e.g. `describe("the location flow")` is fine
  because "location" and "flow" are domain/concept terms, not a reference to the `location`
  property identifier). DO NOT use `describe` to encode context into the test; that's what
  `context` is for. Inside `describe` blocks, use `context` to specify preconditions that will apply
  to tests. Inside `context` blocks, use `it` to house assertions of a single test. You may have
  multiple `context` calls in a single `describe` and multiple `it` calls in a single
  `context` (or in a single `describe` if there is no `context`). If there is no context, simply
  omit the `context` block and skip directly to the `it`. Each test should be self-contained- it
  should perform all setup, testing, and teardown required for the test and should *not* use any
  instance state in the test class. This keeps tests parallelizable. For example:
  ```kotlin
  class FooRepository {

      val foosFlow: Flow<Collection<Foo>>

      suspend fun addFoo(foo: Foo)
  }

  class FooTest : DescribeSpec({

      // Create the subject under test, aka the "sut". If the subject has dependencies,
      // add them as parameters with sensible defaults so tests only declare the
      // dependencies they need to interact with, e.g.:
      //   fun sut(repo: FooRepository = TestFooRepository()): FooViewModel = FooViewModel(repo)
      // Then tests that need to drive behavior through a dependency declare it locally
      // and pass it in:
      //   val repo = TestFooRepository()
      //   val subject = sut(repo = repo)
      // Tests that don't need to interact with any dependency call sut() with no arguments:
      //   val subject = sut()
      fun sut(): FooRepository = FooRepository()
  
      describe("observing the foos by flow") { // Good naming! describe("foosFlow") would be bad.
          context("when no foos have been added") {
              it("emits an empty collection from the flow") {
                  // Put the self-contained test here
              }
          }

          context("when foos are added") {
              it("emits the collection of added foos") {
                  // Put the self-contained test here
              }
          }
      }
  
      describe("adding foos") { // Good naming! describe("addFoo") would be bad!
          // and so on...
      }
  })
  ```
- **Kotest Assertions**: Use Kotest assertions for clear, concise, expressive tests.
- **Turbine for Flows**: Use the Turbine library to test Kotlin Flows.
- **Black Box Testing**: Write "black box" style tests that do not assume any implementation details
  about the code under test. Tests should verify behavior through public APIs only, without relying
  on internal state, private methods, or specific implementation strategies.
- **Edge Case Coverage**: Thoroughly cover all edge cases, including boundary values, error
  conditions, and distinct input categories (e.g. empty vs non-empty collections, valid vs invalid
  inputs, minimum and maximum values).
- **Test Doubles:** Use an appropriate test double type for each need within a given test.
- **Fast Execution**: Keep unit tests fast (milliseconds) so developers run them frequently during
  development.
- **Custom Matchers**: Create custom Kotest matchers and corresponding extension functions when they
  improve test readability and reuse.
- **Test Fixture Factories**: Ensure each model exposed by a module has a test fixture factory
  function in `<module>/src/testFixtures/kotlin/<top module package path>/RandomFixtures.kt`:
    - Name the function `random` followed by the model name and specify the type explicitly, e.g.
      `fun randomUser(): User`
    - Implement the function by returning an instance of the model populated with random data. You
      may use other fixture functions from your module or from modules like `:core:test` to help
      build the model, e.g.:
      `fun randomUser(): User = User(id = randomId(), name = randomName(), email = randomEmail())`
    - Randomize **all** properties of a model, including enum/unit fields. A fixture that fixes any
      property to a constant (e.g. always using `.meters` for a `Distance`) is not truly random and
      will miss bugs that only surface with other values. Use `Arb.enum<Foo.Unit>()` (or equivalent)
      to pick the unit randomly, just as the magnitude is picked randomly.
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
