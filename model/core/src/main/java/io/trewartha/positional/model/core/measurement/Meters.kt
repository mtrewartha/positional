package io.trewartha.positional.model.core.measurement

/**
 * Distance in meters
 */
@JvmInline
value class Meters(override val value: Double) : Distance {

    override fun inFeet(): Feet = Feet(value * FT_PER_M)

    override fun inMeters(): Meters = this

    override fun toString(): String = "${value}m"
}

/**
 * Create a distance in meters with the magnitude of this value
 */
val Number.meters: Meters get() = Meters(toDouble())

private const val FT_PER_M = 3.28084
