package io.trewartha.positional.model.core.measurement

/**
 * Distance in feet
 */
@JvmInline
value class Feet(override val value: Double) : Distance {

    override fun inFeet() = this

    override fun inMeters() = Meters(value * M_PER_FT)

    override fun toString(): String = "${value}ft"
}

/**
 * Create a distance in feet with the magnitude of this value
 */
val Number.feet: Feet get() = Feet(toDouble())

private const val M_PER_FT = 0.3048
