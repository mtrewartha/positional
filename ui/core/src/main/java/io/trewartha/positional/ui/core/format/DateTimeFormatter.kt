package io.trewartha.positional.ui.core.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

interface DateTimeFormatter {

    fun formatDate(localDate: LocalDate): String

    fun formatDateTime(localDateTime: LocalDateTime): String

    fun formatFullDayOfWeek(localDate: LocalDate): String

    fun formatTime(localTime: LocalTime, includeSeconds: Boolean = false): String
}
