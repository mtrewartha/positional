package io.trewartha.positional.model.core.measurement

/**
 * Speed in miles per hour
 */
@JvmInline
value class MilesPerHour(override val value: Double) : Speed {

    override fun inKilometersPerHour(): KilometersPerHour = KilometersPerHour(value * KM_PER_MI)

    override fun inMetersPerSecond(): MetersPerSecond = MetersPerSecond(value * MPS_PER_MPH)

    override fun inMilesPerHour(): MilesPerHour = this
}

/**
 * Create a speed in miles per hour (MPH) with the magnitude of this value
 */
val Number.mph: MilesPerHour get() = MilesPerHour(toDouble())

private const val KM_PER_MI = 1.60934
private const val MPS_PER_MPH = 0.44704
