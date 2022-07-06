package io.trewartha.positional.domain.entities

import kotlinx.coroutines.flow.Flow

interface Locator {

    /**
     * [Flow] of the current [Location]. The semantics of the [Location] may be specific to each
     * implementation of this interface.
     */
    val locationFlow: Flow<Location>
}