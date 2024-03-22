package io.trewartha.positional.ui.core

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Indicator of state
 *
 * @param D Type of data exposed in cases where state was successfully loaded
 * @param C Type of cause exposed in cases where state failed to load
 */
@Immutable
sealed interface State<out D, out C> {

    /**
     * State is loading
     */
    @Immutable
    data object Loading : State<Nothing, Nothing>

    /**
     * State failed to load
     *
     * @param C Type of the cause of the failure
     *
     * @property cause Cause of the failure
     */
    @Immutable
    data class Failure<out C>(val cause: C) : State<Nothing, C>

    /**
     * State was successfully loaded
     *
     * @param D Type of the data wrapped by the state
     *
     * @property data Data wrapped by the state
     */
    @Immutable
    data class Loaded<out D>(val data: D) : State<D, Nothing>

    /**
     * Data wrapped by the state or `null` if the state is not loaded.
     *
     * *NOTE: This property is exposed as a convenience but can be dangerous. If [D] is an optional
     * type, this property can be `null` in two scenarios:
     *
     * 1. when the data has not loaded
     * 2. when the data has loaded
     * but was `null`. Make sure your code is aware of this potential pitfall.*
     */
    val dataOrNull: D?
        get() = (this as? Loaded)?.data

    /**
     * Cause of the failure to load the state if the state failed to load or `null` if the state
     * has not failed to load
     */
    val failureCauseOrNull: C?
        get() = (this as? Failure)?.cause
}

/**
 * Overload for [asStates] that wraps upstream exceptions in a [State.Failure] of type [Unit].
 * This is useful if you care about representing the presence of a failure (via [State.Failure]) but
 * don't care about any details of the failure (via [State.Failure.cause]).
 */
fun <T> Flow<T>.asStates(): Flow<State<T, Unit>> = asStates { }

/**
 * Wrap upstream emissions and exceptions in an appropriate [State] and re-emit them downstream
 *
 * @param T Type of the upstream data being wrapped
 * @param C Type of the cause of any failures to be wrapped
 *
 * @param transform Function to transform an upstream exception into whatever you'd like to be
 * wrapped in a [State.Failure] to be emitted downstream
 *
 * @return Flow that wraps emissions and exceptions from the original flow into an appropriate
 * [State] and re-emits them downstream
 */
fun <T, C> Flow<T>.asStates(transform: (Exception) -> C): Flow<State<T, C>> =
    this.map<T, State<T, C>> {
        State.Loaded(it)
    }.onStart {
        emit(State.Loading)
    }.catch { throwable ->
        if (throwable is Exception) {
            emit(State.Failure(transform(throwable)))
        } else {
            throw throwable
        }
    }
