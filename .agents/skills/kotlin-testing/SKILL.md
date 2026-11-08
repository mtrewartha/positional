---
name: kotlin-testing
description: "Use when writing, reviewing, or fixing Kotlin unit tests — especially Kotest specs (DescribeSpec describe/context/it), property tests (checkAll, Arb, Exhaustive), Turbine Flow tests, sut() factories, Arb generators for models, test doubles (fakes, stubs, mocks, MockK, Mockito) — or when setting up tests in a new Kotlin project, or when Kotlin tests are flaky, pass falsely, or test several things at once."
---

# Kotlin Testing

## Framework and style

- **New project**: set up Kotest from the start (`kotest-runner-junit5`, `kotest-assertions-core`, `kotest-property`, plus `useJUnitPlatform()` on the Gradle `Test` task). Write tests as `DescribeSpec`, with Kotest assertions and Turbine for Flows. Use the latest stable release of each library; look it up (e.g. on Maven Central) rather than guessing or reusing a version from memory.
- **Existing project**: match its framework, spec style, and assertion library. Don't introduce Kotest, convert tests, or add dependencies unless asked.
  - If several styles are in use and `DescribeSpec` is one of them, use `DescribeSpec`.
  - Any consistent naming or grouping convention in the existing tests is the pattern to follow, even if there are only a few tests (e.g. flat backtick names like `` `cancel throws when the order has shipped` ``). Extend it; don't introduce new nesting or grouping.
  - Only if the existing tests follow no consistent convention, use the describe/context/it shape below with what the framework offers. In JUnit 5: `@Nested` classes with `@DisplayName("observing the foos by flow")` / `@DisplayName("when no foos have been added")`, then `@Test fun \`emits an empty collection\`()`.

Everything here except the Kotest APIs applies in any framework.

**Read before you act:**
- Before adding, changing, or reviewing a test double (fake, stub, mock, spy, MockK, Mockito): [references/test-doubles.md](references/test-doubles.md).
- Before writing a `checkAll`/`forAll` or an `Arb` generator, or debugging a failing property test: [references/property-testing.md](references/property-testing.md). Only applies if the project uses kotest-property.

## Principles

- **Effective**: a test passes only when the code it claims to test works. No false passes.
- **Stable**: a test fails only when that code has a bug. False failures erode trust.
- **Black box**: verify behavior through the public API only. Never rely on internal state, private methods, or implementation strategy.
- **Single aspect**: each test covers one aspect of behavior, so a bug fails only the tests for that aspect. Several assertions are fine when they support the same aspect, but they're a smell, and so is "and" in a description. Split `it("throws X and does not produce side effect Y")` into two tests, neither of which incidentally checks the other: the side-effect test must not fail just because X wasn't thrown.
- **Self-contained**: each test does its own setup, exercise, and teardown, and never uses state shared at spec or class level, even immutable state.
- **Edge cases**: cover boundaries, error conditions, and distinct input categories (empty vs non-empty, valid vs invalid, minimum and maximum).
- **Fast**: milliseconds per unit test.

## Spec structure

Each block has one job: `describe` names a behavior, `context` states one precondition, `it` states the outcome.

- **`describe`**: a public behavior in plain English from the caller's view: `describe("adding foos")`, not `describe("addFoo")`. Domain words that happen to match an identifier are fine if they read naturally (`describe("the location flow")`). No preconditions here; every `context` for a behavior goes under the same `describe`.
- **`context`**: exactly one precondition. Nest rather than conjoin: an outer `context("when the connection is bandwidth-constrained")` with an inner `context("when the user has no access")`, not one `context` joined by "and". Place each `it` under the innermost `context` whose preconditions all apply; omit `context` when none applies.
- **`it`**: the outcome only. A "when …" clause in an `it` is a missing `context`. Don't append "regardless of …" either: unconstrained inputs are generated, so that's the default (see below).
- Don't repeat `describe` or `context` blocks. Tests that share a behavior and preconditions are sibling `it`s.

## Test inputs and `sut()`

**No unstated assumptions.** Every input a test uses (`sut()` arguments, arguments to the call under test, data staged in fakes) must be one of:
1. Constrained by the `context` path: pass that value explicitly.
2. Otherwise, with kotest-property: generated inside the property test, as a `checkAll` argument or with `Arb.x().bind()`.
3. Otherwise: written out at the point of use (`subject.addFoo(Foo(id = "foo-1"))`). Never hide it in a shared constant such as a spec-level `val SAMPLE_FOO`.

Build the subject with a `sut()` factory whose defaults follow the same rule:
- **Collaborators** default to a fresh, neutral double, typically a fake in its empty state (`FakeFooStore()`). Never pre-stage data or behavior that satisfies a precondition; a test that needs a particular state configures a local fake and passes it in.
- **Values** never default to a pinned constant. In the plain `sut()` they have no default, so each test's choice is visible at the call site.

