package io.trewartha.positional.core.ui.flow

import kotlinx.coroutines.flow.SharingStarted

public val SharingStarted.Companion.ForViewModel: SharingStarted
    get() = WhileSubscribed(
        stopTimeoutMillis = VIEW_MODEL_SHARING_STOP_TIMEOUT_MS,
        replayExpirationMillis = 0
    )

private const val VIEW_MODEL_SHARING_STOP_TIMEOUT_MS = 5_000L
