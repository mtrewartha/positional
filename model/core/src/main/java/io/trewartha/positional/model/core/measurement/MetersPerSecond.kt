package io.trewartha.positional.model.core.measurement

/**
 * Speed in meters per second
 */
@JvmInline
value class MetersPerSecond(override val value: Double) : Speed {

    override fun inKilometersPerHour(): KilometersPerHour = KilometersPerHour(value * KPH_PER_MPS)

    override fun inMetersPerSecond(): MetersPerSecond = this

    override fun inMilesPerHour(): MilesPerHour = MilesPerHour(value * MPH_PER_MPS)
}

/**
 * Create a speed in meters per second (m/s) with the magnitude of this value
 */
val Number.mps: MetersPerSecond get() = MetersPerSecond(toDouble())

private const val KPH_PER_MPS = 3.6
private const val MPH_PER_MPS = 2.236936
