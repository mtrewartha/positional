package io.trewartha.positional.model.core.measurement

/**
 * Speed abstraction
 */
sealed interface Speed {

    /**
     * Magnitude of the speed
     */
    val value: Double

    /**
     * Converts this speed to an equivalent speed in kilometers per hour
     */
    fun inKilometersPerHour(): KilometersPerHour

    /**
     * Converts this speed to an equivalent speed in meters per second
     */
    fun inMetersPerSecond(): MetersPerSecond

    /**
     * Converts this speed to an equivalent speed in miles per hour
     */
    fun inMilesPerHour(): MilesPerHour
}
