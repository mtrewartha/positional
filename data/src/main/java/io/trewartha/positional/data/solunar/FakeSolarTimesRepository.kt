package io.trewartha.positional.data.solunar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class FakeSolarTimesRepository : SolarTimesRepository {

    private val sunrises = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val sunsets = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val astronomicalDawns = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val astronomicalDusks = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val nauticalDawns = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val nauticalDusks = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val civilDawns = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()
    private val civilDusks = mutableMapOf<Triple<LocalDate, Double, Double>, LocalTime?>()

    override fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        sunrises[Triple(date, latitude, longitude)]

    override fun getSunset(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        sunsets[Triple(date, latitude, longitude)]

    override fun getCivilDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = civilDawns[Triple(date, latitude, longitude)]

    override fun getCivilDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = civilDusks[Triple(date, latitude, longitude)]

    override fun getNauticalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = nauticalDawns[Triple(date, latitude, longitude)]

    override fun getNauticalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = nauticalDusks[Triple(date, latitude, longitude)]

    override fun getAstronomicalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = astronomicalDawns[Triple(date, latitude, longitude)]

    override fun getAstronomicalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = astronomicalDusks[Triple(date, latitude, longitude)]

    fun setAstronomicalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        astronomicalDawn: LocalTime?
    ) {
        astronomicalDawns[Triple(date, latitude, longitude)] = astronomicalDawn
    }

    fun setAstronomicalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        astronomicalDusk: LocalTime?
    ) {
        astronomicalDusks[Triple(date, latitude, longitude)] = astronomicalDusk
    }

    fun setCivilDawn(date: LocalDate, latitude: Double, longitude: Double, civilDawn: LocalTime?) {
        civilDawns[Triple(date, latitude, longitude)] = civilDawn
    }

    fun setCivilDusk(date: LocalDate, latitude: Double, longitude: Double, civilDusk: LocalTime?) {
        civilDusks[Triple(date, latitude, longitude)] = civilDusk
    }

    fun setNauticalDawn(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        nauticalDawn: LocalTime?
    ) {
        nauticalDawns[Triple(date, latitude, longitude)] = nauticalDawn
    }

    fun setNauticalDusk(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        nauticalDusk: LocalTime?
    ) {
        nauticalDusks[Triple(date, latitude, longitude)] = nauticalDusk
    }

    fun setSunrise(date: LocalDate, latitude: Double, longitude: Double, sunrise: LocalTime?) {
        sunrises[Triple(date, latitude, longitude)] = sunrise
    }

    fun setSunset(date: LocalDate, latitude: Double, longitude: Double, sunset: LocalTime?) {
        sunsets[Triple(date, latitude, longitude)] = sunset
    }
}
