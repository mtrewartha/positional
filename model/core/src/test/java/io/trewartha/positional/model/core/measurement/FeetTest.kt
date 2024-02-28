package io.trewartha.positional.model.core.measurement

import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class FeetTest {

    @Test
    fun `Conversion from feet to feet returns original feet`() {
        val feet = 1.feet

        val result = feet.inFeet()

        result.shouldBeInstanceOf<Feet>()
        result.value.shouldBeExactly(1.0)
    }

    @Test
    fun `Conversion from feet to meters returns correct distance in meters`() {
        val feet = 1.feet

        val result = feet.inMeters()

        result.shouldBeInstanceOf<Meters>()
        result.value.shouldBeExactly(0.3048)
    }

    @Test
    fun `Conversion from feet to a string appends 'ft' to the value`() {
        val feet = 1.23.feet

        val result = feet.toString()

        result.shouldBe("1.23ft")
    }

    @Test
    fun `Distances in feet can be created with extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.feet.shouldBe(Feet(number.toDouble()))
        }
    }
}
