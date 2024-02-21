package io.trewartha.positional.ui.location.format

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.trewartha.positional.model.core.measurement.Coordinates
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
    fun formatForDisplayReturnsTwoLines() {
        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        result.size.shouldBe(2)
    }

    @Test
    fun commaDecimalSeparatorUsedForCopyInAppropriateLocales() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldMatch(Regex("\\d+,\\d+°, \\d+,\\d+°"))
    }

    @Test
    fun commaDecimalSeparatorUsedForDisplayInAppropriateLocales() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldMatch(Regex("\\d+,\\d+°"))
    }

    @Test
    fun periodDecimalSeparatorUsedForCopyInAppropriateLocales() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldMatch(Regex("\\d+\\.\\d+°, \\d+\\.\\d+°"))
    }

    @Test
    fun periodDecimalSeparatorUsedForDisplayInAppropriateLocales() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldMatch(Regex("\\d+\\.\\d+°"))
    }

    @Test
    fun latitudeAndLongitudeRoundedForCopy() {
        val result = subject.formatForCopy(Coordinates(1.2345678, 2.3456789))

        withClue("Latitude and longitude should be rounded to the nearest 5th decimal place") {
            result.shouldContain("1.23457°").shouldContain("2.34568°")
        }
    }

    @Test
    fun latitudeAndLongitudeRoundedForDisplay() {
        val result = subject.formatForDisplay(Coordinates(1.2345678, 2.3456789))

        withClue("Latitude and longitude should be rounded to the nearest 5th decimal place") {
            result.shouldContain("1.23457°").shouldContain("2.34568°")
        }
    }

    private fun createFormatter(locale: Locale = Locale.US) =
        DecimalDegreesFormatter(
            InstrumentationRegistry.getInstrumentation().targetContext,
            locale
        )
}
