package io.trewartha.positional.data.measurement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AngleTest {

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsLessThanZero() {
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(-0.0001f) }
    }

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsEqualTo360() {
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(360f) }
    }

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsGreaterThan360() {
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(360.0001f) }
    }

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsInfinite() {
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(Float.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(Float.NEGATIVE_INFINITY) }
    }

    @Test
    fun testIllegalArgumentExceptionThrownWhenValueIsNaN() {
        assertFailsWith<IllegalArgumentException> { Angle.Degrees(Float.NaN) }
    }

    @Test
    fun testConversionFromDegreesToDegreesIsAnIdentityFunction() {
        val originalDegrees = Angle.Degrees(1f)

        val result = originalDegrees.inDegrees()

        assertEquals(expected = originalDegrees, actual = result)
    }

    @Test
    fun testAdditionWithoutWrapping() {
        val result = Angle.Degrees(1f) + Angle.Degrees(2f)

        assertEquals(expected = Angle.Degrees(3f), actual = result)
    }

    @Test
    fun testAdditionWithWrapping() {
        val result = Angle.Degrees(359f) + Angle.Degrees(2f)

        assertEquals(expected = Angle.Degrees(1f), actual = result)
    }
}
