package io.trewartha.positional.data.location

import kotlinx.datetime.Instant

/**
 * Location somewhere on Earth, represented in a decimal degrees latitude, longitude, altitude, and
 * timestamp (among other metadata).
 */
interface Location {
    val latitude: Double
    val longitude: Double
    val horizontalAccuracyMeters: Float?
    val bearingDegrees: Float?
    val bearingAccuracyDegrees: Float?
    val altitudeMeters: Double?
    val altitudeAccuracyMeters: Float?
    val speedMetersPerSecond: Float?
    val speedAccuracyMetersPerSecond: Float?
    val timestamp: Instant
    val magneticDeclinationDegrees: Float
}
