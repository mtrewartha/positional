package io.trewartha.positional.data.compass

import kotlinx.coroutines.flow.Flow

/**
 * Compass abstraction that exposes the device's orientation to north as an azimuth
 */
interface Compass {

    /**
     * Hot [Flow] that constantly emits the most recent azimuth
     */
    val azimuth: Flow<CompassAzimuth>
}
