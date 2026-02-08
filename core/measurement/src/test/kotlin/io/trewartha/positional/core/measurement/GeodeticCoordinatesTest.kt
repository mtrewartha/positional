package io.trewartha.positional.core.measurement

import earth.worldwind.geom.Angle
import earth.worldwind.geom.coords.MGRSCoord
import earth.worldwind.geom.coords.UTMCoord
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith

class GeodeticCoordinatesTest : AnnotationSpec() {

    // Many complicated tests would be required to properly verify conversion between coordinates
    // formats. These tests would be difficult to make comprehensive for every location on Earth
    // since there are so many nuances in the various formats. They would also be very error prone.
    // WorldWind's coordinates conversions are highly trusted, so we verify against them instead.

    @Test
    fun `Conversion to geodetic coordinates returns the original coordinates`() {
        forGeodeticCoordinates { coordinates ->
            val result = coordinates.asGeodeticCoordinates()

            result.shouldBe(coordinates)
        }
    }

    @Test
    fun `Conversion to MGRS matches the WorldWind conversion`() {
        forGeodeticCoordinates { coordinates ->
            val result = coordinates.asMgrsCoordinates().toString()

            val worldWindMgrsConversion = coordinates.toWorldWindMgrsString()
            withClue("Unexpected result for $coordinates") {
                result.filterWordCharacters()
                    .shouldBe(worldWindMgrsConversion.filterWordCharacters().trimStart('0'))
            }
        }
    }

    @Test
    fun `Conversion to UTM matches the WorldWind conversion`() {
        forGeodeticCoordinates { coordinates ->
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
                if (latitudeDegrees > 84.0 || latitudeDegrees < -80) { // UTM doesn't accommodate poles
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

    @Test
    fun `Conversion to a string returns a comma-separated latitude and longitude`() {
        val coordinates = GeodeticCoordinates(1.degrees, 2.degrees)

        val result = coordinates.toString()

        result.shouldContain(", ")
    }

    @Test
    fun `Conversion to a string rounds latitude and longitude to the nearest 5th decimal place`() {
        val coordinates = GeodeticCoordinates(1.234567.degrees, (-4.567894).degrees)

        val result = coordinates.toString()

        result.shouldContain("1.23457")
        result.shouldContain("-4.56789")
    }

    @Test
    fun `Conversion to a string puts latitude first in the resulting string`() {
        val coordinates = GeodeticCoordinates(1.degrees, (-2).degrees)

        val result = coordinates.toString()

        result.shouldStartWith("1.00000")
        result.shouldEndWith("-2.00000")
    }
}

private fun forGeodeticCoordinates(test: (GeodeticCoordinates) -> Unit) {
    for (latitude in -90..90) {
        for (longitude in -180..180) {
            test(GeodeticCoordinates(latitude.degrees, longitude.degrees))
        }
    }
}

private fun GeodeticCoordinates.toWorldWindMgrsString() = MGRSCoord.fromLatLon(
    Angle.fromDegrees(latitude.inDegrees().magnitude),
    Angle.fromDegrees(longitude.inDegrees().magnitude)
).toString()

private fun String.filterWordCharacters() = replace(Regex("\\W"), "")
