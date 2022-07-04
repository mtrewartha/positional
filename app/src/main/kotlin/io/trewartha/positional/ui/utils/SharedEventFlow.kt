package io.trewartha.positional.ui.utils

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <E> mutableSharedViewModelEventFlow(): MutableSharedFlow<E> = MutableSharedFlow<E>(
    replay = 0,
    extraBufferCapacity = 10,
    onBufferOverflow = BufferOverflow.SUSPEND
)