package io.trewartha.positional.core.ui.format

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.trewartha.positional.AppScope
import java.text.SimpleDateFormat
import java.text.SimpleDateFormat.MEDIUM
import java.text.SimpleDateFormat.SHORT
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

@ContributesBinding(AppScope::class)
@Inject
internal class SystemDateTimeFormatter(
    private val clock: Clock,
    locale: Locale,
    private val timeZone: TimeZone,
) : DateTimeFormatter {

    private val javaTimeZone = java.util.TimeZone.getTimeZone(timeZone.toJavaZoneId())
    private val dateFormat = SimpleDateFormat.getDateInstance(MEDIUM, locale)
        .also { it.timeZone = javaTimeZone }
    private val dateTimeFormat = SimpleDateFormat.getDateTimeInstance(MEDIUM, SHORT, locale)
        .also { it.timeZone = javaTimeZone }
    private val fullDayOfWeekFormat = SimpleDateFormat("EEEE", locale)
        .also { it.timeZone = javaTimeZone }
    private val timeFormat = SimpleDateFormat.getTimeInstance(SHORT, locale)
        .also { it.timeZone = javaTimeZone }
    private val timeFormatWithSeconds = SimpleDateFormat.getTimeInstance(MEDIUM, locale)
        .also { it.timeZone = javaTimeZone }

    override fun formatDate(localDate: LocalDate): String =
        dateFormat.format(localDate.toJavaDate())

    override fun formatDateTime(localDateTime: LocalDateTime): String =
        dateTimeFormat.format(localDateTime.toJavaDate())

    override fun formatFullDayOfWeek(localDate: LocalDate): String =
        fullDayOfWeekFormat.format(localDate.toJavaDate())

    override fun formatTime(localTime: LocalTime, includeSeconds: Boolean): String {
        val today = clock.now().toLocalDateTime(timeZone).date
        val javaDate = localTime.atDate(today).toInstant(timeZone).toJavaDate()
        return if (includeSeconds) {
            timeFormatWithSeconds.format(javaDate)
        } else {
            timeFormat.format(javaDate)
        }
    }

    private fun LocalDate.toInstant(): Instant =
        atTime(0, 0).toInstant(timeZone)

    private fun LocalDate.toJavaDate(): Date = toInstant().toJavaDate()

    private fun LocalDateTime.toJavaDate(): Date =
        toInstant(timeZone).toJavaDate()

    private fun Instant.toJavaDate(): Date = Date(toEpochMilliseconds())
}
