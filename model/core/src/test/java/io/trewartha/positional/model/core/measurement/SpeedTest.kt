package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class SpeedTest {

    @Test
    fun `Conversion from KPH to M per S returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.277778)
    }

    @Test
    fun `Conversion from KPH to MPH returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(0.621371)
    }

    @Test
    fun `Conversion from KPH to KPH returns original value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from M per S to M per S returns original value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from M per S to MPH returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(2.236936)
    }

    @Test
    fun `Conversion from M per S to KPH returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(3.6)
    }

    @Test
    fun `Conversion from MPH to M per S returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.44704)
    }

    @Test
    fun `Conversion from MPH to MPH returns original value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from MPH to KPH returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1.60934)
    }

    @Test
    fun `Speeds in kilometers per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.kph.shouldBe(Speed.KilometersPerHour(number.toDouble()))
        }
    }

    @Test
    fun `Speeds in miles per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mph.shouldBe(Speed.MilesPerHour(number.toDouble()))
        }
    }

    @Test
    fun `Speeds in meters per second can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mps.shouldBe(Speed.MetersPerSecond(number.toDouble()))
        }
    }
}
