package io.trewartha.positional.core.measurement

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import kotlin.test.Test

class AngleTest {

    @Test
    fun `Angles in degrees can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.degrees.shouldBe(Angle(number.toDouble(), Angle.Unit.DEGREES))
        }
    }

    @Test
    fun `Conversion from degrees to degrees returns the original degrees`() {
        val originalDegrees = 1.degrees

        val result = originalDegrees.inDegrees()

        result.shouldBe(originalDegrees)
    }

    @Test
    fun `Conversion from degrees to string appends degree symbol to the magnitude`() {
        val degrees = 1234.56.degrees

        val result = degrees.toString()

        result.shouldNotContain(Regex("\\s")).shouldEndWith("°")
    }

    @Test
    fun `Addition of two angles sums their magnitudes in the same unit as the first one`() {
        val result = 1.degrees + 2.degrees

        result.shouldBe(3.degrees)
    }
}
