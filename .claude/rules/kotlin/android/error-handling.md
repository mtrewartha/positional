---
paths:
  - "**/src/**/*.kt"
---

# Error Handling Rules

- **Catch narrowly by default**: Use `try`/`catch` with one `catch` per specific exception type you
  can actually handle.
- **Broad catches only at boundaries**: Catch everything only when wrapping third-party code that
  can throw unknown exceptions, or at the Android/UI boundary as the last line of defense. Do it with
  `runCatchingExceptions` from `:core:error` (opt-in via `@BoundaryErrorHandling`), never with
  `runCatching`, because `runCatching` also catches `CancellationException` and `Error`.
- **No `Result` in internal logic**: `kotlin.Result` belongs only at those boundaries. Elsewhere,
  throw or return specific, module-appropriate error types so callers handle them deliberately.
- **Document failures**: Use KDoc `@throws` for each exception a public function can throw.
