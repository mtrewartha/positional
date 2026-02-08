### Dependency Injection Standards

- **Constructor Injection**: Prefer constructor injection over field injection; only use the latter
  if necessary.
- **Avoid Manual Dependency Instantiation**: Instantiate dependencies in DI modules, not via manual
  instantiation in a class. Use an `@Inject`-annotated constructor with a parameter for the
  dependency type once it's provided in a module.
- **Single Responsibility Principle**: Avoid modules that provide unrelated dependencies.
- **Scoped Bindings**: Use the smallest appropriate scope/lifetime for a binding so its instance can
  be destroyed when no longer needed to conserve resources.
- **Test Bindings**: Provide test-specific bindings in test modules that can be swapped out with
  production modules during testing, e.g. using Hilt's `@TestInstallIn`:
  ```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  internal object DatabaseModule {
  
      @Provides
      fun provideDatabase(@ApplicationContext context: Context): Database =
          Room.databaseBuilder(context, Database::class.java, "database").build()
  }
  
  @Module
  @TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
  internal object TestDatabaseModule {
  
      @Provides
      fun provideDatabase(@ApplicationContext context: Context): Database =
          Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
  }
  ```
- **Use Qualifiers for Ambiguous Types**: When multiple bindings of the same type exist, use
  qualifiers (e.g. `@Named`, custom annotations) to distinguish them.
- **Lazy Injection**: Use lazy injection (e.g. `Provider<T>`, `Lazy<T>`) for dependencies that are
  expensive to create or not always needed, e.g.:
  ```kotlin
  internal class MainFooRepository @Inject constructor(
      private val defaultDispatcher: CoroutineDispatcher,
      private val fooDao: Lazy<FooDao> // FooDao is expensive to create due to file I/O and migration of DB
  ) : FooRepository {
  
      override suspend fun getFoo(id: UUID): Foo? =
          withContext(defaultDispatcher) { fooDao.get().getFoo(id)?.toDomainModel() }
    
      private fun FooDatabaseModel.toDomainModel(): Foo = // mapping logic
  }
  ```
