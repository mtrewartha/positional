package io.trewartha.positional.ui.location.format

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Distance
import io.trewartha.positional.model.core.measurement.Hemisphere
import io.trewartha.positional.model.core.measurement.UtmCoordinates
import io.trewartha.positional.model.core.measurement.meters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale
import kotlin.math.roundToInt

@RunWith(RobolectricTestRunner::class)
class UtmFormatterTest {

    private val coordinates = UtmCoordinates(1, Hemisphere.NORTH, 123456.7.meters, 765432.1.meters)

    private lateinit var subject: UtmFormatter

    @Before
    fun setUp() {
        subject = UtmFormatter(RuntimeEnvironment.getApplication(), Locale.US)
    }

    @Test
    fun `Formatting for display returns the zone and hemisphere in the first line`() {
        val result = subject.formatForDisplay(coordinates)

        with(coordinates.asUtmCoordinates().shouldNotBeNull()) {
            result[0].shouldBe("${zone}$hemisphere")
        }
    }

    @Test
    fun `Formatting for display returns the easting on the second line`() {
        val result = subject.formatForDisplay(coordinates)

        val mgrsCoordinates = coordinates.asUtmCoordinates().shouldNotBeNull()
        val roundedAndPaddedEasting = mgrsCoordinates.easting.inRoundedAndPaddedMeters()
        result[1].shouldBe("${roundedAndPaddedEasting}m E")
    }

    @Test
    fun `Formatting for display returns the northing on the third line`() {
        val result = subject.formatForDisplay(coordinates)

        val mgrsCoordinates = coordinates.asUtmCoordinates().shouldNotBeNull()
        val roundedAndPaddedNorthing = mgrsCoordinates.northing.inRoundedAndPaddedMeters()
        result[2].shouldBe("${roundedAndPaddedNorthing}m N")
    }

    @Test
    fun `Formatting for copy returns the zone, hemisphere, and rounded easting and northing`() {
        val result = subject.formatForCopy(coordinates)

        with(coordinates.asUtmCoordinates().shouldNotBeNull()) {
            val roundedAndPaddedEasting = easting.inRoundedAndPaddedMeters()
            val roundedAndPaddedNorthing = northing.inRoundedAndPaddedMeters()
            result.shouldBe("$zone$hemisphere ${roundedAndPaddedEasting}m E ${roundedAndPaddedNorthing}m N")
        }
    }
}

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString()
        .padStart(UtmCoordinates.EASTING_NORTHING_LENGTH, UtmCoordinates.EASTING_NORTHING_PAD_CHAR)
