package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class MetersPerSecondTest {

    @Test
    fun `Conversion from M per S to M per S returns original value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMetersPerSecond()

        result.shouldBeInstanceOf<MetersPerSecond>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from M per S to MPH returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inMilesPerHour()

        result.shouldBeInstanceOf<MilesPerHour>()
        result.value.shouldBeExactly(2.236936)
    }

    @Test
    fun `Conversion from M per S to KPH returns converted value`() {
        val metersPerSecond = 1.mps

        val result = metersPerSecond.inKilometersPerHour()

        result.shouldBeInstanceOf<KilometersPerHour>()
        result.value.shouldBeExactly(3.6)
    }

    @Test
    fun `Speeds in meters per second can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.mps.shouldBe(MetersPerSecond(number.toDouble()))
        }
    }
}
