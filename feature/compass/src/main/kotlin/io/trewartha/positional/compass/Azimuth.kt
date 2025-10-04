package io.trewartha.positional.compass

import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.degrees

/**
 * Azimuth angle and accuracies associated with it
 *
 * @throws IllegalArgumentException if [angle] is outside of the range 0..<360
 */
public data class Azimuth @Throws(IllegalArgumentException::class) constructor(

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
        require(angle.inDegrees().magnitude in VALID_RANGE) { "$angle is outside of range 0..<360" }
    }

    /**
     * Add an angle to the azimuth's angle. If the sum of the azimuth angle and the given angle is
     * greater than 360°, the result will wrap back into the 0..360° range as appropriate. For
     * example, adding two azimuths with angles of 359° and 2° will result in an azimuth with an
     * angle of 1°.
     *
     * @param angle Angle to add to the receiver of this call
     *
     * @return Azimuth whose angle is the wrapped sum of the two azimuths
     */
    public operator fun plus(angle: Angle): Azimuth {
        val degrees = this.angle.inDegrees().magnitude
        val addendDegrees = angle.inDegrees().magnitude
        // Add and mod 360 to wrap the result sums below 0 and above 360 back into the 0..360 range
        val sumDegrees = (((degrees + addendDegrees) + DEGREES_360) % DEGREES_360).degrees
        return Azimuth(sumDegrees)
    }

    /**
     * Accuracy of an azimuth
     */
    public enum class Accuracy {

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
