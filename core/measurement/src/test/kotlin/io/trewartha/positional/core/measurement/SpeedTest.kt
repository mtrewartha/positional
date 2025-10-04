package io.trewartha.positional.core.measurement

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlin.test.Test

class SpeedTest {

    @Test
    fun `Speeds in kilometers per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.kph.shouldBe(Speed(number.toDouble(), Speed.Unit.KILOMETERS_PER_HOUR))
        }
    }

    @Test
    fun `Speeds in meters per second can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mps.shouldBe(Speed(number.toDouble(), Speed.Unit.METERS_PER_SECOND))
        }
    }

    @Test
    fun `Speeds in miles per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mph.shouldBe(Speed(number.toDouble(), Speed.Unit.MILES_PER_HOUR))
        }
    }

    @Test
    fun `Conversion from kilometers per hour to meters per second returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMetersPerSecond()

        result.shouldBe(Speed(0.277778, Speed.Unit.METERS_PER_SECOND))
    }

    @Test
    fun `Conversion from kilometers per hour to miles per hour returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMilesPerHour()

        result.shouldBe(Speed(0.621371, Speed.Unit.MILES_PER_HOUR))
    }

    @Test
    fun `Conversion from kilometers per hour to kilometers per hour returns original value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inKilometersPerHour()

        result.shouldBe(Speed(1.0, Speed.Unit.KILOMETERS_PER_HOUR))
    }

    @Test
    fun `Conversion from kilometers per hour to string uses standard unit name`() {
        val kilometersPerHour = 1234.56.kph

        val result = kilometersPerHour.toString()

        result.shouldEndWith(" km/h")
    }

    @Test
    fun `Conversion from meters per second to meters per second returns original value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMetersPerSecond()

        result.shouldBe(Speed(1.0, Speed.Unit.METERS_PER_SECOND))
    }

    @Test
    fun `Conversion from meters per second to miles per hour returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMilesPerHour()

        result.shouldBe(Speed(2.236936, Speed.Unit.MILES_PER_HOUR))
    }

    @Test
    fun `Conversion from meters per second to kilometers per hour returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inKilometersPerHour()

        result.shouldBe(Speed(3.6, Speed.Unit.KILOMETERS_PER_HOUR))
    }

    @Test
    fun `Conversion from meters per second to string uses standard unit name`() {
        val metersPerSecond = 1234.56.mps

        val result = metersPerSecond.toString()

        result.shouldEndWith(" m/s")
    }

    @Test
    fun `Conversion from miles per hour to meters per second returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMetersPerSecond()

        result.shouldBe(Speed(0.44704, Speed.Unit.METERS_PER_SECOND))
    }

    @Test
    fun `Conversion from miles per hour to miles per hour returns original value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMilesPerHour()

        result.shouldBe(Speed(1.0, Speed.Unit.MILES_PER_HOUR))
    }

    @Test
    fun `Conversion from miles per hour to kilometers per hour returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inKilometersPerHour()

        result.shouldBe(Speed(1.60934, Speed.Unit.KILOMETERS_PER_HOUR))
    }

    @Test
    fun `Conversion from miles per hour to string uses standard unit name`() {
        val milesPerHour = 1234.56.mph

        val result = milesPerHour.toString()

        result.shouldEndWith(" mi/h")
    }
}
