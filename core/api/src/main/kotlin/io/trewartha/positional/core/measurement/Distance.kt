package io.trewartha.positional.core.measurement

/**
 * Amount of space between two things
 *
 * @property magnitude Magnitude/amount/value of the distance
 * @property unit Unit that [magnitude] is/was measured in
 */
data class Distance(val magnitude: Double, val unit: Unit) {

    /**
     * `true` if the distance is finite, `false` if it is negative or positive infinity
     */
    val isFinite: Boolean get() = magnitude.isFinite()

    /**
     * `true` if the distance is negative, `false` if it is positive or zero
     */
    val isNegative: Boolean get() = magnitude < 0.0

    /**
     * `true` if the distance is positive, `false` if it is negative or zero
     */
    val isPositive: Boolean get() = magnitude > 0.0

    /**
     * Convert this distance to an equivalent distance in meters
     */
    fun inMeters(): Distance = when (unit) {
        Unit.FEET -> Distance(magnitude * M_PER_FT, Unit.METERS)
        Unit.METERS -> this
    }

    /**
     * Convert this distance to an equivalent distance in feet
     */
    fun inFeet(): Distance = when (unit) {
        Unit.FEET -> this
        Unit.METERS -> Distance(magnitude * FT_PER_M, Unit.FEET)
    }

    override fun toString(): String = unit.format.format(magnitude)

    /**
     * Units that a distance can be measured in
     */
    enum class Unit {

        /**
         * Feet
         */
        FEET {
            override val format = FORMAT_FEET
        },

        /**
         * Meters
         */
        METERS {
            override val format = FORMAT_METERS
        };

        /**
         * Java format string that can be used to convert a distance in the unit to a string
         */
        abstract val format: String
    }
}

/**
 * Create a distance in feet with the magnitude of this value
 */
val Number.feet: Distance get() = Distance(toDouble(), Distance.Unit.FEET)

/**
 * Create a distance in meters with the magnitude of this value
 */
val Number.meters: Distance get() = Distance(toDouble(), Distance.Unit.METERS)

private const val FORMAT_FEET = "%,f ft"
private const val FORMAT_METERS = "%,f m"

private const val FT_PER_M = 3.28084
private const val M_PER_FT = 0.3048
