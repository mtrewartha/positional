package io.trewartha.positional.data.measurement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SpeedTest {

    @Test
    fun testConversionFromKilometersPerHourToMetersPerSecond() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMetersPerSecond()

        assertIs<Speed.MetersPerSecond>(result)
        assertEquals(expected = 0.277778f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromKilometersPerHourToMilesPerHour() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inMilesPerHour()

        assertIs<Speed.MilesPerHour>(result)
        assertEquals(expected = 0.621371f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromKilometersPerHourToKilometersPerHour() {
        val kilometersPerHour = Speed.KilometersPerHour(1f)

        val result = kilometersPerHour.inKilometersPerHour()

        assertIs<Speed.KilometersPerHour>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMetersPerSecondToMetersPerSecond() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMetersPerSecond()

        assertIs<Speed.MetersPerSecond>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMetersPerSecondToMilesPerHour() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inMilesPerHour()

        assertIs<Speed.MilesPerHour>(result)
        assertEquals(expected = 2.23694f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMetersPerSecondToKilometersPerHour() {
        val metersPerSecond = Speed.MetersPerSecond(1f)

        val result = metersPerSecond.inKilometersPerHour()

        assertIs<Speed.KilometersPerHour>(result)
        assertEquals(expected = 3.6f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMilesPerHourToMetersPerSecond() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMetersPerSecond()

        assertIs<Speed.MetersPerSecond>(result)
        assertEquals(expected = 0.44704f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMilesPerHourToMilesPerHour() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inMilesPerHour()

        assertIs<Speed.MilesPerHour>(result)
        assertEquals(expected = 1f, actual = result.value, absoluteTolerance = TOLERANCE)
    }

    @Test
    fun testConversionFromMilesPerHourToKilometersPerHour() {
        val milesPerHour = Speed.MilesPerHour(1f)

        val result = milesPerHour.inKilometersPerHour()

        assertIs<Speed.KilometersPerHour>(result)
        assertEquals(expected = 1.60934f, actual = result.value, absoluteTolerance = TOLERANCE)
    }
}

private const val TOLERANCE = 0.001f
