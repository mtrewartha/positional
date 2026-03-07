package io.trewartha.positional.core.measurement

import earth.worldwind.geom.Angle
import earth.worldwind.geom.coords.MGRSCoord
import earth.worldwind.geom.coords.UTMCoord
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.cartesian
import io.kotest.property.exhaustive.ints

class GeodeticCoordinatesTest : DescribeSpec({

    // Many complicated tests would be required to properly verify conversion between coordinates
    // formats. These tests would be difficult to make comprehensive for every location on Earth
    // since there are so many nuances in the various formats. They would also be very error prone.
    // WorldWind's coordinates conversions are highly trusted, so we verify against them instead.

    describe("converting to geodetic coordinates") {
        it("returns the original coordinates") {
            checkAll(Exhaustive.geodeticCoordinates()) { coordinates ->
                coordinates.asGeodeticCoordinates().shouldBe(coordinates)
            }
        }
    }

    describe("converting to MGRS") {
        it("matches the WorldWind conversion") {
            checkAll(Exhaustive.geodeticCoordinates()) { coordinates ->
                val result = coordinates.asMgrsCoordinates().toString()
                val worldWindMgrsConversion = coordinates.toWorldWindMgrsString()
                withClue("Unexpected result for $coordinates") {
                    result.filterWordCharacters()
                        .shouldBe(worldWindMgrsConversion.filterWordCharacters().trimStart('0'))
                }
            }
        }
    }

    describe("converting to UTM") {
        it("matches the WorldWind conversion") {
            checkAll(Exhaustive.geodeticCoordinates()) { coordinates ->
                val result = coordinates.asUtmCoordinates()
                val latitudeDegrees = coordinates.latitude.inDegrees().magnitude
                val longitudeDegrees = coordinates.longitude.inDegrees().magnitude
                val worldWindUtmResult = try {
                    UTMCoord.fromLatLon(
                        Angle.fromDegrees(latitudeDegrees),
                        Angle.fromDegrees(longitudeDegrees)
                    )
                } catch (_: IllegalArgumentException) {
                    null
                }
                withClue("Unexpected result for $coordinates") {
                    if (latitudeDegrees > 84.0 || latitudeDegrees < -80) {
                        result.shouldBe(null)
                    } else {
                        result.shouldNotBeNull()
                        worldWindUtmResult.shouldNotBeNull()
                        result.zone.shouldBe(worldWindUtmResult.zone)
                        result.hemisphere.shouldBe(
                            when (worldWindUtmResult.hemisphere) {
                                earth.worldwind.geom.coords.Hemisphere.N -> Hemisphere.NORTH
                                earth.worldwind.geom.coords.Hemisphere.S -> Hemisphere.SOUTH
                            }
                        )
                        result.easting.shouldBe(worldWindUtmResult.easting.meters)
                        result.northing.shouldBe(worldWindUtmResult.northing.meters)
                    }
                }
            }
        }
    }

    describe("converting to a string") {
        context("with integer latitude and longitude") {
            it("contains a comma separator") {
                GeodeticCoordinates(1.degrees, 2.degrees).toString().shouldContain(", ")
            }
        }

        context("with decimal latitude and longitude") {
            it("rounds values to the nearest fifth decimal place") {
                val result = GeodeticCoordinates(1.234567.degrees, (-4.567894).degrees).toString()
                result.shouldContain("1.23457")
                result.shouldContain("-4.56789")
            }
        }

        context("with positive latitude and negative longitude") {
            it("puts latitude first and longitude last") {
                val result = GeodeticCoordinates(1.degrees, (-2).degrees).toString()
                result.shouldStartWith("1.00000")
                result.shouldEndWith("-2.00000")
            }
        }

        context("with zero latitude and zero longitude") {
            it("formats both values with exactly five decimal places") {
                GeodeticCoordinates(0.degrees, 0.degrees).toString().shouldBe("0.00000, 0.00000")
            }
        }
    }
})

private fun Exhaustive.Companion.geodeticCoordinates() =
    cartesian(Exhaustive.ints(-90..90), Exhaustive.ints(-180..180)) { lat, lon ->
        GeodeticCoordinates(lat.degrees, lon.degrees)
    }

private fun GeodeticCoordinates.toWorldWindMgrsString() = MGRSCoord.fromLatLon(
    Angle.fromDegrees(latitude.inDegrees().magnitude),
    Angle.fromDegrees(longitude.inDegrees().magnitude)
).toString()

private fun String.filterWordCharacters() = replace(Regex("\\W"), "")
