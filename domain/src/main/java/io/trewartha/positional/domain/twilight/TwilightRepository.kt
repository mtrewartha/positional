package io.trewartha.positional.domain.twilight

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Repository for twilights relevant to specific dates at specific locations
 */
interface TwilightRepository {

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

    /**
     * Gets the local time of morning civil twilight (i.e. civil dawn) given a date at a latitude
     * and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of morning civil twilight (i.e. civil dawn) or `null` if there is none
     * on the given date at the given location
     */
    fun getMorningCivilTwilight(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of evening civil twilight (i.e. civil dusk) given a date at a latitude
     * and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of evening civil twilight (i.e. civil dusk) or `null` if there is none
     * on the given date at the given location
     */
    fun getEveningCivilTwilight(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of morning nautical twilight (i.e. nautical dawn) given a date at a
     * latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of morning nautical twilight (i.e. nautical dawn) or `null` if there
     * is none on the given date at the given location
     */
    fun getMorningNauticalTwilight(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of evening nautical twilight (i.e. nautical dusk) given a date at a
     * latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of evening nautical twilight (i.e. nautical dusk) or `null` if there
     * is none on the given date at the given location
     */
    fun getEveningNauticalTwilight(date: LocalDate, latitude: Double, longitude: Double): LocalTime?

    /**
     * Gets the local time of morning astronomical twilight (i.e. astronomical dawn) given a date at
     * a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of morning astronomical twilight (i.e. astronomical dawn) or `null` if
     * there is none on the given date at the given location
     */
    fun getMorningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime?

    /**
     * Gets the local time of evening astronomical twilight (i.e. astronomical dusk) given a date at
     * a latitude and longitude
     *
     * @param date the local date at the location denoted by [latitude] and [longitude]
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     *
     * @return the local time of evening astronomical twilight (i.e. astronomical dusk) or `null` if
     * there is none on the given date at the given location
     */
    fun getEveningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime?
}
