package io.trewartha.positional.domain.entities

import kotlinx.datetime.Instant

data class Location(
    val latitude: Double,
    val longitude: Double,
    val horizontalAccuracyMeters: Double?,
    val bearingDegrees: Double?,
    val bearingAccuracyDegrees: Double?,
    val altitudeMeters: Double?,
    val altitudeAccuracyMeters: Double?,
    val speedMetersPerSecond: Double?,
    val speedAccuracyMetersPerSecond: Double?,
    val timestamp: Instant
)
