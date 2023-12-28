package io.trewartha.positional.data.measurement

import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class SpeedTest {

    @Test
    fun testConversionFromKilometersPerHourToMetersPerSecond() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.277778f)
    }

    @Test
    fun testConversionFromKilometersPerHourToMilesPerHour() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(0.621371f)
    }

    @Test
    fun testConversionFromKilometersPerHourToKilometersPerHour() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun testConversionFromMetersPerSecondToMetersPerSecond() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun testConversionFromMetersPerSecondToMilesPerHour() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(2.236936f)
    }

    @Test
    fun testConversionFromMetersPerSecondToKilometersPerHour() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(3.6f)
    }

    @Test
    fun testConversionFromMilesPerHourToMetersPerSecond() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.44704f)
    }

    @Test
    fun testConversionFromMilesPerHourToMilesPerHour() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun testConversionFromMilesPerHourToKilometersPerHour() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1.60934f)
    }
}
