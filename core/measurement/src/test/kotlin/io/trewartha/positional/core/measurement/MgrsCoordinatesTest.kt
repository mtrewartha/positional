package io.trewartha.positional.core.measurement

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import io.kotest.property.checkAll
import kotlin.math.roundToInt

class MgrsCoordinatesTest : DescribeSpec({

    describe("construction") {
        context("with an invalid grid zone designator") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("", "AA", 0.meters, 0.meters)
                }
            }
        }

        context("with an invalid grid square ID") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("1C", "A", 0.meters, 0.meters)
                }
            }
        }

        context("with a non-finite easting") {
            it("throws an IllegalArgumentException") {
                checkAll(
                    Arb.of(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)
                ) { easting ->
                    shouldThrow<IllegalArgumentException> {
                        MgrsCoordinates("1C", "AA", easting.meters, 0.meters)
                    }
                }
            }
        }

        context("with a negative easting") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("1C", "AA", (-1).meters, 0.meters)
                }
            }
        }

        context("with an easting that exceeds the 100 km grid square") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("1C", "AA", 100000.meters, 0.meters)
                }
            }
        }

        context("with a non-finite northing") {
            it("throws an IllegalArgumentException") {
                checkAll(
                    Arb.of(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)
                ) { northing ->
                    shouldThrow<IllegalArgumentException> {
                        MgrsCoordinates("1C", "AA", 0.meters, northing.meters)
                    }
                }
            }
        }

        context("with a negative northing") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("1C", "AA", 0.meters, (-1).meters)
                }
            }
        }

        context("with a northing that exceeds the 100 km grid square") {
            it("throws an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    MgrsCoordinates("1C", "AA", 0.meters, 100000.meters)
                }
            }
        }
    }

    describe("converting to a string") {
        context("when easting and northing don't need padding") {
            it("concatenates the GZD, grid square ID, rounded easting, and rounded northing") {
                MgrsCoordinates("31N", "AA", 12345.6.meters, 65432.1.meters)
                    .toString()
                    .shouldBe("31NAA1234665432")
            }
        }

        context("when easting and northing are less than 10,000 meters") {
            it("zero-pads easting and northing to five digits") {
                MgrsCoordinates("31N", "AA", 100.meters, 100.meters)
                    .toString()
                    .shouldBe("31NAA0010000100")
            }
        }

        context("with a polar grid zone designator") {
            it("concatenates the single-letter GZD, grid square ID, rounded easting, and rounded northing") {
                val polar = GeodeticCoordinates(85.0.degrees, 0.0.degrees).asMgrsCoordinates()
                polar.toString().shouldBe(
                    "${polar.gridZoneDesignator}${polar.gridSquareID}" +
                        "${polar.easting.inRoundedAndPaddedMeters()}${polar.northing.inRoundedAndPaddedMeters()}"
                )
            }
        }
    }

    describe("converting to MGRS coordinates") {
        it("returns the same coordinates") {
            val coordinates = MgrsCoordinates("31N", "AA", 12345.6.meters, 65432.1.meters)
            coordinates.asMgrsCoordinates().shouldBe(coordinates)
        }
    }

    describe("converting to geodetic coordinates") {
        context("in a northern hemisphere zone") {
            it("returns geodetic coordinates with a positive latitude") {
                MgrsCoordinates("31N", "AA", 50000.0.meters, 50000.0.meters)
                    .asGeodeticCoordinates()
                    .latitude
                    .inDegrees()
                    .magnitude
                    .shouldBeGreaterThan(0.0)
            }
        }

        context("in a southern hemisphere zone") {
            it("returns geodetic coordinates with a negative latitude") {
                GeodeticCoordinates((-35.0).degrees, 3.0.degrees)
                    .asMgrsCoordinates()
                    .asGeodeticCoordinates()
                    .latitude
                    .inDegrees()
                    .magnitude
                    .shouldBeLessThan(0.0)
            }
        }

        context("in an eastern hemisphere zone") {
            it("returns geodetic coordinates with a positive longitude") {
                GeodeticCoordinates(35.0.degrees, 3.0.degrees)
                    .asMgrsCoordinates()
                    .asGeodeticCoordinates()
                    .longitude
                    .inDegrees()
                    .magnitude
                    .shouldBeGreaterThan(0.0)
            }
        }

        context("in a western hemisphere zone") {
            it("returns geodetic coordinates with a negative longitude") {
                GeodeticCoordinates(40.0.degrees, (-70.0).degrees)
                    .asMgrsCoordinates()
                    .asGeodeticCoordinates()
                    .longitude
                    .inDegrees()
                    .magnitude
                    .shouldBeLessThan(0.0)
            }
        }

        context("in a polar zone") {
            it("returns geodetic coordinates with a latitude above 84 degrees") {
                GeodeticCoordinates(85.0.degrees, 0.0.degrees)
                    .asMgrsCoordinates()
                    .asGeodeticCoordinates()
                    .latitude
                    .inDegrees()
                    .magnitude
                    .shouldBeGreaterThan(84.0)
            }
        }
    }

    describe("converting to UTM coordinates") {
        context("in a non-polar northern hemisphere zone") {
            it("returns non-null UTM coordinates") {
                MgrsCoordinates("31N", "AA", 50000.0.meters, 50000.0.meters)
                    .asUtmCoordinates()
                    .shouldNotBeNull()
            }
        }

        context("in a non-polar southern hemisphere zone") {
            it("returns non-null UTM coordinates") {
                GeodeticCoordinates((-35.0).degrees, 3.0.degrees)
                    .asMgrsCoordinates()
                    .asUtmCoordinates()
                    .shouldNotBeNull()
            }
        }

        context("in a north polar zone") {
            it("returns null") {
                GeodeticCoordinates(85.0.degrees, 0.0.degrees)
                    .asMgrsCoordinates()
                    .asUtmCoordinates()
                    .shouldBe(null)
            }
        }

        context("in a south polar zone") {
            it("returns null") {
                GeodeticCoordinates((-85.0).degrees, 0.0.degrees)
                    .asMgrsCoordinates()
                    .asUtmCoordinates()
                    .shouldBe(null)
            }
        }
    }
})

private fun Distance.inRoundedAndPaddedMeters(): String =
    inMeters().magnitude.roundToInt().toString().padStart(
        MgrsCoordinates.EASTING_NORTHING_LENGTH,
        MgrsCoordinates.EASTING_NORTHING_PAD_CHAR
    )
