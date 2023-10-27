package io.trewartha.positional.domain.solunar

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.solunar.FakeSolarTimesRepository
import io.trewartha.positional.data.solunar.SolarTimes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.time.Month

class GetSolarTimesUseCaseSpec : BehaviorSpec({

    lateinit var subject: GetSolarTimesUseCase
    lateinit var fakeSolarTimesRepository: FakeSolarTimesRepository

    beforeEach {
        fakeSolarTimesRepository = FakeSolarTimesRepository()
        subject = GetSolarTimesUseCase(fakeSolarTimesRepository)
    }

    given("a date, latitude, and longitude") {

        val localDate = LocalDate(2000, Month.JANUARY, 1)
        val latitude = 1.23
        val longitude = 4.56
        val astronomicalDawn = LocalTime(0, 0, 1)
        val nauticalDawn = LocalTime(0, 0, 2)
        val civilDawn = LocalTime(0, 0, 3)
        val sunrise = LocalTime(0, 0, 4)
        val sunset = LocalTime(0, 0, 5)
        val civilDusk = LocalTime(0, 0, 6)
        val nauticalDusk = LocalTime(0, 0, 7)
        val astronomicalDusk = LocalTime(0, 0, 8)

        `when`("the use case is executed") {

            lateinit var result: SolarTimes

            beforeEach {
                with(fakeSolarTimesRepository) {
                    setAstronomicalDawn(localDate, latitude, longitude, astronomicalDawn)
                    setNauticalDawn(localDate, latitude, longitude, nauticalDawn)
                    setCivilDawn(localDate, latitude, longitude, civilDawn)
                    setSunrise(localDate, latitude, longitude, sunrise)
                    setSunset(localDate, latitude, longitude, sunset)
                    setCivilDusk(localDate, latitude, longitude, civilDusk)
                    setNauticalDusk(localDate, latitude, longitude, nauticalDusk)
                    setAstronomicalDusk(localDate, latitude, longitude, astronomicalDusk)
                }
                result = subject(localDate, latitude, longitude)
            }

            then("solar times are returned for the correct date, latitude, and longitude") {
                result.astronomicalDawn.shouldBe(astronomicalDawn)
                result.nauticalDawn.shouldBe(nauticalDawn)
                result.civilDawn.shouldBe(civilDawn)
                result.sunrise.shouldBe(sunrise)
                result.sunset.shouldBe(sunset)
                result.civilDusk.shouldBe(civilDusk)
                result.nauticalDusk.shouldBe(nauticalDusk)
                result.astronomicalDusk.shouldBe(astronomicalDusk)
            }
        }
    }
})
