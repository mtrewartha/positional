package io.trewartha.positional.model.core.measurement

/**
 * Distance abstraction
 */
sealed interface Distance {

    /**
     * Magnitude of the distance
     */
    val value: Double

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
    value class Feet(override val value: Double) : Distance {
        override fun inFeet() = this
        override fun inMeters() = Meters(value * METERS_PER_FOOT)
        override fun toString(): String = "${value}ft"
    }

    /**
     * Distance in meters
     */
    @JvmInline
    value class Meters(override val value: Double) : Distance {
        override fun inFeet(): Feet = Feet(value * FEET_PER_METER)
        override fun inMeters(): Meters = this
        override fun toString(): String = "${value}m"
    }
}

/**
 * Create a distance in meters with the magnitude of this value
 */
val Double.meters: Distance.Meters get() = Distance.Meters(this)

/**
 * Create a distance in meters with the magnitude of this value
 */
val Float.meters: Distance.Meters get() = Distance.Meters(toDouble())

/**
 * Create a distance in feet with the magnitude of this value
 */
val Int.feet: Distance.Feet get() = Distance.Feet(toDouble())

/**
 * Create a distance in meters with the magnitude of this value
 */
val Int.meters: Distance.Meters get() = Distance.Meters(toDouble())

private const val FEET_PER_METER = 3.28084
private const val METERS_PER_FOOT = 0.3048
