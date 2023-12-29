package io.trewartha.positional.data.measurement

/**
 * Distance abstraction
 */
sealed interface Distance {

    /**
     * Magnitude of the distance
     */
    val value: Float

    /**
     * Converts this distance to an equivalent distance in meters
     */
    fun inMeters(): Meters

    /**
     * Converts this distance to an equivalent distance in feet
     */
    fun inFeet(): Feet

    /**
     * Distance in feet
     */
    @JvmInline
    value class Feet(override val value: Float) : Distance {
        override fun inFeet() = this
        override fun inMeters() = Meters(value * METERS_PER_FOOT)
        override fun toString(): String = "${value}ft"
    }

    /**
     * Distance in meters
     */
    @JvmInline
    value class Meters(override val value: Float) : Distance {
        override fun inFeet(): Feet = Feet(value * FEET_PER_METER)
        override fun inMeters(): Meters = this
        override fun toString(): String = "${value}m"
    }
}

const val FEET_PER_METER = 3.28084f
const val METERS_PER_FOOT = 0.3048f
