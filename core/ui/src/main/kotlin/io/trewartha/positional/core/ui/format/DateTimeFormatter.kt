package io.trewartha.positional.core.ui.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

/**
 * Formats dates and times for display to the user
 */
public interface DateTimeFormatter {

    /**
     * Formats [localDate] as a locale-appropriate date
     */
    public fun formatDate(localDate: LocalDate): String

    /**
     * Formats [localDateTime] as a locale-appropriate date and time
     */
    public fun formatDateTime(localDateTime: LocalDateTime): String

    /**
     * Formats the full name of the day of the week of [localDate]
     */
    public fun formatFullDayOfWeek(localDate: LocalDate): String

    /**
     * Formats [localTime] as a locale-appropriate time, including seconds if [includeSeconds] is true
     */
    public fun formatTime(localTime: LocalTime, includeSeconds: Boolean = false): String
}
