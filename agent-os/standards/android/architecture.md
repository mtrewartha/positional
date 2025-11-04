## Architecture best practices

- **Use Compose UI for views**: All views should be `@Composable` unless explicitly specified
  otherwise by a human in the loop.
- **Use MVVM architecture**: Utilize the Model View ViewModel architecture when building UI.
- **Utilize the Repository pattern**: Repositories should serve as the primary abstraction for
  accessing data in the rest of the app
- **Use Kotlin's Instant and Duration for time**: To avoid ambiguity related to units, utilize
  Kotlin's `Duration` and `Instant` classes instead of other options like `Long`s to represent
  milliseconds or seconds since the epoch.
- **Use Hilt for dependency injection**:
    - Specify DI bindings in Hilt modules.
    - Avoid singletons without good reason.
    - Keep logic out of modules.
    - Provide test modules that replace production bindings with test fakes.