---
paths:
  - "**/src/**/*.kt"
---

# Dependency Injection Rules

This project uses [Metro](https://zacsweers.github.io/metro/latest/) for dependency injection with
Anvil-style aggregation and a single `AppScope`.

- **Constructor Injection**: Prefer constructor injection over field injection; only use the latter
  if necessary.
- **Avoid Manual Dependency Instantiation**: Instantiate dependencies in DI provider interfaces,
  not via manual instantiation in a class. Use an `@Inject`-annotated constructor with a parameter
  for the dependency type once it's provided.
- **Single Responsibility Principle**: Avoid provider interfaces that provide unrelated
  dependencies.
- **Scoped Bindings**: Use `@SingleIn(AppScope::class)` for bindings that should be singletons.
  Only scope when necessary — unscoped bindings are created fresh each time they're injected.
- **Aggregation with `@ContributesTo` and `@ContributesBinding`**: Feature and core modules
  contribute bindings to the graph via annotations. No need to modify the app module's graph when
  adding new bindings:
  ```kotlin
  // For @Provides-style bindings, use a @ContributesTo interface:
  @ContributesTo(AppScope::class)
  public interface FooProviders {

      @Provides
      public fun fooManager(application: Application): FooManager =
          application.getSystemService(FooManager::class.java)
  }

  // For simple interface-to-implementation bindings, use @ContributesBinding on the class:
  @ContributesBinding(AppScope::class)
  @Inject
  internal class MainFooRepository() : FooRepository {
      // ...
  }
  ```
- **Visibility for Contributed Types**: `@ContributesTo` interfaces must be `public` since Metro's
  aggregation requires cross-module access. `@ContributesBinding` and `@ContributesIntoMap` classes
  should be `internal` — the `generateContributionProviders` feature generates public `@Provides`
  declarations that expose only the bound type, so the implementation class stays encapsulated.
  Only keep a contributed class `public` if it is directly referenced from another module (e.g. a
  ViewModel used cross-module in a Composable).
- **ViewModel Integration**: ViewModels use `@ContributesIntoMap` with `@ViewModelKey`:
  ```kotlin
  @ContributesIntoMap(AppScope::class)
  @ViewModelKey
  @Inject
  internal class FooViewModel(
      fooRepository: FooRepository
  ) : ViewModel()
  ```
  In Composables, use `metroViewModel()` (from `dev.zacsweers.metrox.viewmodel`).
- **Use Qualifiers for Ambiguous Types**: When multiple bindings of the same type exist, use
  qualifiers (e.g. `@Named`, custom annotations) to distinguish them.
- **Lazy Injection**: Use lazy injection (e.g. `Provider<T>`, `Lazy<T>`) for dependencies that are
  expensive to create or not always needed, e.g.:
  ```kotlin
  public class MainFooRepository @Inject constructor(
      private val defaultDispatcher: CoroutineDispatcher,
      private val fooDao: Lazy<FooDao> // FooDao is expensive to create due to file I/O and migration of DB
  ) : FooRepository {

      override suspend fun getFoo(id: UUID): Foo? =
          withContext(defaultDispatcher) { fooDao.get().getFoo(id)?.toDomainModel() }

      private fun FooDatabaseModel.toDomainModel(): Foo = // mapping logic
  }
  ```