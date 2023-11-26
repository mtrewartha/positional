package io.trewartha.positional.data.sun

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar

internal fun LocalDate.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_YEAR, dayOfYear)
    }
}

internal fun Calendar.toLocalTime(): LocalTime =
    LocalTime(get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE), get(Calendar.SECOND))
