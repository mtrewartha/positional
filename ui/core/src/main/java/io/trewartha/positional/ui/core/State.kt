package io.trewartha.positional.ui.core

import androidx.compose.runtime.Immutable

@Immutable
sealed interface State<out T, out E> {

    @Immutable
    data object Loading : State<Nothing, Nothing>

    @Immutable
    data class Error<out E>(val error: E) : State<Nothing, E>

    @Immutable
    data class Loaded<out T>(val data: T) : State<T, Nothing>

    val dataOrNull: T?
        get() = (this as? Loaded)?.data

    val errorOrNull: E?
        get() = (this as? Error)?.error
}

/**
 * Overload for [wrapInState] that wraps upstream exceptions in a [State.Error] of type [Unit].
 * This is useful if you care about representing the presence of a failure (via [State.Error]) but
 * don't care about any details of the failure (via [State.Error.error]).
 */
fun <T> Flow<T>.wrapInState(): Flow<State<T, Unit>> = wrapInState { }

/**
 * Wrap upstream emissions and exceptions in an appropriate [State] and re-emit them downstream
 *
 * @param T Type of the upstream data being wrapped
 * @param E Type of the cause of any failures to be wrapped
 *
 * @param transform Function to transform an upstream exception into whatever you'd like to be
 * wrapped in a [State.Error] to be emitted downstream
 *
 * @return Flow that wraps emissions and exceptions from the original flow into an appropriate
 * [State] and re-emits them downstream
 */
fun <T, E> Flow<T>.wrapInState(transform: (Exception) -> E): Flow<State<T, E>> =
    this.map<T, State<T, E>> {
        State.Loaded(it)
    }.catch { throwable ->
        if (throwable is Exception) {
            emit(State.Error(transform(throwable)))
        } else {
            throw throwable
        }
    }
