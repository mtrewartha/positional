package io.trewartha.positional.data.sun

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.location.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import java.util.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LibrarySolarTimesRepositoryTest {

    private val northOfLongyearbyen = Coordinates(89.000, 15.490)
    private val longyearbyenTimeZone = TimeZone.getTimeZone("Europe/Oslo")
    private val mitadDelMundo = Coordinates(-0.00217, -78.45581)
    private val mitadDelMundoTimeZone = TimeZone.getTimeZone("America/Guayaquil")
    private val winterSolstice2023 = LocalDate(2023, Month.DECEMBER, 21)
    private lateinit var originalDefaultTimeZone: TimeZone

    private lateinit var subject: LibrarySolarTimesRepository

    @BeforeTest
    fun setUp() {
        originalDefaultTimeZone = TimeZone.getDefault()
        subject = LibrarySolarTimesRepository()
    }

    @AfterTest
    fun tearDown() {
        TimeZone.setDefault(originalDefaultTimeZone)
    }

    @Test
    fun testAstronomicalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getAstronomicalDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(4, 53))
    }

    @Test
    fun testNoAstronomicalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getAstronomicalDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testNauticalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getNauticalDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(5, 19))
    }

    @Test
    fun testNoNauticalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getNauticalDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testCivilDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getCivilDawn(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(5, 46))
    }

    @Test
    fun testNoCivilDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getCivilDawn(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testSunriseAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getSunrise(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(6, 8))
    }

    @Test
    fun testNoSunriseAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getSunrise(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testSunsetAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getSunset(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(18, 16))
    }

    @Test
    fun testNoSunsetAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getSunset(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testCivilDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getCivilDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(18, 38))
    }

    @Test
    fun testNoCivilDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getCivilDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testNauticalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getNauticalDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(19, 4))
    }

    @Test
    fun testNoNauticalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getNauticalDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }

    @Test
    fun testAstronomicalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)
        subject.getAstronomicalDusk(mitadDelMundo, winterSolstice2023).shouldBe(LocalTime(19, 31))
    }

    @Test
    fun testNoAstronomicalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)
        subject.getAstronomicalDusk(northOfLongyearbyen, winterSolstice2023).shouldBeNull()
    }
}
