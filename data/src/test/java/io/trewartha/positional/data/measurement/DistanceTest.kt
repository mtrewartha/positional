package io.trewartha.positional.data.measurement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DistanceTest {

    @Test
    fun testConversionFromFeetToFeet() {
        val feet = Distance.Feet(1f)

        val result = feet.inFeet()

        assertIs<Distance.Feet>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromFeetToMeters() {
        val feet = Distance.Feet(1f)

        val result = feet.inMeters()

        assertIs<Distance.Meters>(result)
        assertEquals(expected = 0.3048f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMetersToFeet() {
        val meters = Distance.Meters(1f)

        val result = meters.inFeet()

        assertIs<Distance.Feet>(result)
        assertEquals(expected = 3.28084f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMetersToMeters() {
        val meters = Distance.Meters(1f)

        val result = meters.inMeters()

        assertIs<Distance.Meters>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }
}

private const val TOLERANCE = 0.001f
