package io.trewartha.positional.ui.location.format

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.trewartha.positional.model.core.measurement.MgrsCoordinates
import io.trewartha.positional.model.core.measurement.meters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MgrsFormatterTest {

    private lateinit var subject: MgrsFormatter

    @Before
    fun setUp() {
        subject = MgrsFormatter()
    }

    @Test
    fun `Formatting for display returns the zone, band, and 100km square ID in the first line`() {
        val result = subject.formatForDisplay(MgrsCoordinates(1, "T", "VK", 2.meters, 3.meters))

        result[0].shouldBe("1T VK")
    }

    @Test
    fun `Formatting for display returns the easting on the second line`() {
        val result = subject.formatForDisplay(MgrsCoordinates(1, "T", "VK", 2.meters, 3.meters))

        result[1].shouldBe("00002")
    }

    @Test
    fun `Formatting for display returns the northing on the third line`() {
        val result = subject.formatForDisplay(MgrsCoordinates(1, "T", "VK", 2.meters, 3.meters))

        result[2].shouldBe("00003")
    }

    @Test
    fun `Formatting for display rounds easting and northing to the nearest meter`() {
        val result = subject.formatForDisplay(MgrsCoordinates(1, "T", "VK", 2.4.meters, 3.5.meters))

        result[1].shouldBe("00002")
        result[2].shouldBe("00004")
    }

    @Test
    fun `Formatting for copy separates the zone and band, easting, and northing with spaces`() {
        val result = subject.formatForCopy(MgrsCoordinates(1, "T", "VK", 2.meters, 3.meters))

        result.shouldBe("1T VK 00002 00003")
    }

    @Test
    fun `Formatting for copy rounds easting and northing to the nearest meter`() {
        val result = subject.formatForCopy(MgrsCoordinates(1, "T", "VK", 2.4.meters, 3.5.meters))

        result.shouldContain("00002")
        result.shouldContain("00004")
    }
}
