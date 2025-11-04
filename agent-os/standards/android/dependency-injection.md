## Dependency injection best practices

- **Prefer Constructor Injection**: Only use field injection when necessary, e.g. when Android is
  responsible for the constructor definition and call.
- **Avoid Manual Dependency Instantiation**: Add dependencies to the DI graph instead of manually
  instantiating them within classes.
- **Adhere to Single Responsibility Principle in modules**: Modules should provide dependencies that
  are cohesive/related.
- **Use Tight Scopes for Modules, Bindings, and Providers**: Use the smallest appropriate
  scope/lifetime for modules, bindings, and providers. This allows us to conserve system resources
  when no longer needed.
- **Provide Test Bindings**: Provide them in test modules that can be swapped out with production
  modules during testing, e.g. using Hilt's `@TestInstallIn`:
  ```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  internal object DatabaseModule {
  
      @Provides
      fun provideDatabase(@ApplicationContext context: Context): RoomDatabase.Builder<Database> =
          Room.databaseBuilder(context, Database::class.java, "database").build()
  }
  
  @Module
  @TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
  internal object TestDatabaseModule {
  
      @Provides
      fun provideDatabase(@ApplicationContext context: Context): RoomDatabase.Builder<Database> =
          Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
  }
  ```
- **Use Qualifiers for Ambiguous Types**: When multiple bindings of the same type exist, use
  qualifiers (e.g. `@Named` or custom qualifier annotations) to distinguish them.
- **Defer Injection of Expensive Dependencies**: Use `Provider<T>`, `Lazy<T>`, etc in such cases,
  e.g.:
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