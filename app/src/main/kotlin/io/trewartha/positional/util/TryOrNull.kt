package io.trewartha.positional.util

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> tryOrNull(block: () -> T): T? =
    try {
        block()
    } catch (exception: Exception) {
        if (exception is CancellationException) {
            throw exception
        } else {
            null
        }
    }
