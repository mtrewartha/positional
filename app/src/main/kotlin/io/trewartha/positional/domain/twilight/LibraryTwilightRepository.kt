package io.trewartha.positional.domain.twilight

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalTime

class LibraryTwilightRepository : TwilightRepository {

    override fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunriseForDate(date.toCalendar())
            .asLocalTime()

    override fun getSunset(date: LocalDate, latitude: Double, longitude: Double): LocalTime? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunsetForDate(date.toCalendar())
            .asLocalTime()

    override fun getMorningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getCivilSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getEveningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getCivilSunsetForDate(date.toCalendar())
        .asLocalTime()

    override fun getMorningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getEveningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunsetForDate(date.toCalendar())
        .asLocalTime()

    override fun getMorningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunriseForDate(date.toCalendar())
        .asLocalTime()

    override fun getEveningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): LocalTime? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunsetForDate(date.toCalendar())
        .asLocalTime()

    private fun createLibraryCalculator(latitude: Double, longitude: Double) =
        SunriseSunsetCalculator(Location(latitude, longitude), java.util.TimeZone.getDefault())

    private fun String.asLocalTime(): LocalTime? = takeIf { it != NO_TIME }?.toLocalTime()

    private companion object {
        private const val NO_TIME = "00:00"
    }
}
