package io.trewartha.positional.model.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt
import kotlin.test.Test

class MgrsCoordinatesTest {

    @Test
    fun `Creation of coordinates with an invalid grid zone designator throws an exception`() {
        shouldThrow<IllegalArgumentException> { MgrsCoordinates("", "AA", 0.meters, 0.meters) }
    }

    @Test
    fun `Creation of coordinates with an invalid grid square ID throws an exception`() {
        shouldThrow<IllegalArgumentException> { MgrsCoordinates("1C", "A", 0.meters, 0.meters) }
    }

    @Test
    fun `Creation of coordinates with a non-finite easting throws an exception`() {
        for (easting in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
            shouldThrow<IllegalArgumentException> {
                MgrsCoordinates("1C", "AA", easting.meters, 0.meters)
            }
        }
    }

    @Test
    fun `Creation of coordinates with a negative easting throws an exception`() {
        shouldThrow<IllegalArgumentException> { MgrsCoordinates("1C", "AA", (-1).meters, 0.meters) }
    }

    @Test
    fun `Creation of coordinates with a non-finite northing throws an exception`() {
        for (northing in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
            shouldThrow<IllegalArgumentException> {
                MgrsCoordinates("1C", "AA", 0.meters, northing.meters)
            }
        }
    }

    @Test
    fun `Creation of coordinates with a negative northing throws an exception`() {
        shouldThrow<IllegalArgumentException> { MgrsCoordinates("1C", "AA", 0.meters, (-1).meters) }
    }

    @Test
    fun `Conversion to string returns the GZD, 100 km square ID, easting and northing`() {
        val coordinates = MgrsCoordinates("31N", "AA", 12345.6.meters, 65432.1.meters)

        val result = coordinates.toString()

        with(coordinates) {
            val roundedAndPaddedEasting = easting.inRoundedAndPaddedMeters()
            val roundedAndPaddedNorthing = northing.inRoundedAndPaddedMeters()
            result.shouldBe("$gridZoneDesignator$gridSquareID$roundedAndPaddedEasting$roundedAndPaddedNorthing")
        }
    }
}

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString()
        .padStart(
            MgrsCoordinates.EASTING_NORTHING_LENGTH,
            MgrsCoordinates.EASTING_NORTHING_PAD_CHAR
        )
