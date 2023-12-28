package io.trewartha.positional.ui.utils.format.coordinates

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.trewartha.positional.data.location.Coordinates
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DecimalDegreesFormatterTest {

    private lateinit var subject: DecimalDegreesFormatter

    @Before
    fun setUp() {
        subject = createFormatter(Locale.US)
    }

    @Test
    fun testFormatForDisplayReturnsTwoLines() {
        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        result.size.shouldBe(2)
    }

    @Test
    fun testCommaDecimalSeparatorForCopy() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldMatch(Regex("\\d+,\\d+°, \\d+,\\d+°"))
    }

    @Test
    fun testCommaDecimalSeparatorForDisplay() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldMatch(Regex("\\d+,\\d+°"))
    }

    @Test
    fun testPeriodDecimalSeparatorForCopy() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldMatch(Regex("\\d+\\.\\d+°, \\d+\\.\\d+°"))
    }

    @Test
    fun testPeriodDecimalSeparatorForDisplay() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldMatch(Regex("\\d+\\.\\d+°"))
    }

    @Test
    fun testRoundingOfLatitudeAndLongitudeForCopy() {
        val result = subject.formatForCopy(Coordinates(1.2345678, 2.3456789))

        withClue("Latitude and longitude should be rounded to the nearest 5th decimal place") {
            result.shouldContain("1.23457°").shouldContain("2.34568°")
        }
    }

    @Test
    fun testRoundingOfLatitudeAndLongitudeForDisplay() {
        val result = subject.formatForDisplay(Coordinates(1.2345678, 2.3456789))

        withClue("Latitude and longitude should be rounded to the nearest 5th decimal place") {
            result.shouldContain("1.23457°").shouldContain("2.34568°")
        }
    }

    private fun createFormatter(locale: Locale = Locale.US) =
        DecimalDegreesFormatter(InstrumentationRegistry.getInstrumentation().targetContext, locale)
}
