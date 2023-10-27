package io.trewartha.positional.domain.solunar

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.data.solunar.LibrarySolarTimesRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

private val JAN_1_2000 = LocalDate(2000, Month.JANUARY, 1)
private val MAR_1_2000 = LocalDate(2000, Month.MARCH, 1)
private val JUN_1_2000 = LocalDate(2000, Month.JUNE, 1)
private val SEP_1_2000 = LocalDate(2000, Month.SEPTEMBER, 1)
private val DEC_1_2000 = LocalDate(2000, Month.DECEMBER, 1)

class LocalSolarTimesRepositorySpec : BehaviorSpec({

    lateinit var subject: LibrarySolarTimesRepository

    val dates = listOf(JAN_1_2000, MAR_1_2000, JUN_1_2000, SEP_1_2000, DEC_1_2000)

    // Duluth, MN (according to Google)
    val latitude = 46.7867
    val longitude = -92.1005

    beforeTest {
        subject = LibrarySolarTimesRepository()
    }

    given("a date at a specific location") {
        `when`("astronomical dawn is calculated") {
            val astronomicalDawns = dates.associateWith { date ->
                subject.getAstronomicalDawn(date, latitude, longitude)
            }
            then("then it is correct") {
                astronomicalDawns[JAN_1_2000]!!.shouldBe(LocalTime(6, 3))
                astronomicalDawns[MAR_1_2000]!!.shouldBe(LocalTime(5, 6))
                astronomicalDawns[JUN_1_2000]!!.shouldBe(LocalTime(2, 38))
                astronomicalDawns[SEP_1_2000]!!.shouldBe(LocalTime(4, 39))
                astronomicalDawns[DEC_1_2000]!!.shouldBe(LocalTime(5, 45))
            }
        }
        `when`("nautical dawn is calculated") {
            val nauticalDawns = dates.associateWith { date ->
                subject.getNauticalDawn(date, latitude, longitude)
            }
            then("then it is correct") {
                nauticalDawns[JAN_1_2000]!!.shouldBe(LocalTime(6, 40))
                nauticalDawns[MAR_1_2000]!!.shouldBe(LocalTime(5, 41))
                nauticalDawns[JUN_1_2000]!!.shouldBe(LocalTime(3, 48))
                nauticalDawns[SEP_1_2000]!!.shouldBe(LocalTime(5, 20))
                nauticalDawns[DEC_1_2000]!!.shouldBe(LocalTime(6, 21))
            }
        }
        `when`("civil dawn is calculated") {
            val civilDawns = dates.associateWith { date ->
                subject.getCivilDawn(date, latitude, longitude)
            }
            then("then it is correct") {
                civilDawns[JAN_1_2000]!!.shouldBe(LocalTime(7, 18))
                civilDawns[MAR_1_2000]!!.shouldBe(LocalTime(6, 16))
                civilDawns[JUN_1_2000]!!.shouldBe(LocalTime(4, 39))
                civilDawns[SEP_1_2000]!!.shouldBe(LocalTime(5, 58))
                civilDawns[DEC_1_2000]!!.shouldBe(LocalTime(6, 59))
            }
        }
        `when`("sunrise is calculated") {
            val sunrises = dates.associateWith { date ->
                subject.getSunrise(date, latitude, longitude)
            }
            then("then it is correct") {
                sunrises[JAN_1_2000]!!.shouldBe(LocalTime(7, 54))
                sunrises[MAR_1_2000]!!.shouldBe(LocalTime(6, 47))
                sunrises[JUN_1_2000]!!.shouldBe(LocalTime(5, 18))
                sunrises[SEP_1_2000]!!.shouldBe(LocalTime(6, 29))
                sunrises[DEC_1_2000]!!.shouldBe(LocalTime(7, 34))
            }
        }
        `when`("sunset is calculated") {
            val sunsets = dates.associateWith { date ->
                subject.getSunset(date, latitude, longitude)
            }
            then("then it is correct") {
                sunsets[JAN_1_2000]!!.shouldBe(LocalTime(16, 31))
                sunsets[MAR_1_2000]!!.shouldBe(LocalTime(17, 55))
                sunsets[JUN_1_2000]!!.shouldBe(LocalTime(20, 56))
                sunsets[SEP_1_2000]!!.shouldBe(LocalTime(19, 46))
                sunsets[DEC_1_2000]!!.shouldBe(LocalTime(16, 22))
            }
        }
        `when`("civil dusk is calculated") {
            val civilDusks = dates.associateWith { date ->
                subject.getCivilDusk(date, latitude, longitude)
            }
            then("then it is correct") {
                civilDusks[JAN_1_2000]!!.shouldBe(LocalTime(17, 6))
                civilDusks[MAR_1_2000]!!.shouldBe(LocalTime(18, 26))
                civilDusks[JUN_1_2000]!!.shouldBe(LocalTime(21, 34))
                civilDusks[SEP_1_2000]!!.shouldBe(LocalTime(20, 18))
                civilDusks[DEC_1_2000]!!.shouldBe(LocalTime(16, 56))
            }
        }
        `when`("nautical dusk is calculated") {
            val nauticalDusks = dates.associateWith { date ->
                subject.getNauticalDusk(date, latitude, longitude)
            }
            then("then it is correct") {
                nauticalDusks[JAN_1_2000]!!.shouldBe(LocalTime(17, 44))
                nauticalDusks[MAR_1_2000]!!.shouldBe(LocalTime(19, 1))
                nauticalDusks[JUN_1_2000]!!.shouldBe(LocalTime(22, 25))
                nauticalDusks[SEP_1_2000]!!.shouldBe(LocalTime(20, 55))
                nauticalDusks[DEC_1_2000]!!.shouldBe(LocalTime(17, 34))
            }
        }
        `when`("astronomical dusk is calculated") {
            val astronomicalDusks = dates.associateWith { date ->
                subject.getAstronomicalDusk(date, latitude, longitude)
            }
            then("then it is correct") {
                astronomicalDusks[JAN_1_2000]!!.shouldBe(LocalTime(18, 21))
                astronomicalDusks[MAR_1_2000]!!.shouldBe(LocalTime(19, 36))
                astronomicalDusks[JUN_1_2000]!!.shouldBe(LocalTime(23, 36))
                astronomicalDusks[SEP_1_2000]!!.shouldBe(LocalTime(21, 36))
                astronomicalDusks[DEC_1_2000]!!.shouldBe(LocalTime(18, 11))
            }
        }
    }
})
