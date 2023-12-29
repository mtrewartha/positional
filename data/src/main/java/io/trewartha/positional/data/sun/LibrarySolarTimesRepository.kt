package io.trewartha.positional.data.sun

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import io.trewartha.positional.data.location.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalTime
import java.util.Calendar
import javax.inject.Inject

/**
 * Library-based implementation of a [SolarTimesRepository]
 */
class LibrarySolarTimesRepository @Inject constructor() : SolarTimesRepository {

    override fun getAstronomicalDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getAstronomicalSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getAstronomicalDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getAstronomicalSunsetForDate(date.toCalendar())
            .asLocalTime()

    override fun getCivilDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getCivilSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getCivilDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getCivilSunsetForDate(date.toCalendar())
            .asLocalTime()

    override fun getNauticalDawn(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getNauticalSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getNauticalDusk(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getNauticalSunsetForDate(date.toCalendar())
            .asLocalTime()

    override fun getSunrise(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getOfficialSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getSunset(coordinates: Coordinates, date: LocalDate): LocalTime? =
        createLibraryCalculator(coordinates)
            .getOfficialSunsetForDate(date.toCalendar())
            .asLocalTime()

    private fun createLibraryCalculator(coordinates: Coordinates) =
        SunriseSunsetCalculator(
            Location(coordinates.latitude, coordinates.longitude),
            java.util.TimeZone.getDefault()
        )

    private fun String.asLocalTime(): LocalTime? = takeIf { it != NO_TIME }?.toLocalTime()
}

private fun LocalDate.toCalendar(): Calendar =
    Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_YEAR, dayOfYear)
    }

private const val NO_TIME = "99:99"
