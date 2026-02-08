### Coroutines Standards

- **Well-Defined Scopes**: Strive to launch coroutines in a well-defined scopes that have a proper
  beginning and end (e.g., `viewModelScope`, `lifecycleScope`) to ensure proper cancellation and
  resource management. Never create a scope without cancelling it, except at the application level.
- **No CoroutineScope Dependencies**: Never inject a `CoroutineScope` into a class or instantiate
  one manually.
- **Cancellation Exceptions**: Never catch a `CancellationException` unless you rethrow it.
- **Inject Dispatchers**: Always inject `CoroutineDispatcher` instances (e.g., `Dispatchers.IO`,
  `Dispatchers.Default`) so they can be overridden in tests.
- **Use Background Dispatchers**: Never run long-running or blocking operations on
  `Dispatchers.Main`. Use dispatchers that run coroutines on background threads instead (e.g.,
  `Dispatchers.IO`, `Dispatchers.Default`).
- **Prefer `Dispatchers.Default`**: Only use `Dispatchers.IO` for blocking I/O operations that are
  not _already_ taken off the main thread by a library you're using (e.g. filesystem I/O).
- **Prefer Declarative Flows**: Use declarative flows instead of imperatively updating flows (via
  MutableStateFlow, Channels, or other constructs) to make them easier to understand/maintain and to
  prevent wasteful work being done to update flows that no one is collecting. A common exception to
  this best practice is when state is inherently imperative (e.g. a View Model that determines a "
  refreshing" state based on the outcome of an imperative "refresh" function call). In such cases,
  an imperative construct like `MutableStateFlow` is more appropriate. For example:
  ```kotlin
  class GoodViewModel @Inject constructor(
      fooRepository: FooRepository
  ) : ViewModel() {
  
      // Good: Everything you need to understand when/how this flow emits data is right here.
      val state: StateFlow<State?> =
          fooRepository.getFooFlow()
              .map { foo -> State.Loaded(foo) }
              .catch { emit(State.Error(it)) }
              .stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)
  }
  
  class BadViewModel @Inject constructor(
      private val fooRepository: FooRepository
  ) : ViewModel() {
  
      // Bad: You have to read the definitions here and then find every spot in the view model where the flow is updated,
      // like the init below, to understand when/how it emits data.
      private val _state = MutableStateFlow<State>(State.Loading)
      val state: StateFlow<State> = _state.asStateFlow()
  
      init {
          viewModelScope.launch { refresh() }
      }
  
      private suspend fun refresh() {
          try {
              val foo = fooRepository.getFoo()
              _state.update { State.Loaded(foo) }
          } catch (cancellationException: CancellationException) {
              throw cancellationException
          } catch (exception: Exception) {
              _state.update { State.Error(exception) }
          }
      }
  }
  
  class ExceptionalViewModel @Inject constructor(
      private val fooRepository: FooRepository
  ) : ViewModel() {
  
      // Exception to the rule: This state is inherently imperative because it is determined by the start and end of a
      // function call. Combine it into the other declaratively defined state flow below.
      private val refreshingStateFlow = MutableStateFlow(false)
  
      // OK: Most of the state is still declarative and easy to find here, but you do still have to look for where the
      // refreshing state is determined.
      val state: StateFlow<State> =
          combine<Foo?, Boolean, State>(fooRepository.getFooFlow(), refreshingStateFlow) { foo, refreshing ->
              State.Loaded(foo, refreshing)
          }.catch {
              if (it is Exception) emit(State.Error(it)) else throw it
          }.stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State.Loading)
  
      init {
          viewModelScope.launch { refresh() }
      }
  
      private suspend fun refresh() {
          refreshingStateFlow.update { true }
          runCatchingExceptions { fooRepository.refresh() }
          refreshingStateFlow.update { false }
      }
  }
  ```
