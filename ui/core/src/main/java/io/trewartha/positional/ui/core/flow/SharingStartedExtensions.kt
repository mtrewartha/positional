package io.trewartha.positional.ui.core.flow

import kotlinx.coroutines.flow.SharingStarted

val SharingStarted.Companion.ForViewModel: SharingStarted
    get() = WhileSubscribed(
        stopTimeoutMillis = VIEW_MODEL_SHARING_STOP_TIMEOUT_MS,
        replayExpirationMillis = 0
    )

private const val VIEW_MODEL_SHARING_STOP_TIMEOUT_MS = 5_000L
