package io.trewartha.positional.location.ui

import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import java.util.Locale
import org.junit.After
import org.junit.Before
import org.junit.Test

class GeoUriTest {

    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `Building a URI uses plain decimal degrees`() {
        Locale.setDefault(Locale.US)

        val result = geoUri(GeodeticCoordinates(52.52.degrees, 13.405.degrees), "Pin")

        result.shouldBe("geo:52.520000,13.405000?q=52.520000,13.405000(Pin)")
    }

    @Test
    fun `Building a URI uses periods in a comma-decimal locale`() {
        Locale.setDefault(Locale.GERMANY)

        val result = geoUri(GeodeticCoordinates(52.52.degrees, 13.405.degrees), "Pin")

        result.shouldBe("geo:52.520000,13.405000?q=52.520000,13.405000(Pin)")
    }

    @Test
    fun `Building a URI uses periods in French`() {
        Locale.setDefault(Locale.FRANCE)

        val result = geoUri(GeodeticCoordinates((-33.8688).degrees, 151.2093.degrees), "Pin")

        result.shouldBe("geo:-33.868800,151.209300?q=-33.868800,151.209300(Pin)")
    }

    @Test
    fun `Building a URI has no grouping separators`() {
        Locale.setDefault(Locale.US)

        val result = geoUri(GeodeticCoordinates(0.degrees, (-179.999999).degrees), "Pin")

        result.shouldBe("geo:0.000000,-179.999999?q=0.000000,-179.999999(Pin)")
    }

    @Test
    fun `Building a URI never produces a negative zero`() {
        val result = geoUri(GeodeticCoordinates((-0.0000001).degrees, (-0.0).degrees), "Pin")

        result.shouldBe("geo:0.000000,0.000000?q=0.000000,0.000000(Pin)")
    }

    @Test
    fun `Building a URI percent-encodes the label`() {
        val result = geoUri(GeodeticCoordinates(1.degrees, 2.degrees), "Nov 18, 2026 (5:30 PM)")

        result.shouldBe(
            "geo:1.000000,2.000000?q=1.000000,2.000000(Nov%2018%2C%202026%20%285%3A30%20PM%29)"
        )
    }
}
