package io.trewartha.positional.model.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DegreesTest {

    @Test
    fun `IllegalArgumentException thrown when value is infinite`() {
        shouldThrow<IllegalArgumentException> { Double.POSITIVE_INFINITY.degrees }
        shouldThrow<IllegalArgumentException> { Double.NEGATIVE_INFINITY.degrees }
    }

    @Test
    fun `IllegalArgumentException thrown when value is NaN`() {
        shouldThrow<IllegalArgumentException> { Double.NaN.degrees }
    }

    @Test
    fun `Conversion from degrees to degrees returns the original degrees`() {
        val originalDegrees = 1.degrees

        val result = originalDegrees.inDegrees()

        result.shouldBe(originalDegrees)
    }

    @Test
    fun `Addition of two degrees sums them`() {
        val result = 1.degrees + 2.degrees

        result.shouldBe(3.degrees)
    }

    @Test
    fun `Addition of two degrees wraps properly after 360`() {
        val result = 359.degrees + 2.degrees

        result.shouldBe(1.degrees)
    }

    @Test
    fun `Conversion to string returns the degrees with the symbol`() {
        val angle = 1.degrees

        val result = angle.toString()

        result.shouldBe("1.0°")
    }

    @Test
    fun `Degrees can be created from extension properties`() {
        for (number in setOf<Number>(1, 1.23f, 1.23)) {
            number.degrees.shouldBe(Degrees(number.toDouble()))
        }
    }
}
