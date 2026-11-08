# Test doubles

For each collaborator, use the first option that keeps the test fast and deterministic. Terms follow Meszaros and Fowler (dummy, fake, stub, spy, mock); the preference order follows *Software Engineering at Google*, ch. 13, and Android's testing guidance.

1. **Real implementation**: when it is fast, deterministic, and free of I/O (value objects, data classes, pure logic, in-process collaborators that are cheap to construct). Never double data classes or value types; build them directly or with their `Arb` generators.
2. **Fake**: a working, lightweight implementation (e.g. an in-memory repository backed by a `MutableStateFlow`, a fake clock). This is the default for anything at an I/O or nondeterminism boundary: network, database, filesystem, time, randomness, platform APIs.
   - Write one fake per boundary interface and keep it in the project's shared test-fixture location (e.g. a `testing` module, Gradle's `java-test-fixtures` plugin, or `commonTest`), not one fake per test class. Name it per project convention (`TestFooStore`, `FakeFooStore`, preferring the latter if there is no convention).
   - Its as-constructed state is empty and neutral, so it can serve as a `sut()` default. Tests configure it for their precondition.
   - Match the production implementation's behavior for everything a test can observe (same outputs, same state changes, same errors). Unsupported operations throw (`TODO()`, `error("…")`), never silently return.
   - Include the interface's shared spec in the fake's spec (see *Shared specs for interfaces* in SKILL.md), so the fake stays accurate.
   - Use fakes a library already ships before writing your own (e.g. `kotlinx-coroutines-test`'s `TestCoroutineScheduler` for virtual time, `kotlin.time.TestTimeSource`).
3. **Stub**: canned answers that put the subject into the precondition a `context` names (e.g. "when the server returns an error"). Prefer a configurable property on the fake (`var nextFetchResult: Result<Foo>`) over a mocking framework. Stub only what the precondition needs. If understanding the stubs requires tracing the subject's implementation, use a fake instead.
4. **Spy or mock (interaction verification)**: a last resort, for when the call itself *is* the behavior. Use it only for state-changing side effects that state can't reveal (sending an email, emitting an analytics event), or when the number or order of calls matters (e.g. "does not fetch twice when cached").
   - Prefer a recording fake (`val sentEmails: List<Email>`) and assert on what it recorded.
   - Verify only state-changing calls, and only the arguments the test cares about. Never verify queries or getters: those are change-detector tests that break on refactors, which violates black-box testing.
5. **Dummy**: a placeholder for a parameter the code path under test never uses. Make it fail loudly if used (`error("unused")`), so a hidden dependency shows up as a failure rather than a silent pass.

## Mocking libraries

If the project uses a mocking library (e.g. MockK, Mockito), use it only for the stubs and mocks that steps 3–4 allow.

- Avoid mocks that return defaults for unstubbed calls. They cause false passes.
  - In libraries where defaults are opt-in (e.g. MockK's `relaxed = true`), don't opt in.
  - In libraries that return defaults unless told otherwise (e.g. Mockito), give each mock a default answer that fails: `mock(Foo::class.java) { error("Unstubbed call: ${it.method}") }`. Then stub in the `doReturn(…).when(mock).call()` form, because the `when(mock.call())` form calls the method while stubbing and hits that default answer. (In Kotlin, `when` is written `` `when` ``, or `whenever` with mockito-kotlin.)
- Avoid static, object, and constructor mocking (e.g. MockK's `mockkStatic`, `mockkObject`, and `mockkConstructor`; Mockito's `mockStatic` and `mockConstruction`). Each marks a missing seam; inject the dependency instead.
- Don't mock third-party types you don't own. Instead, define your own boundary interface, a thin production implementation of it that calls the third-party code, and a fake for testing the code that depends on it. Test the production implementation against the real third-party code or a stand-in its maintainers ship (e.g. `MockWebServer`). Include the interface's shared spec in both specs (see *Shared specs for interfaces* in SKILL.md).
