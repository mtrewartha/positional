package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class KilometersPerHourTest {

    @Test
    fun `Conversion from KPH to M per S returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<MetersPerSecond>()
        result.value.shouldBeExactly(0.277778)
    }

    @Test
    fun `Conversion from KPH to MPH returns converted value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<MilesPerHour>()
        result.value.shouldBeExactly(0.621371)
    }

    @Test
    fun `Conversion from KPH to KPH returns original value`() {
        val kilometersPerHour = 1.kph

        val result = kilometersPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<KilometersPerHour>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Speeds in kilometers per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.kph.shouldBe(KilometersPerHour(number.toDouble()))
        }
    }
}
