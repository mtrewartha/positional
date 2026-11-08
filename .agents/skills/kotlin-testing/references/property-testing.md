# Property testing with Kotest

Applies only when the project uses `kotest-property` (new projects do). The APIs below were checked against Kotest 6.2; that's not a version requirement. Use the project's version, or the latest stable release in a new project.

## Randomness and seeds

All randomness in a test comes from a property test's seeded source: inside `checkAll`/`forAll`, via `checkAll` arguments, `Arb.x().bind()`, or `randomSource()` for raw values.

- Never call `.next()`, `.single()`, `Random.Default`, or `RandomSource.default()` in tests or `sut()` defaults. Those values are unseeded, unreported, and can't be replayed. Kotest deprecates `.next()` inside property contexts for exactly this reason.
- On failure, Kotest lists every input, including each `bind()` value ("generated within property context"), and prints the seed.
- Kotest saves a failing seed to `~/.kotest/seeds/<spec>/<test>` and reuses it on the next run until the test passes. To keep a failure as a permanent regression test, pin its seed with `PropTestConfig(seed = …)`.

## Running `checkAll`

- **Small, finite domains**: use `Exhaustive` (`Exhaustive.enum<T>()`, `.collection(…)`, `.of(…)`). When every generator passed to `checkAll` is an `Exhaustive`, every combination runs once, deterministically, and the iteration count is ignored.
- **Mixing `Exhaustive` and `Arb`**: values are zipped, not combined, and the exhaustive cycles. When you need full coverage of a dimension, quantify every dimension exhaustively.
- **Open or large domains**: use `Arb`.
- **Only the `sut()` defaults vary**: when a test has no inputs of its own, use `checkAll(n, Arb.constant(Unit)) { _ -> … }`. Don't use `Exhaustive.constant(Unit)`, which runs once.
- **Iteration counts**: the default is 1000. Keep it proportionate to test speed: pass a count per test (`checkAll(50, …)`) or set `PropertyTesting.defaultIterationCount` in project config.
- **Naming**: the `it` states the outcome only (`it("rejects the request")`). Don't list quantified dimensions ("… regardless of the client and the window"): every input the `context` path doesn't constrain is generated, so "for any value" is already the default. A narrowed domain is a precondition and gets its own `context` (`context("when the maximum is at least two")`), with a generator matching it. To check that a body matches its claim, verify that every pinned value is justified by the `context` path and everything else is generated.

## Model generators

Each model a module exposes gets an `Arb` generator, defined as `fun Arb.Companion.<model>(): Arb<Model>` in the project's shared test-fixture location, so other modules' tests can reuse it. Search for existing generators (`fun Arb.Companion.`) before writing new ones.

```kotlin
fun Arb.Companion.distance(): Arb<Distance> = Arb.bind(Arb.double(0.0, 1e6), Arb.enum<Distance.Unit>(), ::Distance)
fun Arb.Companion.user(): Arb<User> = Arb.bind(Arb.uuid(), Arb.string(1..40), Arb.email(), Arb.distance(), ::User)
```

- Build generators from Kotest's own `Arb`s (`Arb.int`, `Arb.string`, `Arb.email`, `Arb.uuid`, `Arb.enum`, `Arb.duration`, `Arb.localDate`, …) and other models' generators. Write custom `Arb`s only for domain types Kotest lacks.
- Compose with `Arb.bind(arbA, arbB, …, ::Model)`, so each field shrinks independently on failure. On the JVM, reflective `Arb.bind<Model>()` also works when no field needs a custom generator.
- Use the `arbitrary { … bind() … }` builder only when fields depend on each other or need imperative logic. It doesn't shrink by itself, so pair it with a custom `Shrinker` if shrinking matters.
- Avoid `flatMap` for composition: only the outer value shrinks.
- Cover **all** properties, including enum and unit fields. A generator that fixes any property (e.g. always `.meters` for a `Distance`) misses bugs that only show up with other values.
- Don't add generator parameters that map to model properties. Pin a property at the use site with `copy` (`Arb.user().bind().copy(email = "bob@example.com")`) or `.map { it.copy(…) }`. Parameters may tune generation itself, e.g. `Arb.sentence(wordCount: IntRange)`.
