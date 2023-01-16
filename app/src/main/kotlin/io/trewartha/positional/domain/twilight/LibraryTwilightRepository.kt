package io.trewartha.positional.domain.twilight

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalTime

class LibraryTwilightRepository : TwilightRepository {

    override fun getSunrise(date: LocalDate, latitude: Double, longitude: Double): Instant? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunriseForDate(date.toCalendar())
            .asInstant(date)

    override fun getSunset(date: LocalDate, latitude: Double, longitude: Double): Instant? =
        createLibraryCalculator(latitude, longitude)
            .getOfficialSunsetForDate(date.toCalendar())
            .asInstant(date)

    override fun getMorningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getCivilSunriseForDate(date.toCalendar())
        .asInstant(date)

    override fun getEveningCivilTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getCivilSunsetForDate(date.toCalendar())
        .asInstant(date)

    override fun getMorningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunriseForDate(date.toCalendar())
        .asInstant(date)

    override fun getEveningNauticalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getNauticalSunsetForDate(date.toCalendar())
        .asInstant(date)

    override fun getMorningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunriseForDate(date.toCalendar())
        .asInstant(date)

    override fun getEveningAstronomicalTwilight(
        date: LocalDate,
        latitude: Double,
        longitude: Double
    ): Instant? = createLibraryCalculator(latitude, longitude)
        .getAstronomicalSunsetForDate(date.toCalendar())
        .asInstant(date)

    private fun createLibraryCalculator(latitude: Double, longitude: Double) =
        SunriseSunsetCalculator(Location(latitude, longitude), java.util.TimeZone.getDefault())

    private fun String.asInstant(localDate: LocalDate): Instant? = takeIf { it != NO_TIME }
        ?.toLocalTime()
        ?.atDate(localDate)
        ?.toInstant(TimeZone.currentSystemDefault())

    private companion object {
        private const val NO_TIME = "00:00"
    }
}
