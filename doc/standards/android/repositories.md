### Repository Standards

- **Repositories Like Sets**: Use repository interfaces to provide a simple collection-like
  abstraction for a single domain type that allows adding, removing, getting, and updating items in
  the repository based on unique identifiers.
- **Domain Type Based Names**: Name repository interfaces by prepending the domain type they vend to
  the word "Repository". For example, a repository that vends `Foo` instances would be named
  "FooRepository".
- **Repository Interfaces**: Always define repository interfaces separate from repository
  implementations.
- **Test Fakes**: Provide in-memory fake implementations of repository interfaces for use in tests
  named by prefixing the word "Fake" to the repository interface name, e.g. "FakeFooRepository".
- **Getter Functions**: Follow these conventions when defining functions that look data up:
    - If returning a `Flow` so the caller can observe a single item, then:
        - Do not mark the function `suspend`.
        - Set the name to `getTFlow` where `T` is the domain type name, e.g. `getFooFlow`.
        - Set the return type to `Flow<T?>` where `T` is the domain type name, e.g. `Flow<Foo?>`.
        - When the returned `Flow` is collected, it should emit the current item at the start of
          collection (or `null` if not found), then emit the item or `null` every time it changes to
          something distinct.
        - Use `distinctUntilChanged()` to avoid duplicate `Flow` emissions.
        - Use `flowOn(...)` to ensure the `Flow` does its work on an appropriate background
          dispatcher.
    - If returning a single item, then:
        - Mark the function `suspend`.
        - Set the name to `getT` where `T` is the domain type name, e.g. `getFoo`.
        - Set the return type to `T?` where `T` is the domain type name, e.g. `Foo?`.
        - Return the found item or `null` if not found.
        - Use `withContext(...)` to ensure the function does its work on an appropriate background
          dispatcher.
    - If returning a `Flow` so the caller can observe a list of items, then:
        - Do not mark the function `suspend`.
        - Set the name to `getTsFlow` where `Ts` is plural of the domain type name, e.g.
          `getFoosFlow`. If the function should return some subset of the full collection of items,
          name it accordingly, e.g. `getBarredFoosFlow`.
        - Set the return type to `Flow<Collection<T>>` where `Collection` is an appropriate
          `Collection` type (e.g. `Set`, `List`) and `T` is the domain type name, e.g.
          `Flow<List<Foo>>`.
        - When the returned `Flow` is collected, it should emit the current items at the start of
          collection (or an empty collection if none), then emit an updated collection every time it
          changes to something distinct.
        - Use `distinctUntilChanged()` to avoid duplicate `Flow` emissions.
        - Use `flowOn(...)` to ensure the `Flow` does its work on an appropriate background
          dispatcher.
    - If returning a list of items, then:
        - Mark the function `suspend`.
        - Set the name to `getTs` where `Ts` is the plural of the domain type name, e.g. `getFoos`.
        - Set the return type to `Collection<T>` where `Collection` is an appropriate `Collection`
          type (e.g. `Set`, `List`) and `T` is the domain type name, e.g. `List<Foo>`.
        - Return the collection of applicable items or an empty collection if none.
        - Use `withContext(...)` to ensure the function does its work on an appropriate background
          dispatcher.
- **Add Functions**: Follow these conventions when defining functions that add data:
    - Mark the function `suspend`.
    - Set the name to `addT` where `T` is the domain type name, e.g. `addFoo`.
    - Accept a single instance of the domain type to add as a parameter, e.g. `foo: Foo`.
    - Return `Unit`.
- **Update Functions**: Follow these conventions when defining functions that update data:
    - Mark the function `suspend`.
    - Set the name to `updateT` where `T` is the domain type name, e.g. `updateFoo`.
    - Accept a single instance of the domain type to update as a parameter, e.g. `foo: Foo`.
    - Return `Unit`.
    - Ensure calls to the function are idempotent.
    - If the repository doesn't contain an item matching the provided item's unique identifier, the
      function should throw a `NoSuchElementException`.
- **Remove Functions**: Follow these conventions when defining functions that remove data:
    - Mark the function `suspend`.
    - Set the name to `removeT` where `T` is the domain type name, e.g. `removeFoo`.
    - Accept a parameter that uniquely identifies instances of the domain type, e.g. if `Foo`
      instances are uniquely identified by an `id: String`, then the parameter would be
      `id: String`.
    - Ensure calls to the function are idempotent.
    - If the repository doesn't contain an item matching the provided unique identifier, the
      function should throw a
      `NoSuchElementException`.
