package io.trewartha.positional.ui.utils.format

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.text.SimpleDateFormat.MEDIUM
import java.text.SimpleDateFormat.SHORT
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SystemDateTimeFormatter @Inject constructor(locale: Locale) : DateTimeFormatter {

    private val dateFormat = SimpleDateFormat.getDateInstance(MEDIUM)
    private val dateTimeFormat = SimpleDateFormat.getDateTimeInstance(MEDIUM, SHORT)
    private val fullDayOfWeekFormat = SimpleDateFormat("EEEE", locale)
    private val timeFormat = SimpleDateFormat.getTimeInstance(SHORT)
    private val timeFormatWithSeconds = SimpleDateFormat.getTimeInstance(MEDIUM)

    override fun formatDate(localDate: LocalDate): String =
        dateFormat.format(localDate.toJavaDate())

    override fun formatDateTime(localDateTime: LocalDateTime): String =
        dateTimeFormat.format(localDateTime.toJavaDate())

    override fun formatFullDayOfWeek(localDate: LocalDate): String =
        fullDayOfWeekFormat.format(localDate.toJavaDate())

    override fun formatTime(localTime: LocalTime, includeSeconds: Boolean): String {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val javaDate = localTime.atDate(today).toInstant(timeZone).toJavaDate()
        return if (includeSeconds) {
            timeFormatWithSeconds.format(javaDate)
        } else {
            timeFormat.format(javaDate)
        }
    }

    private fun LocalDate.toInstant(): Instant =
        atTime(0, 0).toInstant(TimeZone.currentSystemDefault())

    private fun LocalDate.toJavaDate(): Date = toInstant().toJavaDate()

    private fun LocalDateTime.toJavaDate(): Date =
        toInstant(TimeZone.currentSystemDefault()).toJavaDate()

    private fun Instant.toJavaDate(): Date = Date(toEpochMilliseconds())
}
