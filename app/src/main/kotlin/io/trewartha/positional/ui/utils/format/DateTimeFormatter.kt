package io.trewartha.positional.ui.utils.format

import kotlinx.datetime.Instant

interface DateTimeFormatter {

    fun formatDate(instant: Instant): String

    fun formatDateTime(instant: Instant): String

    fun formatTime(instant: Instant, includeSeconds: Boolean = false): String
}
