package io.trewartha.positional.data.sun

import io.trewartha.positional.data.location.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class FakeSolarTimesRepository : SolarTimesRepository {

    private val sunrises = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val sunsets = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val astronomicalDawns = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val astronomicalDusks = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val nauticalDawns = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val nauticalDusks = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val civilDawns = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()
    private val civilDusks = mutableMapOf<Pair<Coordinates, LocalDate>, LocalTime?>()

    override fun getSunrise(coordinates: Coordinates, date: LocalDate): LocalTime? =
        sunrises[Pair(coordinates, date)]

    override fun getSunset(coordinates: Coordinates, date: LocalDate): LocalTime? =
        sunsets[Pair(coordinates, date)]

    override fun getCivilDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        civilDawns[Pair(coordinates, date)]

    override fun getCivilDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        civilDusks[Pair(coordinates, date)]

    override fun getNauticalDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        nauticalDawns[Pair(coordinates, date)]

    override fun getNauticalDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        nauticalDusks[Pair(coordinates, date)]

    override fun getAstronomicalDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        astronomicalDawns[Pair(coordinates, date)]

    override fun getAstronomicalDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        astronomicalDusks[Pair(coordinates, date)]

    fun setAstronomicalDawn(
        coordinates: Coordinates,
        date: LocalDate,
        astronomicalDawn: LocalTime?
    ) {
        astronomicalDawns[Pair(coordinates, date)] = astronomicalDawn
    }

    fun setAstronomicalDusk(
        coordinates: Coordinates,
        date: LocalDate,
        astronomicalDusk: LocalTime?
    ) {
        astronomicalDusks[Pair(coordinates, date)] = astronomicalDusk
    }

    fun setCivilDawn(coordinates: Coordinates, date: LocalDate, civilDawn: LocalTime?) {
        civilDawns[Pair(coordinates, date)] = civilDawn
    }

    fun setCivilDusk(coordinates: Coordinates, date: LocalDate, civilDusk: LocalTime?) {
        civilDusks[Pair(coordinates, date)] = civilDusk
    }

    fun setNauticalDawn(
        coordinates: Coordinates,
        date: LocalDate,
        nauticalDawn: LocalTime?
    ) {
        nauticalDawns[Pair(coordinates, date)] = nauticalDawn
    }

    fun setNauticalDusk(
        coordinates: Coordinates,
        date: LocalDate,
        nauticalDusk: LocalTime?
    ) {
        nauticalDusks[Pair(coordinates, date)] = nauticalDusk
    }

    fun setSunrise(coordinates: Coordinates, date: LocalDate, sunrise: LocalTime?) {
        sunrises[Pair(coordinates, date)] = sunrise
    }

    fun setSunset(coordinates: Coordinates, date: LocalDate, sunset: LocalTime?) {
        sunsets[Pair(coordinates, date)] = sunset
    }
}
