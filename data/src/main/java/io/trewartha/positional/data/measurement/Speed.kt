package io.trewartha.positional.data.measurement

sealed interface Speed {

    val value: Float

    fun inKilometersPerHour(): KilometersPerHour
    fun inMetersPerSecond(): MetersPerSecond
    fun inMilesPerHour(): MilesPerHour

    @JvmInline
    value class KilometersPerHour(override val value: Float) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour = this

        override fun inMetersPerSecond(): MetersPerSecond =
            MetersPerSecond(value * METERS_PER_SECOND_PER_KILOMETER_PER_HOUR)

        override fun inMilesPerHour(): MilesPerHour =
            MilesPerHour(value * MILES_PER_KILOMETER)
    }

    @JvmInline
    value class MetersPerSecond(override val value: Float) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour =
            KilometersPerHour(value * KILOMETERS_PER_HOUR_PER_METER_PER_SECOND)

        override fun inMetersPerSecond(): MetersPerSecond = this

        override fun inMilesPerHour(): MilesPerHour =
            MilesPerHour(value * MILES_PER_HOUR_PER_METER_PER_SECOND)
    }

    @JvmInline
    value class MilesPerHour(override val value: Float) : Speed {

        override fun inKilometersPerHour(): KilometersPerHour =
            KilometersPerHour(value * KILOMETERS_PER_MILE)

        override fun inMetersPerSecond(): MetersPerSecond =
            MetersPerSecond(value * METERS_PER_SECOND_PER_MILE_PER_HOUR)

        override fun inMilesPerHour(): MilesPerHour = this
    }
}

const val METERS_PER_SECOND_PER_MILE_PER_HOUR = 0.44704f
const val METERS_PER_SECOND_PER_KILOMETER_PER_HOUR = 0.277778f
const val MILES_PER_KILOMETER = 0.621371f
const val KILOMETERS_PER_MILE = 1.60934f
const val MILES_PER_HOUR_PER_METER_PER_SECOND = 2.236936f
const val KILOMETERS_PER_HOUR_PER_METER_PER_SECOND = 3.6f
