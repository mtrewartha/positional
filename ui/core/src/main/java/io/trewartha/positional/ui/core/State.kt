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
