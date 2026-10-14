package io.trewartha.positional.core.ui.format

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.trewartha.positional.AppScope
import java.text.DateFormat
import java.text.DateFormat.MEDIUM
import java.text.DateFormat.SHORT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toLocalDateTime

/**
 * Formats dates and times using the locale and time zone that are current at the time of each call.
 *
 * A new [DateFormat] is created for every call because [DateFormat] instances are not thread-safe
 * and the locale and time zone may change while the process is alive.
 */
@ContributesBinding(AppScope::class)
@Inject
internal class SystemDateTimeFormatter(
    private val clock: Clock,
    private val localeProvider: () -> Locale,
    private val timeZoneProvider: () -> TimeZone,
) : DateTimeFormatter {

    override fun formatDate(localDate: LocalDate): String {
        val timeZone = timeZoneProvider()
        return DateFormat.getDateInstance(MEDIUM, localeProvider())
            .formatIn(timeZone, localDate.atTime(0, 0).toInstant(timeZone))
    }

    override fun formatDateTime(localDateTime: LocalDateTime): String {
        val timeZone = timeZoneProvider()
        return DateFormat.getDateTimeInstance(MEDIUM, SHORT, localeProvider())
            .formatIn(timeZone, localDateTime.toInstant(timeZone))
    }

    override fun formatFullDayOfWeek(localDate: LocalDate): String {
        val timeZone = timeZoneProvider()
        return SimpleDateFormat("EEEE", localeProvider())
            .formatIn(timeZone, localDate.atTime(0, 0).toInstant(timeZone))
    }

    override fun formatTime(localTime: LocalTime, includeSeconds: Boolean): String {
        val timeZone = timeZoneProvider()
        val today = clock.now().toLocalDateTime(timeZone).date
        val style = if (includeSeconds) MEDIUM else SHORT
        return DateFormat.getTimeInstance(style, localeProvider())
            .formatIn(timeZone, localTime.atDate(today).toInstant(timeZone))
    }

    private fun DateFormat.formatIn(timeZone: TimeZone, instant: Instant): String {
        this.timeZone = java.util.TimeZone.getTimeZone(timeZone.toJavaZoneId())
        return format(Date(instant.toEpochMilliseconds()))
    }
}
