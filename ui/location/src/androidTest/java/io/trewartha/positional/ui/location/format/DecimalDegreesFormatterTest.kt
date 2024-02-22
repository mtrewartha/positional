package io.trewartha.positional.ui.location.format

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
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
    fun formatForDisplayUsesCommaDecimalSeparatorForAppropriateLocales() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldContain(",")
    }

    @Test
    fun formatForDisplayUsesDotDecimalSeparatorForAppropriateLocales() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldContain(".")
    }

    @Test
    fun formatForDisplayPadsAndRoundsAppropriately() {
        val result = subject.formatForDisplay(Coordinates(0.123456, -1.234567))

        result.shouldHaveSize(2)
        result[0].shouldBe("   0.12346°")
        result[1].shouldBe("  -1.23457°")
    }

    @Test
    fun formatForCopyUsesCommaDecimalSeparatorForAppropriateLocales() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldContain(",")
    }

    @Test
    fun formatForCopyUsesDotDecimalSeparatorForAppropriateLocales() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldContain(".")
    }

    @Test
    fun formatForCopyRoundsMinutesToNearestFifthDecimalPlace() {
        val result = subject.formatForCopy(Coordinates(0.123456, -1.234567))

        result.shouldBe("0.12346°, -1.23457°")
    }

    private fun createFormatter(locale: Locale = Locale.US) =
        DecimalDegreesFormatter(
            InstrumentationRegistry.getInstrumentation().targetContext,
            locale
        )
}
