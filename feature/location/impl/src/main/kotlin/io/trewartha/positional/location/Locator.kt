package io.trewartha.positional.location

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction that exposes the device's location
 */
public interface Locator {

    /**
     * [Flow] that constantly emits the most recent location of the device as it changes
     */
    public val location: Flow<Location>
}
