package io.trewartha.positional.core.measurement

/**
 * Space (measured in a given unit) between two intersecting lines or surfaces at or close to the
 * point where they meet
 *
 * @property magnitude Magnitude/value/size of the angle
 * @property unit Unit the angle is measured in
 */
data class Angle(val magnitude: Double, val unit: Unit) {

    /**
     * Convert this angle to degrees if it is not already in degrees
     */
    fun inDegrees(): Angle = this

    /**
     * Add another angle to this angle
     *
     * @param other Angle to add to this angle
     *
     * @return Angle whose magnitude is the sum of the original angle's magnitude and the given
     * angle's magnitude converted to the same unit as this angle. The result has the same unit as
     * this angle.
     */
    operator fun plus(other: Angle): Angle = (magnitude + other.inDegrees().magnitude).degrees

    override fun toString(): String = when (unit) {
        Unit.DEGREES -> FORMAT_DEGREES.format(magnitude)
    }

    /**
     * Units that an angle can be measured in
     */
    enum class Unit {

        /**
         * Degrees
         */
        DEGREES {
            override val format = FORMAT_DEGREES
        };

        /**
         * Java format string that can be used to convert a distance in the unit to a string
         */
        abstract val format: String
    }
}

/**
 * Create an angle in degrees with the magnitude of this value
 */
inline val Number.degrees: Angle get() = Angle(toDouble(), Angle.Unit.DEGREES)

private const val FORMAT_DEGREES = "%,f°"
