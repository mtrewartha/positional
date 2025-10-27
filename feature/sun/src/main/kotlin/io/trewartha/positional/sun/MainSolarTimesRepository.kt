package io.trewartha.positional.sun

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import io.trewartha.positional.core.measurement.Coordinates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

/**
 * [SolarTimesRepository] implementation powered by a library
 */
internal class MainSolarTimesRepository @Inject constructor() : SolarTimesRepository {

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
            coordinates.asGeodeticCoordinates().let {
                Location(it.latitude.inDegrees().magnitude, it.longitude.inDegrees().magnitude)
            },
            TimeZone.getDefault()
        )

    private fun String.asLocalTime(): LocalTime? = takeIf { it != NO_TIME }?.let(LocalTime::parse)
}

private fun LocalDate.toCalendar(): Calendar =
    Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_YEAR, dayOfYear)
    }

private const val NO_TIME = "99:99"
