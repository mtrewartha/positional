package io.trewartha.positional.data.sun

import kotlinx.datetime.LocalDate
import java.util.Calendar

internal fun LocalDate.toCalendar(): Calendar =
    Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.DAY_OF_YEAR, dayOfYear)
    }
