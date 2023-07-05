package io.trewartha.positional.data.location

import kotlinx.datetime.Instant

/**
 * Location somewhere on Earth, represented in a decimal degrees latitude, longitude, altitude, and
 * timestamp (among other metadata).
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracyMeters: Float?,
    val bearingDegrees: Float?,
    val bearingAccuracyDegrees: Float?,
    val altitudeMeters: Double?,
    val altitudeAccuracyMeters: Float?,
    val speedMetersPerSecond: Float?,
    val speedAccuracyMetersPerSecond: Float?,
    val timestamp: Instant,
    val magneticDeclinationDegrees: Float
) {
    val altitudeFeet: Double?
        get() = altitudeMeters?.let { metersToFeet(it) }

    val altitudeAccuracyFeet: Float?
        get() = altitudeAccuracyMeters?.let { metersToFeet(it) }

    val horizontalAccuracyFeet: Float?
        get() = horizontalAccuracyMeters?.let { metersToFeet(it) }

    val speedKilometersPerHour: Float?
        get() = speedMetersPerSecond?.let { metersPerSecondToKilometersPerHour(it) }

    val speedMilesPerHour: Float?
        get() = speedMetersPerSecond?.let { metersPerSecondToMilesPerHour(it) }

    val speedAccuracyKilometersPerHour: Float?
        get() = speedAccuracyMetersPerSecond?.let { metersPerSecondToKilometersPerHour(it) }

    val speedAccuracyMilesPerHour: Float?
        get() = speedAccuracyMetersPerSecond?.let { metersPerSecondToMilesPerHour(it) }

    private companion object {

        private const val FEET_PER_METER = 3.2808399200439453
        private const val MPH_PER_MPS = 2.236936
        private const val KPH_PER_MPS = 3.6

        fun metersToFeet(meters: Double): Double = meters * FEET_PER_METER

        fun metersToFeet(meters: Float): Float = (meters * FEET_PER_METER).toFloat()

        fun metersPerSecondToMilesPerHour(metersPerSecond: Float): Float =
            (metersPerSecond * MPH_PER_MPS).toFloat()

        fun metersPerSecondToKilometersPerHour(metersPerSecond: Float): Float =
            (metersPerSecond * KPH_PER_MPS).toFloat()
    }
}
