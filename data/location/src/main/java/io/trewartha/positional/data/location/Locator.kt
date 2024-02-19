package io.trewartha.positional.data.location

import io.trewartha.positional.model.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction that exposes the device's location
 */
interface Locator {

    /**
     * [Flow] that constantly emits the most recent location of the device as it changes
     */
    val location: Flow<Location>
}
