package io.trewartha.positional.data.sun

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Coordinates
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

    private lateinit var subject: SolarTimesRepository

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

        val result = subject.getAstronomicalDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(4, 53))
    }

    @Test
    fun testNoAstronomicalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getAstronomicalDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testNauticalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getNauticalDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(5, 19))
    }

    @Test
    fun testNoNauticalDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getNauticalDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testCivilDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getCivilDawn(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(5, 46))
    }

    @Test
    fun testNoCivilDawnAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getCivilDawn(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testSunriseAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getSunrise(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(6, 8))
    }

    @Test
    fun testNoSunriseAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getSunrise(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testSunsetAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getSunset(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(18, 16))
    }

    @Test
    fun testNoSunsetAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getSunset(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testCivilDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getCivilDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(18, 38))
    }

    @Test
    fun testNoCivilDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getCivilDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testNauticalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getNauticalDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(19, 4))
    }

    @Test
    fun testNoNauticalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getNauticalDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }

    @Test
    fun testAstronomicalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(mitadDelMundoTimeZone)

        val result = subject.getAstronomicalDusk(mitadDelMundo, winterSolstice2023)

        result.shouldBe(LocalTime(19, 31))
    }

    @Test
    fun testNoAstronomicalDuskAtCoordinatesOnDate() {
        TimeZone.setDefault(longyearbyenTimeZone)

        val result = subject.getAstronomicalDusk(northOfLongyearbyen, winterSolstice2023)

        result.shouldBeNull()
    }
}
