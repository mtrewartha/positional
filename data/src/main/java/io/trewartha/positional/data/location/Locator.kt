package io.trewartha.positional.data.location

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction that exposes the device's location
 */
interface Locator {

    /**
     * Hot [Flow] that constantly emits the most recent location of the device as it changes
     */
    val location: Flow<Location>
}
