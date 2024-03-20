package io.trewartha.positional.model.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UtmCoordinatesTest {

    @Test
    fun `Creation of coordinates with an invalid zone throws an exception`() {
        for (zone in listOf(-1, 61)) {
            shouldThrow<IllegalArgumentException> {
                UtmCoordinates(zone, Hemisphere.NORTH, 0.meters, 0.meters)
            }
        }
    }

    @Test
    fun `Creation of coordinates with a non-finite easting throws an exception`() {
        for (easting in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
            shouldThrow<IllegalArgumentException> {
                UtmCoordinates(1, Hemisphere.NORTH, easting.meters, 0.meters)
            }
        }
    }

    @Test
    fun `Creation of coordinates with a negative easting throws an exception`() {
        shouldThrow<IllegalArgumentException> {
            UtmCoordinates(1, Hemisphere.NORTH, (-1).meters, 0.meters)
        }
    }

    @Test
    fun `Creation of coordinates with a non-finite northing throws an exception`() {
        for (northing in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
            shouldThrow<IllegalArgumentException> {
                UtmCoordinates(1, Hemisphere.NORTH, 0.meters, northing.meters)
            }
        }
    }

    @Test
    fun `Creation of coordinates with a negative northing throws an exception`() {
        shouldThrow<IllegalArgumentException> {
            UtmCoordinates(1, Hemisphere.NORTH, 0.meters, (-1).meters)
        }
    }

    @Test
    fun `Conversion to string returns the latitude band, hemisphere, easting and northing`() {
        val coordinates = UtmCoordinates(1, Hemisphere.NORTH, 12345.6789.meters, 987654.321.meters)

        val result = coordinates.toString()

        result.shouldBe("1N 0012346m E 0987654m N")
    }
}
