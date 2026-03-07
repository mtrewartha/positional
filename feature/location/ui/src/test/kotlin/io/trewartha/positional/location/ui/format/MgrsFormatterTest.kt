package io.trewartha.positional.location.ui.format

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.Distance
import io.trewartha.positional.core.measurement.MgrsCoordinates
import io.trewartha.positional.core.measurement.meters
import io.trewartha.positional.core.measurement.randomGeodeticCoordinates
import io.trewartha.positional.core.measurement.randomMgrsCoordinates
import kotlin.math.roundToInt

class MgrsFormatterTest : DescribeSpec({

    fun sut() = MgrsFormatter()

    describe("formatting for display") {
        context("with MGRS coordinates") {
            it("the first line contains the zone, band, and 100km square ID") {
                val coordinates = randomMgrsCoordinates()
                sut().formatForDisplay(coordinates)[0]
                    .shouldBe("${coordinates.gridZoneDesignator}\u00A0${coordinates.gridSquareID}")
            }

            it("the second line contains the rounded and padded easting") {
                val coordinates = randomMgrsCoordinates()
                sut().formatForDisplay(coordinates)[1]
                    .shouldBe(coordinates.easting.inRoundedAndPaddedMeters())
            }

            it("the third line contains the rounded and padded northing") {
                val coordinates = randomMgrsCoordinates()
                sut().formatForDisplay(coordinates)[2]
                    .shouldBe(coordinates.northing.inRoundedAndPaddedMeters())
            }
        }

        context("with null coordinates") {
            it("all three lines are null") {
                sut().formatForDisplay(null).shouldBe(listOf(null, null, null))
            }
        }

        context("with geodetic coordinates") {
            it("the first line contains the zone, band, and 100km square ID") {
                val geodetic = randomGeodeticCoordinates()
                val mgrs = geodetic.asMgrsCoordinates()
                sut().formatForDisplay(geodetic)[0]
                    .shouldBe("${mgrs.gridZoneDesignator}\u00A0${mgrs.gridSquareID}")
            }

            it("the second line contains the rounded and padded easting") {
                val geodetic = randomGeodeticCoordinates()
                sut().formatForDisplay(geodetic)[1]
                    .shouldBe(geodetic.asMgrsCoordinates().easting.inRoundedAndPaddedMeters())
            }

            it("the third line contains the rounded and padded northing") {
                val geodetic = randomGeodeticCoordinates()
                sut().formatForDisplay(geodetic)[2]
                    .shouldBe(geodetic.asMgrsCoordinates().northing.inRoundedAndPaddedMeters())
            }
        }

        context("with MGRS coordinates with easting at a rounding boundary") {
            it("the easting is rounded to the nearest integer") {
                val coordinates = MgrsCoordinates("31N", "AA", 12345.5.meters, 65432.0.meters)
                sut().formatForDisplay(coordinates)[1]
                    .shouldBe(coordinates.easting.inRoundedAndPaddedMeters())
            }
        }
    }

    describe("formatting for copy") {
        context("with MGRS coordinates") {
            it("the result is the string representation of the coordinates") {
                val coordinates = randomMgrsCoordinates()
                sut().formatForCopy(coordinates).shouldBe(coordinates.toString())
            }
        }

        context("with geodetic coordinates") {
            it("the result is the MGRS string representation of the coordinates") {
                val geodetic = randomGeodeticCoordinates()
                sut().formatForCopy(geodetic).shouldBe(geodetic.asMgrsCoordinates().toString())
            }
        }
    }
})

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString().padStart(
        MgrsCoordinates.EASTING_NORTHING_LENGTH,
        MgrsCoordinates.EASTING_NORTHING_PAD_CHAR
    )