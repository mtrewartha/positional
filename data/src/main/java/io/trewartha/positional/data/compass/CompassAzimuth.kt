package io.trewartha.positional.data.compass

import io.trewartha.positional.data.measurement.Angle

/**
 * Compass azimuth angle and accuracies associated with it
 *
 * @throws IllegalArgumentException if [angle] is outside of the range 0..<360
 */
data class CompassAzimuth @Throws(IllegalArgumentException::class) constructor(

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
) {

    init {
        require(angle.inDegrees().value in VALID_RANGE) { "$angle is outside of range 0..<360" }
    }
}

private const val DEGREES_0 = 0f
private const val DEGREES_360 = 360f
private val VALID_RANGE = DEGREES_0..<DEGREES_360
