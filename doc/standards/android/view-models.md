### View Model Standards

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
- **Action Handling**:
    - Define a sealed `Action` interface in the View Model whose implementations are all of the view
      actions the view model handles.
    - `Action` implementations should be a `data object` or `data class` with immutable properties.
    - Expose an `onAction(action: Action)` function to receive `Action` instances from the View,
      process them, and update state.
- **Events/Effects**: Instead of modeling one-shot events or effects as a separate `Flow` from
  `state`, they should be "reduced" to state changes and reflected in the single `state` flow. When
  a piece of state should only be represented once in a View, the View Model should expose an
  additional `Action` that lets the View signal to the View Model that that piece of state has been
  represented so that the View Model can then update the state accordingly (e.g. so a Snackbar isn't
  shown once and then again after a device rotation).
- **No Direct View References**: View Models must never hold references to Views, Activities,
  Fragments, or Contexts (except `Application` context if absolutely necessary).
- **Single Responsibility**: Each View Model should correspond to a single screen or well-defined UI
  component.
- **View Model Scope**: Launch coroutines in a scope corresponding to the View Model lifecycle to
  ensure proper cancellation when View Model goes out of scope, e.g. `viewModelScope` for an
  AndroidX `ViewModel`.
- **Complex Business Logic**: Extract complex or reusable business logic into use cases that get
  injected into the view model.

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
    sealed interface Action {
        data object Refresh : Action
        data class CreateData(val value: Int) : Action
        data object DismissError : Action
    }

    // Sealed interface representing all possible errors
    sealed interface Error {
        data object Connection : Error
        data object Unexpected : Error
    }

    private val errorFlow =
        MutableStateFlow<Error?>(null) // Imperatively updated error state component
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
        }.stateIn(viewModelScope, SharingStarted.ForViewModel, initialValue = State())

    // Single function to handle all actions from view
    fun onAction(action: Action) {
        when (action) {
            is Action.CreateData -> handleCreateDataAction(action)
            is Action.DismissError -> handleDismissErrorAction()
            is Action.Refresh -> handleRefreshAction()
        }
    }

    private fun handleCreateDataAction(action: Action.CreateData) {
        viewModelScope.launch { createDataUseCase(action.value) }
    }

    private fun handleDismissErrorAction() {
        errorFlow.update { null }
    }

    private fun handleRefreshAction() {
        viewModelScope.launch { refresh() }
    }

    private suspend fun refresh() {
        try {
            refreshingFlow.update { true }
            dataRepository.refresh()
        } catch (_: ConnectionException) {
            errorFlow.update { Error.Connection }
        } catch (_: Exception) {
            errorFlow.update { Error.Unexpected }
        } finally {
            refreshingFlow.update { false }
        }
    }
}
```
