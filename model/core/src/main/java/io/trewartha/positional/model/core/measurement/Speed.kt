package io.trewartha.positional.model.core.measurement

/**
 * Speed abstraction
 */
sealed interface Speed {

    /**
     * Magnitude of the speed
     */
    val value: Double

    /**
     * Converts this speed to an equivalent speed in kilometers per hour
     */
    fun inKilometersPerHour(): KilometersPerHour

    /**
     * Converts this speed to an equivalent speed in meters per second
     */
    fun inMetersPerSecond(): MetersPerSecond

    /**
     * Converts this speed to an equivalent speed in miles per hour
     */
    fun inMilesPerHour(): MilesPerHour

    /**
     * Speed in kilometers per hour
     */
    @JvmInline
    value class KilometersPerHour(override val value: Double) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour = this

        override fun inMetersPerSecond(): MetersPerSecond =
            MetersPerSecond(value * METERS_PER_SECOND_PER_KILOMETER_PER_HOUR)

        override fun inMilesPerHour(): MilesPerHour =
            MilesPerHour(value * MILES_PER_KILOMETER)
    }

    /**
     * Speed in meters per second
     */
    @JvmInline
    value class MetersPerSecond(override val value: Double) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour =
            KilometersPerHour(value * KILOMETERS_PER_HOUR_PER_METER_PER_SECOND)

        override fun inMetersPerSecond(): MetersPerSecond = this

        override fun inMilesPerHour(): MilesPerHour =
            MilesPerHour(value * MILES_PER_HOUR_PER_METER_PER_SECOND)
    }

    /**
     * Speed in miles per hour
     */
    @JvmInline
    value class MilesPerHour(override val value: Double) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour =
            KilometersPerHour(value * KILOMETERS_PER_MILE)

        override fun inMetersPerSecond(): MetersPerSecond =
            MetersPerSecond(value * METERS_PER_SECOND_PER_MILE_PER_HOUR)

        override fun inMilesPerHour(): MilesPerHour = this
    }
}

/**
 * Create a speed in kilometers per hour (KPH) with the magnitude of this value
 */
val Double.kph: Speed.KilometersPerHour get() = Speed.KilometersPerHour(this)

/**
 * Create a speed in miles per hour (MPH) with the magnitude of this value
 */
val Double.mph: Speed.MilesPerHour get() = Speed.MilesPerHour(this)

/**
 * Create a speed in meters per second (m/s) with the magnitude of this value
 */
val Double.mps: Speed.MetersPerSecond get() = Speed.MetersPerSecond(this)

/**
 * Create a speed in kilometers per hour (KPH) with the magnitude of this value
 */
val Float.kph: Speed.KilometersPerHour get() = Speed.KilometersPerHour(toDouble())

/**
 * Create a speed in miles per hour (MPH) with the magnitude of this value
 */
val Float.mph: Speed.MilesPerHour get() = Speed.MilesPerHour(toDouble())

/**
 * Create a speed in meters per second (m/s) with the magnitude of this value
 */
val Float.mps: Speed.MetersPerSecond get() = Speed.MetersPerSecond(toDouble())

/**
 * Create a speed in kilometers per hour (KPH) with the magnitude of this value
 */
val Int.kph: Speed.KilometersPerHour get() = Speed.KilometersPerHour(toDouble())

/**
 * Create a speed in miles per hour (MPH) with the magnitude of this value
 */
val Int.mph: Speed.MilesPerHour get() = Speed.MilesPerHour(toDouble())

/**
 * Create a speed in meters per second (m/s) with the magnitude of this value
 */
val Int.mps: Speed.MetersPerSecond get() = Speed.MetersPerSecond(toDouble())

private const val METERS_PER_SECOND_PER_MILE_PER_HOUR = 0.44704
private const val METERS_PER_SECOND_PER_KILOMETER_PER_HOUR = 0.277778
private const val MILES_PER_KILOMETER = 0.621371
private const val KILOMETERS_PER_MILE = 1.60934
private const val MILES_PER_HOUR_PER_METER_PER_SECOND = 2.236936
private const val KILOMETERS_PER_HOUR_PER_METER_PER_SECOND = 3.6
