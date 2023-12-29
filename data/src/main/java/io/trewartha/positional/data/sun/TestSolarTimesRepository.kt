package io.trewartha.positional.data.sun

import io.trewartha.positional.data.location.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * [SolarTimesRepository] implementation intended for easing testing of classes that depend on a
 * [SolarTimesRepository]
 */
class TestSolarTimesRepository : SolarTimesRepository {

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

    /**
     * Sets the astronomical dawn time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param astronomicalDawn Time to set or `null` if no such time should exist at the coordinates
     * on the date
     */
    fun setAstronomicalDawn(
        coordinates: Coordinates,
        date: LocalDate,
        astronomicalDawn: LocalTime?
    ) {
        astronomicalDawns[Pair(coordinates, date)] = astronomicalDawn
    }

    /**
     * Sets the astronomical dusk time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param astronomicalDusk Time to set or `null` if no such time should exist at the coordinates
     * on the date
     */
    fun setAstronomicalDusk(
        coordinates: Coordinates,
        date: LocalDate,
        astronomicalDusk: LocalTime?
    ) {
        astronomicalDusks[Pair(coordinates, date)] = astronomicalDusk
    }

    /**
     * Sets the civil dawn time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param civilDawn Time to set or `null` if no such time should exist at the coordinates on the
     * date
     */
    fun setCivilDawn(coordinates: Coordinates, date: LocalDate, civilDawn: LocalTime?) {
        civilDawns[Pair(coordinates, date)] = civilDawn
    }

    /**
     * Sets the civil dusk time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param civilDusk Time to set or `null` if no such time should exist at the coordinates on the
     * date
     */
    fun setCivilDusk(coordinates: Coordinates, date: LocalDate, civilDusk: LocalTime?) {
        civilDusks[Pair(coordinates, date)] = civilDusk
    }

    /**
     * Sets the nautical dawn time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param nauticalDawn Time to set or `null` if no such time should exist at the coordinates on
     * the date
     */
    fun setNauticalDawn(
        coordinates: Coordinates,
        date: LocalDate,
        nauticalDawn: LocalTime?
    ) {
        nauticalDawns[Pair(coordinates, date)] = nauticalDawn
    }

    /**
     * Sets the nautical dusk time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param nauticalDusk Time to set or `null` if no such time should exist at the coordinates on
     * the date
     */
    fun setNauticalDusk(
        coordinates: Coordinates,
        date: LocalDate,
        nauticalDusk: LocalTime?
    ) {
        nauticalDusks[Pair(coordinates, date)] = nauticalDusk
    }

    /**
     * Sets the sunrise time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param sunrise Time to set or `null` if no such time should exist at the coordinates on the
     * date
     */
    fun setSunrise(coordinates: Coordinates, date: LocalDate, sunrise: LocalTime?) {
        sunrises[Pair(coordinates, date)] = sunrise
    }

    /**
     * Sets the sunset time for some given coordinates and a given date
     *
     * @param coordinates Coordinates to set the time for
     * @param date Date to set the time for
     * @param sunset Time to set or `null` if no such time should exist at the coordinates on the
     * date
     */
    fun setSunset(coordinates: Coordinates, date: LocalDate, sunset: LocalTime?) {
        sunsets[Pair(coordinates, date)] = sunset
    }
}
