## View Model best practices

- **MVI Architecture**: Follow Model-View-Intent (MVI) architecture with strict unidirectional data
  flow:
    - View observes state from View Models
    - View sends user actions (i.e. the "Intent" in MVI) to View Model
    - View Model processes actions and updates state
    - State changes flow back to View
- **State Exposure**: Expose UI state using a single `StateFlow<State>` where `State` is an
  immutable data class defined inside the View Model, e.g. `val state: StateFlow<State>`.
    - Prefer declaratively defined state over imperatively updating `MutableStateFlow`s.
    - If you are imperatively updating part of the state, isolate that part into its own
      `MutableStateFlow` and combine it into other state using `combine` operator.
    - Provide a sensible initial state in the StateFlow initialization.
- **One-Time Events**: TODO
- **Action Handling**:
    - Define a sealed `Action` interface in the View Model whose implementations are all of the view
      actions the view model handles.
    - `Action` implementations should be a `data object` or `data class` with immutable properties.
    - Expose an `onAction(action: Action)` function to receive `Action` instances from the View,
      process them, and update state.
- **No Direct View References**: View Models must never hold references to Views, Activities,
  Fragments, or Contexts
  (except `Application` context if absolutely necessary).
- **Single Responsibility**: Each View Model should correspond to a single screen or well-defined UI
  component.
- **View Model Scope**: Launch coroutines in a scope corresponding to the View Model lifecycle to
  ensure proper cancellation when View Model goes out of scope, e.g. `viewModelScope` for an
  AndroidX `ViewModel`.
- **Complex Business Logic**: Extract complex or reusable business logic into use cases that get
  injected into and invoked by the view model.

Example of View Model that follows all best practices:

```kotlin
class ExampleViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val createDataUseCase: CreateDataUseCase // Separate use case for complex or reusable logic
) : ViewModel() {

    // Immutable data class for state
    data class State(
        val data: List<Int>? = null, // Null indicates data not loaded
        val error: Error? = null, // Null indicates no error
        val refreshing: Boolean = false
    )

    // Sealed interface representing all possible actions
    sealed interface Action
    data object Refresh : Action
    data class CreateData(val value: Int) : Action

    // Sealed interface representing all possible errors
    sealed interface Error
    data object ConnectionError : Error
    data object UnexpectedError : Error

    private val errorFlow = MutableStateFlow(null) // Imperatively updated error state component
    private val refreshingFlow =
        MutableStateFlow(true) // Imperatively updated refreshing state component

    // Single state flow exposed to view
    val stateFlow: StateFlow<State> =
        // Declarative state combined with imperatively updated refreshing state
        combine(
            dataRepository.getDataFlow(),
            refreshingFlow,
            errorFlow
        ) { data, refreshing, error ->
            State(data, error, refreshing)
        }.stateIn(
            viewModelScope,
            SharingStarted.ForViewModel,
            initialValue = State()
        )

    // Single function to handle all actions from view
    fun onAction(action: Action) {
        when (action) {
            is CreateData -> handleCreateDataAction(action)
            is Refresh -> handleRefreshAction()
        }
    }

    private fun handleCreateDataAction(action: CreateData) {
        viewModelScope.launch { createDataUseCase(action.value) }
    }

    private fun handleRefreshAction() {
        viewModelScope.launch { refresh() }
    }

    private suspend fun refresh() {
        try {
            refreshingFlow.update { true }
            dataRepository.refresh()
        } catch (_: ConnectionException) {
            errorFlow.update { ConnectionError }
        } catch (_: Exception) {
            errorFlow.update { UnexpectedError }
        } finally {
            refreshingFlow.update { false }
        }
    }
}
```