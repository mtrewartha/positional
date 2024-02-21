package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class SpeedTest {

    @Test
    fun `Conversion from KPH to M per S returns converted value`() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.277778f)
    }

    @Test
    fun `Conversion from KPH to MPH returns converted value`() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(0.621371f)
    }

    @Test
    fun `Conversion from KPH to KPH returns original value`() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun `Conversion from M per S to M per S returns original value`() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun `Conversion from M per S to MPH returns converted value`() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(2.236936f)
    }

    @Test
    fun `Conversion from M per S to KPH returns converted value`() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(3.6f)
    }

    @Test
    fun `Conversion from MPH to M per S returns converted value`() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<Speed.MetersPerSecond>()
        result.value.shouldBeExactly(0.44704f)
    }

    @Test
    fun `Conversion from MPH to MPH returns original value`() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<Speed.MilesPerHour>()
        result.value.shouldBeExactly(1f)
    }

    @Test
    fun `Conversion from MPH to KPH returns converted value`() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<Speed.KilometersPerHour>()
        result.value.shouldBeExactly(1.60934f)
    }
}
