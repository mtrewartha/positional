package io.trewartha.positional.ui

import androidx.compose.runtime.Immutable

@Immutable
sealed interface State<T, E> {

    @Immutable
    class Loading<T, E> : State<T, E>

    @Immutable
    data class Error<T, E>(val error: E) : State<T, E>

    @Immutable
    data class Loaded<T, E>(val data: T) : State<T, E>

    val dataOrNull: T?
        get() = (this as? Loaded)?.data

    val errorOrNull: E?
        get() = (this as? Error)?.error
}
