package io.trewartha.positional.domain.entities

import kotlinx.datetime.Instant

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
