package io.trewartha.positional.ui.utils.format

import kotlinx.datetime.Instant
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

    override fun formatDate(instant: Instant): String =
        simpleDateFormat.format(Date(instant.toEpochMilliseconds()))

    override fun formatDateTime(instant: Instant): String =
        simpleDateTimeFormat.format(Date(instant.toEpochMilliseconds()))

    override fun formatTime(instant: Instant, includeSeconds: Boolean): String {
        val date = Date(instant.toEpochMilliseconds())
        return if (includeSeconds) {
            simpleTimeFormatWithSeconds.format(date)
        } else {
            simpleTimeFormat.format(date)
        }
    }
}