**Define each factory only if some test in the spec calls it.** The plain `sut()` serves tests outside `checkAll`. With kotest-property, a `PropertyContext.sut()` serves property tests: it has value defaults drawn with `bind()`, and if the spec also has a plain `sut()`, the same parameters and collaborator defaults. When both exist, a bare `sut()` resolves to the property factory inside `checkAll` and to the plain one everywhere else. Each factory calls the subject's constructor directly. **Never have one `sut` call the other**: depending on declaration order and placement, that call can resolve to the calling factory itself, and the test dies with a `StackOverflowError`. If building the subject takes more than the constructor call (a `start()`, a registered listener, collaborators wired together), put those steps in one private function with a name other than `sut`, declared before the factories, and have each factory call it.

Inside `checkAll`, use `PropertyContext.sut()` for any test whose outcome shouldn't depend on unconstrained values. Use the plain `sut()` when the project lacks kotest-property, when the `context` fixes every value parameter, or when the test is too slow to repeat. Calls like `sut()` and `sut(maxFoos = 0)` inside `checkAll` compile without any ambiguity error, so there's no need to rename either factory. When a call passes every value, either factory may be chosen, and both build the same subject.

**Use property tests when the claim is about a kind of input, not one example**: invariants, round-trips (`decode(encode(x)) == x`), idempotence, agreement with a simpler reference implementation, size or order relationships between input and output. With kotest-property, write these with `checkAll`/`forAll`. Keep example-based tests for specific named cases (a known bug, a documented boundary). Name only the outcome in `it`; a narrowed input domain is its own `context` with a matching generator.

```kotlin
// Subject's public API (implementation omitted).
class FooRepository(private val maxFoos: Int, private val backingStore: FooStore) {
    val foosFlow: Flow<Collection<Foo>>
    suspend fun addFoo(foo: Foo)
}

class FooRepositoryTest : DescribeSpec({
    // Plain factory (used outside checkAll): value parameters have no default;
    // collaborators default to an empty fake.
    fun sut(maxFoos: Int, backingStore: FooStore = FakeFooStore()) =
        FooRepository(maxFoos, backingStore)

    // Property-test factory (kotest-property only): value defaults drawn with bind(),
    // same collaborator defaults. Calls the constructor, never the plain sut().
    fun PropertyContext.sut(maxFoos: Int = Arb.int(0..100).bind(), backingStore: FooStore = FakeFooStore()) =
        FooRepository(maxFoos, backingStore)

    describe("observing the foos by flow") { // not describe("foosFlow")
        context("when no foos have been added") {
            it("emits an empty collection") {
                checkAll(50, Arb.constant(Unit)) { _ -> // only the sut() defaults vary
                    sut().foosFlow.test { awaitItem().shouldBeEmpty() }
                }
            }
        }
    }

    describe("adding foos") { // not describe("addFoo")
        context("when the maximum number of foos is zero") {
            it("rejects the foo") {
                checkAll(Arb.foo()) { foo -> // the claim covers every foo, so foo is generated
                    shouldThrow<IllegalStateException> { sut(maxFoos = 0).addFoo(foo) }
                }
            }
        }
        context("when the maximum number of foos is one") {
            context("when a foo has already been added") {
                it("rejects another foo") { // a named boundary case: a fixed example, plain sut()
                    val subject = sut(maxFoos = 1)
                    subject.addFoo(Foo(id = "foo-1"))
                    shouldThrow<IllegalStateException> { subject.addFoo(Foo(id = "foo-2")) }
                }
            }
        }
    }
})
```

## Test doubles

Use the first option that keeps the test fast and deterministic: real implementation → fake (the default at I/O and nondeterminism boundaries) → stub (only to set up a `context`'s precondition) → mock or spy (a last resort, only when the call itself is the behavior) → dummy that fails if used. Details, fake-authoring rules, and mocking-library rules are in [references/test-doubles.md](references/test-doubles.md).

## Shared specs for interfaces

When an interface has more than one implementation (a fake counts), write its behavior once as a Kotest test factory and `include` it in each implementation's spec. If the implementations disagree, one of the specs fails. With TDD, write the factory first, against the interface, then make each implementation pass it.

```kotlin
// In the shared test-fixture location, next to the fake.
fun fooStoreSpec(create: () -> FooStore) = describeSpec {
    describe("loading foos") {
        it("returns a saved foo") {
            val store = create() // each test calls create(), never shares one instance
            …
        }
    }
}

class RoomFooStoreTest : DescribeSpec({
    include(fooStoreSpec { RoomFooStore(inMemoryDatabase()) })
    // Room-specific tests go here.
})

class FakeFooStoreTest : DescribeSpec({
    include(fooStoreSpec { FakeFooStore() })
})
```

- Put only behavior every implementation must share in the factory. Implementation-specific tests go in that implementation's own spec.
- If an implementation holds resources (e.g. a database connection), release them per test in that implementation's spec: keep track of what its `create` lambda opens and close it in `afterTest`.
- If an implementation can't run in a unit test (e.g. it needs a real server), include the factory only where it can run, and cover that implementation with an integration test.

## Assertions and Flows

- Use Kotest assertions, unless the project standardizes on another assertion library. Write custom Kotest matchers (with extension functions) when they improve readability and reuse.
- Test Flows with Turbine if the project has it or is new; otherwise use the project's existing approach.
