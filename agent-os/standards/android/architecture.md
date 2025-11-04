## Architecture best practices

- **Modularize code into Gradle subprojects**:
    - **One Gradle subproject per module**: This allows for build optimization based on separation
      of concerns.
    - **Group modules under "app", "feature", and "core" directories**: Base the directory on the
      functionality the module exposes. This helps us intuitively locate code.
    - **Hide module implementation details**:
        - Hide Gradle dependency details with appropriate dependency configurations (e.g.
          `implementation` instead of `api`)
        - Hide implementation details in Kotlin code with appropriate visibility modifiers (e.g.
          `internal` instead of `public` or no modifier)
- **Use Compose UI for views**: All views should be `@Composable` unless explicitly specified
  otherwise by a human in the loop.
- **Use MVI architecture**: Utilize the Model View Intent (MVI) architecture when building UI.
- **Use repository pattern for abstracting data access**: Data operations should be abstracted
  behind a repository.
    - Use one repository per data type.
    - When a repository performs both local and remote operations on data, abstract all local
      operations behind a local-specific "data source" interface and all remote operations behind a
      remote-specific one, then combine their usage in the repository.
- **Use Hilt for dependency injection (DI)**: This is the gold standard for Android apps.