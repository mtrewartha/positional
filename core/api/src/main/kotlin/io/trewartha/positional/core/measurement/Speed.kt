package io.trewartha.positional.core.measurement

/**
 * Rate at which someone or something moves
 *
 * @property magnitude Magnitude/amount/value of the speed
 * @property unit Unit that [magnitude] is/was measured in
 */
data class Speed(val magnitude: Double, val unit: Unit) {

    /**
     * Convert this speed to an equivalent speed in kilometers per hour
     */
    fun inKilometersPerHour(): Speed = when (unit) {
        Unit.KILOMETERS_PER_HOUR -> this
        Unit.METERS_PER_SECOND -> Speed(magnitude * KPH_PER_MPS, Unit.KILOMETERS_PER_HOUR)
        Unit.MILES_PER_HOUR -> Speed(magnitude * KM_PER_MI, Unit.KILOMETERS_PER_HOUR)
    }

    /**
     * Convert this speed to an equivalent speed in meters per second
     */
    fun inMetersPerSecond(): Speed = when (unit) {
        Unit.KILOMETERS_PER_HOUR -> Speed(magnitude * MPS_PER_KPH, Unit.METERS_PER_SECOND)
        Unit.METERS_PER_SECOND -> this
        Unit.MILES_PER_HOUR -> Speed(magnitude * MPS_PER_MPH, Unit.METERS_PER_SECOND)
    }

    /**
     * Convert this speed to an equivalent speed in miles per hour
     */
    fun inMilesPerHour(): Speed = when (unit) {
        Unit.KILOMETERS_PER_HOUR -> Speed(magnitude * MI_PER_KM, Unit.MILES_PER_HOUR)
        Unit.METERS_PER_SECOND -> Speed(magnitude * MPH_PER_MPS, Unit.MILES_PER_HOUR)
        Unit.MILES_PER_HOUR -> this
    }

    override fun toString(): String = when (unit) {
        Unit.KILOMETERS_PER_HOUR -> FORMAT_KILOMETERS_PER_HOUR.format(magnitude)
        Unit.METERS_PER_SECOND -> FORMAT_METERS_PER_SECOND.format(magnitude)
        Unit.MILES_PER_HOUR -> FORMAT_MILES_PER_HOUR.format(magnitude)
    }

    /**
     * Units that a speed can be measured in
     */
    enum class Unit {

        /**
         * Kilometers per hour
         */
        KILOMETERS_PER_HOUR {
            override val format = FORMAT_KILOMETERS_PER_HOUR
        },

        /**
         * Meters per second
         */
        METERS_PER_SECOND {
            override val format = FORMAT_METERS_PER_SECOND
        },

        /**
         * Miles per hour
         */
        MILES_PER_HOUR {
            override val format = FORMAT_MILES_PER_HOUR
        };

        /**
         * Java format string that can be used to convert a distance in the unit to a string
         */
        abstract val format: String
    }
}

/**
 * Create a speed in kilometers per hour (KPH) with the magnitude of this value
 */
val Number.kph: Speed get() = Speed(toDouble(), Speed.Unit.KILOMETERS_PER_HOUR)

/**
 * Create a speed in meters per second (m/s) with the magnitude of this value
 */
val Number.mps: Speed get() = Speed(toDouble(), Speed.Unit.METERS_PER_SECOND)

/**
 * Create a speed in miles per hour (MPH) with the magnitude of this value
 */
val Number.mph: Speed get() = Speed(toDouble(), Speed.Unit.MILES_PER_HOUR)

private const val FORMAT_KILOMETERS_PER_HOUR = "%,f km/h"
private const val FORMAT_METERS_PER_SECOND = "%,f m/s"
private const val FORMAT_MILES_PER_HOUR = "%,f mi/h"

private const val KM_PER_MI = 1.60934
private const val KPH_PER_MPS = 3.6
private const val MI_PER_KM = 0.621371
private const val MPH_PER_MPS = 2.236936
private const val MPS_PER_KPH = 0.277778
private const val MPS_PER_MPH = 0.44704
