package io.trewartha.positional.model.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Angle
import kotlin.test.Test

class AngleTest {

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsInfinite() {
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.POSITIVE_INFINITY) }
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.NEGATIVE_INFINITY) }
    }

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsNaN() {
        shouldThrow<IllegalArgumentException> { Angle.Degrees(Float.NaN) }
    }

    @Test
    fun testConversionFromDegreesToDegreesIsAnIdentityFunction() {
        val originalDegrees = Angle.Degrees(1f)

        val result = originalDegrees.inDegrees()

        result.shouldBe(originalDegrees)
    }

    @Test
    fun testAdditionWithoutWrapping() {
        val result = Angle.Degrees(1f) + Angle.Degrees(2f)

        result.shouldBe(Angle.Degrees(3f))
    }

    @Test
    fun testAdditionWithWrapping() {
        val result = Angle.Degrees(359f) + Angle.Degrees(2f)

        result.shouldBe(Angle.Degrees(1f))
    }
}
