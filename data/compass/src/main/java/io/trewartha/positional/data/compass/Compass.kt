package io.trewartha.positional.data.compass

import io.trewartha.positional.model.compass.Azimuth
import kotlinx.coroutines.flow.Flow

/**
 * Compass abstraction that exposes the device's orientation to north as an azimuth
 */
interface Compass {

    /**
     * [Flow] that constantly emits the most recent azimuth
     */
    val azimuth: Flow<Azimuth>
}
