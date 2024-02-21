package io.trewartha.positional.model.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AngleTest {

    @Test
    fun `IllegalArgumentException thrown when value is infinite`() {
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.POSITIVE_INFINITY) }
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.NEGATIVE_INFINITY) }
    }

    @Test
    fun `IllegalArgumentException thrown when value is NaN`() {
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.NaN) }
    }

    @Test
    fun `Conversion from degrees to degrees returns the original degrees`() {
        val originalDegrees = Angle.Degrees(1f)

        val result = originalDegrees.inDegrees()

        result.shouldBe(originalDegrees)
    }

    @Test
    fun `Addition of two angles sums the angles`() {
        val result = Angle.Degrees(1f) + Angle.Degrees(2f)

        result.shouldBe(Angle.Degrees(3f))
    }

    @Test
    fun `Addition of two angles wraps properly after 360 degrees`() {
        val result = Angle.Degrees(359f) + Angle.Degrees(2f)

        result.shouldBe(Angle.Degrees(1f))
    }
}
