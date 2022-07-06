package io.trewartha.positional.ui.utils.format

import kotlinx.datetime.Instant

interface DateTimeFormatter {
    fun getFormattedDate(instant: Instant?): String?
    fun getFormattedTime(instant: Instant, includeSeconds: Boolean = false): String
}