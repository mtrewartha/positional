package io.trewartha.positional.ui.location.format

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.trewartha.positional.model.core.measurement.Coordinates
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class DecimalDegreesFormatterTest {

    private lateinit var subject: DecimalDegreesFormatter

    @Before
    fun setUp() {
        subject = createFormatter(Locale.US)
    }

    @Test
    fun `Formatting for display uses comma decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldContain(",")
    }

    @Test
    fun `Formatting for display uses dot decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForDisplay(Coordinates(1.0, 2.0))

        for (line in result) line.shouldContain(".")
    }

    @Test
    fun `Formatting for display pads and rounds appropriately`() {
        val result = subject.formatForDisplay(Coordinates(0.123456, -1.234567))

        result.shouldHaveSize(2)
        result[0].shouldBe("   0.12346°")
        result[1].shouldBe("  -1.23457°")
    }

    @Test
    fun `Formatting for copy uses comma decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.FRANCE)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldContain(",")
    }

    @Test
    fun `Formatting for copy uses dot decimal separator for appropriate locales`() {
        val subject = createFormatter(Locale.US)

        val result = subject.formatForCopy(Coordinates(1.0, 2.0))

        result.shouldContain(".")
    }

    @Test
    fun `Formatting for copy rounds minutes to nearest fifth decimal place`() {
        val result = subject.formatForCopy(Coordinates(0.123456, -1.234567))

        result.shouldBe("0.12346°, -1.23457°")
    }

    private fun createFormatter(locale: Locale = Locale.US) =
        DecimalDegreesFormatter(RuntimeEnvironment.getApplication(), locale)
}
