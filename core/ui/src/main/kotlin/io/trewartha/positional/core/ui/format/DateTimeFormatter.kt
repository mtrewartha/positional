package io.trewartha.positional.core.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

public interface DateTimeFormatter {

    public fun formatDate(localDate: LocalDate): String

    public fun formatDateTime(localDateTime: LocalDateTime): String

    public fun formatFullDayOfWeek(localDate: LocalDate): String

    public fun formatTime(localTime: LocalTime, includeSeconds: Boolean = false): String
}
