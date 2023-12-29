package io.trewartha.positional.data.measurement

/**
 * Speed abstraction
 */
sealed interface Speed {

    /**
     * Magnitude of the speed
     */
    val value: Float

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
    value class KilometersPerHour(override val value: Float) : Speed {

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
    value class MetersPerSecond(override val value: Float) : Speed {

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
    value class MilesPerHour(override val value: Float) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour =
            KilometersPerHour(value * KILOMETERS_PER_MILE)

        override fun inMetersPerSecond(): MetersPerSecond =
            MetersPerSecond(value * METERS_PER_SECOND_PER_MILE_PER_HOUR)

        override fun inMilesPerHour(): MilesPerHour = this
    }
}

private const val METERS_PER_SECOND_PER_MILE_PER_HOUR = 0.44704f
private const val METERS_PER_SECOND_PER_KILOMETER_PER_HOUR = 0.277778f
private const val MILES_PER_KILOMETER = 0.621371f
private const val KILOMETERS_PER_MILE = 1.60934f
private const val MILES_PER_HOUR_PER_METER_PER_SECOND = 2.236936f
private const val KILOMETERS_PER_HOUR_PER_METER_PER_SECOND = 3.6f
