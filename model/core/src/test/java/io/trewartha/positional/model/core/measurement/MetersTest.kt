package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class MetersTest {

    @Test
    fun `Conversion from meters to feet returns correct distance in feet`() {
        val meters = 1.meters

        val result = meters.inFeet()

        result.shouldBeInstanceOf<Feet>()
        result.value.shouldBeExactly(3.28084)
    }

    @Test
    fun `Conversion from meters to meters returns original meters`() {
        val meters = 1.meters

        val result = meters.inMeters()

        result.shouldBeInstanceOf<Meters>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from meters to a string appends 'm' to the value`() {
        val meters = 1.23.meters

        val result = meters.toString()

        result.shouldBe("1.23m")
    }

    @Test
    fun `Distances in meters can be created with extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.meters.shouldBe(Meters(number.toDouble()))
        }
    }
}
