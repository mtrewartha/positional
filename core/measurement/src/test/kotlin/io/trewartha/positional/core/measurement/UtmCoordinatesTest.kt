package io.trewartha.positional.core.measurement

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class UtmCoordinatesTest : DescribeSpec({

    describe("creating UTM coordinates") {
        context("when the zone number is less than 1") {
            it("throws an illegal argument exception") {
                shouldThrow<IllegalArgumentException> {
                    UtmCoordinates(-1, Hemisphere.NORTH, 0.meters, 0.meters)
                }
            }
        }

        context("when the zone number is 1") {
            it("does not throw an exception") {
                shouldNotThrowAny { UtmCoordinates(1, Hemisphere.NORTH, 500000.meters, 4649776.meters) }
            }
        }

        context("when the zone number is 60") {
            it("does not throw an exception") {
                shouldNotThrowAny { UtmCoordinates(60, Hemisphere.NORTH, 500000.meters, 4649776.meters) }
            }
        }

        context("when the zone number is greater than 60") {
            it("throws an illegal argument exception") {
                shouldThrow<IllegalArgumentException> {
                    UtmCoordinates(61, Hemisphere.NORTH, 0.meters, 0.meters)
                }
            }
        }

        context("when the easting is non-finite") {
            it("throws an illegal argument exception") {
                for (easting in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
                    shouldThrow<IllegalArgumentException> {
                        UtmCoordinates(1, Hemisphere.NORTH, easting.meters, 0.meters)
                    }
                }
            }
        }

        context("when the easting is negative") {
            it("throws an illegal argument exception") {
                shouldThrow<IllegalArgumentException> {
                    UtmCoordinates(1, Hemisphere.NORTH, (-1).meters, 0.meters)
                }
            }
        }

        context("when the northing is non-finite") {
            it("throws an illegal argument exception") {
                for (northing in listOf(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN)) {
                    shouldThrow<IllegalArgumentException> {
                        UtmCoordinates(1, Hemisphere.NORTH, 0.meters, northing.meters)
                    }
                }
            }
        }

        context("when the northing is negative") {
            it("throws an illegal argument exception") {
                shouldThrow<IllegalArgumentException> {
                    UtmCoordinates(1, Hemisphere.NORTH, 0.meters, (-1).meters)
                }
            }
        }
    }

    describe("converting to UTM coordinates") {
        it("returns the same coordinates") {
            val coordinates = UtmCoordinates(1, Hemisphere.NORTH, 12345.6789.meters, 987654.321.meters)
            coordinates.asUtmCoordinates().shouldBe(coordinates)
        }
    }

    describe("converting to geodetic coordinates") {
        context("when in the northern hemisphere") {
            // Zone 32N, easting 500000m, northing 5000000m is approximately 45°N, 9°E
            it("returns a positive latitude") {
                UtmCoordinates(32, Hemisphere.NORTH, 500000.meters, 5000000.meters)
                    .asGeodeticCoordinates().latitude.inDegrees().magnitude.shouldBeGreaterThan(0.0)
            }

            it("returns a positive longitude") {
                UtmCoordinates(32, Hemisphere.NORTH, 500000.meters, 5000000.meters)
                    .asGeodeticCoordinates().longitude.inDegrees().magnitude.shouldBeGreaterThan(0.0)
            }
        }

        context("when in the southern hemisphere") {
            // Zone 32S, easting 500000m, northing 5000000m is in the southern hemisphere
            it("returns a negative latitude") {
                UtmCoordinates(32, Hemisphere.SOUTH, 500000.meters, 5000000.meters)
                    .asGeodeticCoordinates().latitude.inDegrees().magnitude.shouldBeLessThan(0.0)
            }
        }
    }

    describe("converting to MGRS coordinates") {
        context("when in the northern hemisphere") {
            it("returns coordinates with a non-empty grid zone designator") {
                UtmCoordinates(32, Hemisphere.NORTH, 500000.meters, 5000000.meters)
                    .asMgrsCoordinates().gridZoneDesignator.shouldNotBeEmpty()
            }
        }

        context("when in the southern hemisphere") {
            it("returns coordinates with a non-empty grid zone designator") {
                UtmCoordinates(32, Hemisphere.SOUTH, 500000.meters, 5000000.meters)
                    .asMgrsCoordinates().gridZoneDesignator.shouldNotBeEmpty()
            }
        }
    }

    describe("converting to a string") {
        context("when in the northern hemisphere with a single-digit zone number") {
            it("formats the zone, hemisphere, easting, and northing correctly") {
                UtmCoordinates(1, Hemisphere.NORTH, 12345.6789.meters, 987654.321.meters)
                    .toString()
                    .shouldBe("1N\u00A00012346m\u00A0E\u00A00987654m\u00A0N")
            }
        }

        context("when in the southern hemisphere with a two-digit zone number") {
            it("formats the zone, hemisphere, easting, and northing correctly") {
                UtmCoordinates(32, Hemisphere.SOUTH, 500000.meters, 5000000.meters)
                    .toString()
                    .shouldBe("32S\u00A00500000m\u00A0E\u00A05000000m\u00A0N")
            }
        }
    }
})
