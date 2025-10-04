package io.trewartha.positional.sun

import io.trewartha.positional.core.measurement.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Repository for solar times relevant to specific dates at specific locations
 */
public interface SolarTimesRepository {

    /**
     * Gets the local time of astronomical dawn at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of astronomical dawn or `null` if there is none at the given place on
     * the given date
     */
    public fun getAstronomicalDawn(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of astronomical dusk at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of astronomical dusk or `null` if there is none at the given place on
     * the given date
     */
    public fun getAstronomicalDusk(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of civil dawn at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of civil dawn or `null` if there is none at the given place on the
     * given date
     */
    public fun getCivilDawn(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of civil dusk at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of civil dusk or `null` if there is none at the given place on the
     * given date
     */
    public fun getCivilDusk(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of nautical dawn at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of nautical dawn or `null` if there is none at the given place on the
     * given date
     */
    public fun getNauticalDawn(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of nautical dusk at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of nautical dusk or `null` if there is none at the given place on the
     * given date
     */
    public fun getNauticalDusk(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of sunrise at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of sunrise or `null` if there is none at the given place on the given
     * date
     */
    public fun getSunrise(coordinates: Coordinates, date: LocalDate): LocalTime?

    /**
     * Gets the local time of sunset at a given place on a given date
     *
     * @param coordinates Coordinates of the place
     * @param date Local date at the place
     *
     * @return the local time of sunset or `null` if there is none at the given place on the given
     * date
     */
    public fun getSunset(coordinates: Coordinates, date: LocalDate): LocalTime?
}
