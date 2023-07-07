package io.trewartha.positional.data.measurement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AngleTest {

    @Test
    fun testConversionFromDegreesToDegrees() {
        val degrees = Angle.Degrees(1f)

        val result = degrees.inDegrees()

        assertIs<Angle.Degrees>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }
}

private const val TOLERANCE = 0.001f
