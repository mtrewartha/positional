### Error Handling Standards

- **Clear, Actionable Errors**: Throw or return clear, unambiguous, actionable types that indicate
  what went wrong so callers can respond appropriately. Re-use existing types (exceptions or
  otherwise) when reasonable. For specific errors that only make sense in the context of a
  particular module, define them there.
- **Document Error Handling**: Clearly document which exceptions or return types a function can
  produce under various conditions. Use KDoc's `@return` and `@throws` tags to do so.
- **Fail Fast and Explicitly**: Validate input and check preconditions early; fail with clear
  exceptions or return types rather than allowing invalid state
- **Boundary Error Handling**:
    - Catch broad Exception types only at these system boundaries:
        - Android framework interaction points like Activities' callbacks or View Models' functions:
          These prevent unexpected exceptions from taking down the app and let us communicate
          failures to the user.
        - Interactions with third-party code: Catch exceptions from the smallest scope of code
          possible to give us the best chance of translating them into meaningful errors for
          higher-level code or UX.
    - Do not catch `CancellationException` unless you rethrow it unconditionally.
    - Do not catch `Throwable` unless only logging it. In such a case, rethrow unconditionally. Do
      not attempt to recover from it; doing so risks additional harm.
    - Use `runCatchingExceptions`; do not use `try`/`catch` or `runCatching` directly since these
      both make it too easy to accidentally catch `CancellationException` and `Throwable`.
- **Avoid Result for Internal Logic**: Only use `kotlin.Result` in boundary error handling. Explicit
  handling of specific errors promotes healthy code that is more aware of the specific errors it
  might encounter and is equipped to handle them in ways the produce software that is easier to
  maintain and that results in good UX. The `Result` type encourages blind handling of generic
  errors that are poorly understood, which results in poorly designed code and poor UX.
- **Clean Up Resources**: Always clean up resources (files, connections, anything that implements
  `Closeable` or the like) in finally blocks or equivalent mechanisms
