package io.trewartha.positional.data.compass

import kotlinx.coroutines.flow.Flow

/**
 * Compass abstraction that exposes a user's azimuth, i.e. the angle from north that the user is
 * facing, as a flow that constantly emits the most recent azimuth
 */
interface Compass {

    /**
     * [Flow] that constantly emits the most recent azimuth, i.e. the angle from north that the user
     * is facing
     */
    val azimuth: Flow<CompassAzimuth>
}
