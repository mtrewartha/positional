package io.trewartha.positional.ui.location.format

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.trewartha.positional.model.core.measurement.GeodeticCoordinates
import io.trewartha.positional.model.core.measurement.degrees
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class DegreesMinutesSecondsFormatterTest {

    private lateinit var subject: DegreesMinutesSecondsFormatter

    @Before
    fun setUp() {
        subject = createFormatter(Locale.US)
    }

    @Test
    fun `Formatting for display uses comma decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForDisplay(GeodeticCoordinates(1.degrees, 2.degrees))

        for (line in result) line.shouldContain(",")
    }

    @Test
    fun `Formatting for display uses dot decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForDisplay(GeodeticCoordinates(1.degrees, 2.degrees))

        for (line in result) line.shouldContain(".")
    }

    @Test
    fun `Formatting for display pads and rounds appropriately`() {
        val result = subject.formatForDisplay(
            GeodeticCoordinates(0.123458.degrees, (-1.234567).degrees)
        )

        result.shouldHaveSize(2)
        result[0].shouldBe("   0°  7' 24.45\"")
        result[1].shouldBe("  -1° 14' 4.44\"")
    }

    @Test
    fun `Formatting for copy uses comma decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForCopy(GeodeticCoordinates(1.degrees, 2.degrees))

        result.shouldContain(",")
    }

    @Test
    fun `Formatting for copy uses dot decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForCopy(GeodeticCoordinates(1.degrees, 2.degrees))

        result.shouldContain(".")
    }

    @Test
    fun `Formatting for copy rounds seconds to nearest second decimal place`() {
        val result = subject.formatForCopy(
            GeodeticCoordinates(0.123458.degrees, (-1.234567).degrees)
        )

        result.shouldBe("0° 7' 24.45\", -1° 14' 4.44\"")
    }
}

private fun createFormatter(locale: Locale) =
    DegreesMinutesSecondsFormatter(RuntimeEnvironment.getApplication(), locale)
