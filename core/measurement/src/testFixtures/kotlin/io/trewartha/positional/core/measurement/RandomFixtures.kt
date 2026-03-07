package io.trewartha.positional.core.measurement

import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.numericDouble
import io.kotest.property.arbitrary.of
import io.trewartha.positional.core.test.TEST_RANDOM
import io.trewartha.positional.core.test.TEST_RANDOM_SOURCE

// Odd-numbered UTM zones only — MGRS column letters A–H (used in randomGridSquareID) are
// only valid for odd zones; even zones use J–R instead.
private val GZD_ODD_ZONE_NUMBERS = (1..59 step 2).toList()

// MGRS latitude band letters C–X (80°S to 84°N), excluding I and O.
private val GZD_BAND_LETTERS = ('C'..'X').filter { it != 'I' && it != 'O' }

// Column letters A–H are valid for odd-numbered UTM zones (see GZD_ODD_ZONE_NUMBERS).
// Row letters are A–V per the MGRS spec, excluding I and O.
private val MGRS_COLUMN_LETTERS = ('A'..'H').toList()

private val MGRS_ROW_LETTERS = ('A'..'V').filter { it != 'I' && it != 'O' }

public fun randomAngle(range: OpenEndRange<Double> = 0.0.rangeUntil(360.0)): Angle {
    val randomMagnitude =
        Arb.numericDouble(min = range.start, max = range.endExclusive).next(TEST_RANDOM_SOURCE)
    return Angle(
        randomMagnitude,
        randomAngleUnit(),
    )
}

public fun randomAngleUnit(): Angle.Unit = Arb.enum<Angle.Unit>().next(TEST_RANDOM_SOURCE)

public fun randomCoordinates(): Coordinates =
    when (val implementationClass = Coordinates::class.sealedSubclasses.random(TEST_RANDOM)) {
        GeodeticCoordinates::class -> randomGeodeticCoordinates()
        MgrsCoordinates::class -> randomMgrsCoordinates()
        UtmCoordinates::class -> randomUtmCoordinates()
        else -> error("Not implemented for ${implementationClass.qualifiedName}")
    }

public fun randomDistance(): Distance {
    val randomMagnitude = Arb.numericDouble().next(TEST_RANDOM_SOURCE)
    return Distance(randomMagnitude, randomDistanceUnit())
}

public fun randomDistanceUnit(): Distance.Unit = Arb.enum<Distance.Unit>().next(TEST_RANDOM_SOURCE)

public fun randomGeodeticCoordinates(): GeodeticCoordinates =
    GeodeticCoordinates(randomLatitude(), randomLongitude())

public fun randomHemisphere(): Hemisphere = Arb.enum<Hemisphere>().next(TEST_RANDOM_SOURCE)

public fun randomLatitude(): Angle = Arb.double(-90.0..90.0).next(TEST_RANDOM_SOURCE).degrees

public fun randomLongitude(): Angle = Arb.double(-180.0..180.0).next(TEST_RANDOM_SOURCE).degrees

public fun randomMgrsCoordinates(): MgrsCoordinates =
    randomGeodeticCoordinates().asMgrsCoordinates()

public fun randomMgrsEasting(): Distance = Arb.double(0.0..99999.0).next(TEST_RANDOM_SOURCE).meters

public fun randomMgrsGridSquareID(): String {
    val randomColumn = Arb.of(MGRS_COLUMN_LETTERS).next(TEST_RANDOM_SOURCE)
    val randomRow = Arb.of(MGRS_ROW_LETTERS).next(TEST_RANDOM_SOURCE)
    return "$randomColumn$randomRow"
}

public fun randomMgrsGridZoneDesignator(): String {
    val randomZone = Arb.of(GZD_ODD_ZONE_NUMBERS).next(TEST_RANDOM_SOURCE)
    val randomBand = Arb.of(GZD_BAND_LETTERS).next(TEST_RANDOM_SOURCE)
    return "$randomZone$randomBand"
}

public fun randomMgrsNorthing(): Distance = Arb.double(0.0..99999.0).next(TEST_RANDOM_SOURCE).meters

public fun randomSpeed(): Speed {
    val randomMagnitude = Arb.double(0.0..1000.0).next(TEST_RANDOM_SOURCE)
    return Speed(randomMagnitude, randomSpeedUnit())
}

public fun randomSpeedUnit(): Speed.Unit = Arb.enum<Speed.Unit>().next(TEST_RANDOM_SOURCE)

public fun randomUnits(): Units = Arb.enum<Units>().next(TEST_RANDOM_SOURCE)

public fun randomUtmCoordinates(): UtmCoordinates {
    val latitude = Arb.double(-80.0..84.0).next(TEST_RANDOM_SOURCE).degrees
    val longitude = randomLongitude()
    return requireNotNull(GeodeticCoordinates(latitude, longitude).asUtmCoordinates())
}

public fun randomUtmEasting(): Distance =
    Arb.double(100000.0..899999.0).next(TEST_RANDOM_SOURCE).meters

public fun randomUtmNorthing(): Distance =
    Arb.double(0.0..9999999.0).next(TEST_RANDOM_SOURCE).meters

public fun randomUtmZone(): Int = Arb.int(1..60).next(TEST_RANDOM_SOURCE)
