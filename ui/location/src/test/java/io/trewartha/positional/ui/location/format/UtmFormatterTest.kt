package io.trewartha.positional.ui.location.format

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.trewartha.positional.model.core.measurement.Hemisphere.NORTH
import io.trewartha.positional.model.core.measurement.Hemisphere.SOUTH
import io.trewartha.positional.model.core.measurement.UtmCoordinates
import io.trewartha.positional.model.core.measurement.meters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class UtmFormatterTest {

    private lateinit var subject: UtmFormatter

    @Before
    fun setUp() {
        subject = UtmFormatter(RuntimeEnvironment.getApplication(), Locale.US)
    }

    @Test
    fun `Formatting for display returns the grid zone designation in the first line`() {
        val northResult = subject.formatForDisplay(UtmCoordinates(1, NORTH, 2.meters, 3.meters))
        // Avoid the south pole since it's a special case
        val southResult =
            subject.formatForDisplay(UtmCoordinates(1, SOUTH, 2.meters, 9_999_999.meters))

        northResult[0].shouldBe("1N")
        southResult[0].shouldBe("1S")
    }

    @Test
    fun `Formatting for display returns the easting on the second line`() {
        val result = subject.formatForDisplay(UtmCoordinates(1, NORTH, 2.meters, 3.meters))

        result[1].shouldBe("2m E")
    }

    @Test
    fun `Formatting for display returns the northing on the third line`() {
        val result = subject.formatForDisplay(UtmCoordinates(1, NORTH, 2.meters, 3.meters))

        result[2].shouldBe("3m N")
    }

    @Test
    fun `Formatting for display rounds easting and northing to the nearest meter`() {
        val result = subject.formatForDisplay(UtmCoordinates(10, NORTH, 1.4999.meters, 2.5.meters))

        result[1].shouldBe("1m E")
        result[2].shouldBe("3m N")
    }

    @Test
    fun `Formatting for copy separates the zone, easting, and northing with spaces`() {
        val result = subject.formatForCopy(UtmCoordinates(1, NORTH, 2.meters, 3.meters))

        result.shouldBe("1N 2m E 3m N")
    }

    @Test
    fun `Formatting for copy rounds easting and northing to the nearest meter`() {
        val result = subject.formatForCopy(UtmCoordinates(10, NORTH, 1.4999.meters, 2.5.meters))

        result.shouldContain("1m E")
        result.shouldContain("3m N")
    }
}
