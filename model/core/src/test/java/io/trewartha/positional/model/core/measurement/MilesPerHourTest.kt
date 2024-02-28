package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class MilesPerHourTest {

    @Test
    fun `Conversion from MPH to M per S returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMetersPerSecond()

        result.shouldBeInstanceOf<MetersPerSecond>()
        result.value.shouldBeExactly(0.44704)
    }

    @Test
    fun `Conversion from MPH to MPH returns original value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inMilesPerHour()

        result.shouldBeInstanceOf<MilesPerHour>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from MPH to KPH returns converted value`() {
        val milesPerHour = 1.mph

        val result = milesPerHour.inKilometersPerHour()

        result.shouldBeInstanceOf<KilometersPerHour>()
        result.value.shouldBeExactly(1.60934)
    }

    @Test
    fun `Speeds in miles per hour can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mph.shouldBe(MilesPerHour(number.toDouble()))
        }
    }
}
