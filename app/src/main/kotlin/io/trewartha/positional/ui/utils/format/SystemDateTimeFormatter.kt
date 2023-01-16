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
import java.util.Date
import javax.inject.Inject

class SystemDateTimeFormatter @Inject constructor() : DateTimeFormatter {

    private val simpleDateFormat =
        SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
    private val simpleDateTimeFormat =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.SHORT)
    private val simpleTimeFormat =
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    private val simpleTimeFormatWithSeconds =
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM)

    override fun formatDate(localDate: LocalDate): String =
        simpleDateFormat.format(localDate.toJavaDate())

    override fun formatDateTime(localDateTime: LocalDateTime): String =
        simpleDateTimeFormat.format(localDateTime.toJavaDate())

    override fun formatTime(localTime: LocalTime, includeSeconds: Boolean): String {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val javaDate = localTime.atDate(today).toInstant(timeZone).toJavaDate()
        return if (includeSeconds) {
            simpleTimeFormatWithSeconds.format(javaDate)
        } else {
            simpleTimeFormat.format(javaDate)
        }
    }

    private fun LocalDate.toInstant(): Instant =
        atTime(0, 0).toInstant(TimeZone.currentSystemDefault())

    private fun LocalDate.toJavaDate(): Date = toInstant().toJavaDate()

    private fun LocalDateTime.toJavaDate(): Date =
        toInstant(TimeZone.currentSystemDefault()).toJavaDate()

    private fun Instant.toJavaDate(): Date = Date(toEpochMilliseconds())
}
