package io.trewartha.positional.data.compass

import io.trewartha.positional.data.measurement.Angle

/**
 * Compass azimuth angle and accuracies associated with it
 */
data class CompassAzimuth(

    /**
     * Angle from north. A positive angle indicates the user is facing east of north and a negative
     * angle indicates the user is facing west of north.
     */
    val angle: Angle,

    /**
     * Accuracy of the accelerometer or `null` if unavailable
     */
    val accelerometerAccuracy: CompassAccuracy?,

    /**
     * Accuracy of the magnetometer or `null` if unavailable
     */
    val magnetometerAccuracy: CompassAccuracy?
)
