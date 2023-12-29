package io.trewartha.positional.domain.sun

import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.location.Coordinates
import io.trewartha.positional.data.sun.FakeSolarTimesRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.time.Month
import kotlin.test.BeforeTest
import kotlin.test.Test

class GetSolarTimesUseCaseTest {

    private lateinit var fakeSolarTimesRepository: FakeSolarTimesRepository
    private lateinit var subject: GetSolarTimesUseCase

    private val localDate = LocalDate(2000, Month.JANUARY, 1)
    private val coordinates = Coordinates(1.23, 4.56)
    private val astronomicalDawn = LocalTime(0, 0, 1)
    private val nauticalDawn = LocalTime(0, 0, 2)
    private val civilDawn = LocalTime(0, 0, 3)
    private val sunrise = LocalTime(0, 0, 4)
    private val sunset = LocalTime(0, 0, 5)
    private val civilDusk = LocalTime(0, 0, 6)
    private val nauticalDusk = LocalTime(0, 0, 7)
    private val astronomicalDusk = LocalTime(0, 0, 8)

    @BeforeTest
    fun setUp() {
        fakeSolarTimesRepository = FakeSolarTimesRepository().apply {
            setAstronomicalDawn(coordinates, localDate, astronomicalDawn)
            setNauticalDawn(coordinates, localDate, nauticalDawn)
            setCivilDawn(coordinates, localDate, civilDawn)
            setSunrise(coordinates, localDate, sunrise)
            setSunset(coordinates, localDate, sunset)
            setCivilDusk(coordinates, localDate, civilDusk)
            setNauticalDusk(coordinates, localDate, nauticalDusk)
            setAstronomicalDusk(coordinates, localDate, astronomicalDusk)
        }
        subject = DefaultGetSolarTimesUseCase(fakeSolarTimesRepository)
    }

    @Test
    fun testAstronomicalDawn() {
        val result = subject(coordinates, localDate)

        result.astronomicalDawn.shouldBe(astronomicalDawn)
    }

    @Test
    fun testNauticalDawn() {
        val result = subject(coordinates, localDate)

        result.nauticalDawn.shouldBe(nauticalDawn)
    }

    @Test
    fun testCivilDawn() {
        val result = subject(coordinates, localDate)

        result.civilDawn.shouldBe(civilDawn)
    }

    @Test
    fun testSunrise() {
        val result = subject(coordinates, localDate)

        result.sunrise.shouldBe(sunrise)
    }

    @Test
    fun testSunset() {
        val result = subject(coordinates, localDate)

        result.sunset.shouldBe(sunset)
    }

    @Test
    fun testCivilDusk() {
        val result = subject(coordinates, localDate)

        result.civilDusk.shouldBe(civilDusk)
    }

    @Test
    fun testNauticalDusk() {
        val result = subject(coordinates, localDate)

        result.nauticalDusk.shouldBe(nauticalDusk)
    }

    @Test
    fun testAstronomicalDusk() {
        val result = subject(coordinates, localDate)

        result.astronomicalDusk.shouldBe(astronomicalDusk)
    }
}
