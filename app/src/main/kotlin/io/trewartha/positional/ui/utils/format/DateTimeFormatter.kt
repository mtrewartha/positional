package io.trewartha.positional.ui.utils.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

interface DateTimeFormatter {

    fun formatDate(localDate: LocalDate): String

    fun formatDateTime(localDateTime: LocalDateTime): String

    fun formatTime(localTime: LocalTime, includeSeconds: Boolean = false): String
}
