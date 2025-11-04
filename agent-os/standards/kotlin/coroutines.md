## Coroutines best practices

- **Well-Defined Scopes**: Strive to launch coroutines in a well-defined scopes that have a proper
  beginning and end (e.g., `viewModelScope`, `lifecycleScope`) to ensure proper cancellation and
  resource management. Never create a scope without cancelling it.
- **No CoroutineScope Dependencies**: Never inject a `CoroutineScope` into a class or instantiate
  one manually.
- **Cancellation Exceptions**: Never catch a `CancellationException` unless you rethrow it.
- **Inject Dispatchers**: Always inject `CoroutineDispatcher` instances (e.g., `Dispatchers.IO`,
  `Dispatchers.Default`) using qualifier annotations so they can be overridden in tests.
- **Use Background Dispatchers**: Never run long-running or blocking operations on
  `Dispatchers.Main`. Use dispatchers that run coroutines on background threads instead (e.g.
  `Dispatchers.IO`, `Dispatchers.Default`).
- **Prefer `Dispatchers.Default`**: Only use `Dispatchers.IO` for blocking I/O operations that are
  not _already_ taken off the main thread by a library you're using (e.g. filesystem I/O).