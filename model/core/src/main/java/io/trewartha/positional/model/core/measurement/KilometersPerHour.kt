package io.trewartha.positional.model.core.measurement

/**
 * Speed in kilometers per hour
 */
@JvmInline
value class KilometersPerHour(override val value: Double) : Speed {

    override fun inKilometersPerHour(): KilometersPerHour = this

    override fun inMetersPerSecond(): MetersPerSecond = MetersPerSecond(value * MPS_PER_KPH)

    override fun inMilesPerHour(): MilesPerHour = MilesPerHour(value * MI_PER_KM)
}

/**
 * Create a speed in kilometers per hour (KPH) with the magnitude of this value
 */
val Number.kph: KilometersPerHour get() = KilometersPerHour(toDouble())

private const val MI_PER_KM = 0.621371
private const val MPS_PER_KPH = 0.277778
