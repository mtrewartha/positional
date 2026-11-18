package io.trewartha.positional.location.ui

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.degrees
import java.util.Locale

private fun <T> withDefaultLocale(locale: Locale, block: () -> T): T {
    val original = Locale.getDefault()
    Locale.setDefault(locale)
    try {
        return block()
    } finally {
        Locale.setDefault(original)
    }
}

class GeoUriTest : DescribeSpec({

    describe("building a geo: URI") {
        context("when the default locale uses a period as the decimal separator") {
            it("uses plain decimal degrees") {
                withDefaultLocale(Locale.US) {
                    geoUri(GeodeticCoordinates(52.52.degrees, 13.405.degrees), "Pin")
                }.shouldBe("geo:52.520000,13.405000?q=52.520000,13.405000(Pin)")
            }
        }

        context("when the default locale is de-DE") {
            it("uses periods as decimal separators") {
                withDefaultLocale(Locale.GERMANY) {
                    geoUri(GeodeticCoordinates(52.52.degrees, 13.405.degrees), "Pin")
                }.shouldBe("geo:52.520000,13.405000?q=52.520000,13.405000(Pin)")
            }
        }

        context("when the default locale is fr-FR") {
            it("uses periods as decimal separators") {
                withDefaultLocale(Locale.FRANCE) {
                    geoUri(GeodeticCoordinates((-33.8688).degrees, 151.2093.degrees), "Pin")
                }.shouldBe("geo:-33.868800,151.209300?q=-33.868800,151.209300(Pin)")
            }
        }

        context("when a coordinate has a large magnitude") {
            it("has no grouping separators") {
                withDefaultLocale(Locale.GERMANY) {
                    geoUri(GeodeticCoordinates(0.degrees, (-179.999999).degrees), "Pin")
                }.shouldBe("geo:0.000000,-179.999999?q=0.000000,-179.999999(Pin)")
            }
        }

        context("when a coordinate rounds to zero from below") {
            it("shows zero without a sign") {
                geoUri(GeodeticCoordinates((-0.0000001).degrees, (-0.0).degrees), "Pin")
                    .shouldBe("geo:0.000000,0.000000?q=0.000000,0.000000(Pin)")
            }
        }

        context("when the label contains spaces, commas, colons, and parentheses") {
            it("percent-encodes the label") {
                geoUri(GeodeticCoordinates(1.degrees, 2.degrees), "Nov 18, 2026 (5:30 PM)")
                    .shouldBe(
                        "geo:1.000000,2.000000?q=1.000000,2.000000" +
                            "(Nov%2018%2C%202026%20%285%3A30%20PM%29)"
                    )
            }
        }
    }
})
