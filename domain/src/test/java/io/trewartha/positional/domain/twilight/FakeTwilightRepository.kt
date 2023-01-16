package io.trewartha.positional.domain.twilight

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class FakeTwilightRepository : TwilightRepository {

    override fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getSunset(date: LocalDate, latitude: Double, longitude: Double): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getMorningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getEveningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getMorningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getEveningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getMorningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }

    override fun getEveningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? {
        TODO("Not yet implemented")
    }
}
