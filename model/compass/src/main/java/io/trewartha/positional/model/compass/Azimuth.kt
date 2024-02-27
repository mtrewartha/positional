package io.trewartha.positional.model.compass

import io.trewartha.positional.model.core.measurement.Angle

/**
 * Azimuth angle and accuracies associated with it
 *
 * @throws IllegalArgumentException if [angle] is outside of the range 0..<360
 */
data class Azimuth @Throws(IllegalArgumentException::class) constructor(

    /**
     * Angle from north. A positive angle indicates the user is facing east of north and a negative
     * angle indicates the user is facing west of north.
     */
    val angle: Angle,

    /**
     * Accuracy of the accelerometer or `null` if unavailable
     */
    val accelerometerAccuracy: Accuracy? = null,

    /**
     * Accuracy of the magnetometer or `null` if unavailable
     */
    val magnetometerAccuracy: Accuracy? = null
) {
    init {
        require(angle.inDegrees().value in VALID_RANGE) { "$angle is outside of range 0..<360" }
    }

    /**
     * Accuracy of an azimuth
     */
    enum class Accuracy {

        /**
         * Azimuths with this accuracy are not usable and should never be trusted
         */
        UNUSABLE,

        /**
         * Azimuths with this accuracy are unreliable and should rarely be trusted
         */
        UNRELIABLE,

        /**
         * Azimuths with this accuracy are usable but should be treated with caution
         */
        LOW,

        /**
         * Azimuths with this accuracy are usable and can be trusted
         */
        MEDIUM,

        /**
         * Azimuths with this accuracy are very accurate and can be trusted
         */
        HIGH
    }
}

private const val DEGREES_0 = 0.0
private const val DEGREES_360 = 360.0
private val VALID_RANGE = DEGREES_0..<DEGREES_360
