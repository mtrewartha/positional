package io.trewartha.positional.ui.location.format

import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Distance
import io.trewartha.positional.model.core.measurement.MgrsCoordinates
import io.trewartha.positional.model.core.measurement.meters
import org.junit.Before
import org.junit.Test
import kotlin.math.roundToInt

class MgrsFormatterTest {

    private val coordinates = MgrsCoordinates("31N", "AA", 12345.6.meters, 65432.1.meters)

    private lateinit var subject: MgrsFormatter

    @Before
    fun setUp() {
        subject = MgrsFormatter()
    }

    @Test
    fun `Formatting for display returns the zone, band, and 100km square ID in the first line`() {
        val result = subject.formatForDisplay(coordinates)

        with(coordinates) { result[0].shouldBe("${gridZoneDesignator} $gridSquareID") }
    }

    @Test
    fun `Formatting for display returns the easting on the second line`() {
        val result = subject.formatForDisplay(coordinates)

        val mgrsCoordinates = coordinates.asMgrsCoordinates()
        val roundedAndPaddedEasting = mgrsCoordinates.easting.inRoundedAndPaddedMeters()
        result[1].shouldBe(roundedAndPaddedEasting)
    }

    @Test
    fun `Formatting for display returns the northing on the third line`() {
        val result = subject.formatForDisplay(coordinates)

        val mgrsCoordinates = coordinates.asMgrsCoordinates()
        val roundedAndPaddedNorthing = mgrsCoordinates.northing.inRoundedAndPaddedMeters()
        result[2].shouldBe(roundedAndPaddedNorthing)
    }

    @Test
    fun `Formatting for copy returns the GZD, 100km square ID, and rounded easting and northing`() {
        val result = subject.formatForCopy(coordinates)

        with(coordinates.asMgrsCoordinates()) {
            val roundedAndPaddedEasting = easting.inRoundedAndPaddedMeters()
            val roundedAndPaddedNorthing = northing.inRoundedAndPaddedMeters()
            result.shouldBe("$gridZoneDesignator$gridSquareID$roundedAndPaddedEasting$roundedAndPaddedNorthing")
        }
    }
}

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString().padStart(
        MgrsCoordinates.EASTING_NORTHING_LENGTH,
        MgrsCoordinates.EASTING_NORTHING_PAD_CHAR
    )

