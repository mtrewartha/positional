package io.trewartha.positional.domain.solunar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Repository for solar times relevant to specific dates at specific locations
 */
interface SolarTimesRepository {

    /**
     * Gets the local time of astronomical dawn given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of astronomical dawn or `null` if there is none on the given date at
     * the given location
     */
    fun getAstronomicalDawn(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of astronomical dusk given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of astronomical dusk or `null` if there is none on the given date at
     * the given location
     */
    fun getAstronomicalDusk(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of civil dawn given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of civil dawn or `null` if there is none on the given date at the
     * given location
     */
    fun getCivilDawn(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of civil dusk given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of civil dusk or `null` if there is none on the given date at the
     * given location
     */
    fun getCivilDusk(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of nautical dawn given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of nautical dawn or `null` if there is none on the given date at the
     * given location
     */
    fun getNauticalDawn(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of nautical dusk given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of nautical dusk or `null` if there is none on the given date at the
     * given location
     */
    fun getNauticalDusk(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of sunrise given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of sunrise or `null` if there is none on the given date at the given
     * location
     */
    fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of sunset given a date at a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of sunset or `null` if there is none on the given date at the given
     * location
     */
    fun getSunset(date: LocalDate, latitude: Double, longitude: Double): LocalTime?
}
