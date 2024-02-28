package io.trewartha.positional.model.core.measurement

/**
 * Distance abstraction
 */
sealed interface Distance {

    /**
     * Magnitude of the distance
     */
    val value: Double

    /**
     * Converts this distance to an equivalent distance in meters
     */
    fun inMeters(): Meters

    /**
     * Converts this distance to an equivalent distance in feet
     */
    fun inFeet(): Feet

}
