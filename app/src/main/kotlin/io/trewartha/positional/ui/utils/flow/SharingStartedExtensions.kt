package io.trewartha.positional.ui.utils

import kotlinx.coroutines.flow.SharingStarted

val SharingStarted.Companion.ForViewModel: SharingStarted
    get() = WhileSubscribed(
        stopTimeoutMillis = VIEW_MODEL_SHARING_STOP_TIMOUT_MS,
        replayExpirationMillis = 0
    )

private const val VIEW_MODEL_SHARING_STOP_TIMOUT_MS = 3_000L