package io.trewartha.positional.ui.utils.format

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.R
import java.text.DateFormat.SHORT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.datetime.Instant

class AndroidDateTimeFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
) : DateTimeFormatter {

    private val timeFormatKey by lazy { context.getString(R.string.settings_time_format_key) }
    private val timeFormatValue12Hour by lazy {
        context.getString(R.string.settings_time_format_12hr_value)
    }
    private val simpleDateFormat by lazy { SimpleDateFormat.getDateInstance() }
    private val timeFormat12Hour by lazy { SimpleDateFormat.getTimeInstance(SHORT) }
    private val timeFormat12HourWithSeconds by lazy { SimpleDateFormat.getTimeInstance() }
    private val timeFormat24Hour by lazy { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    private val timeFormat24HourWithSeconds by lazy {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    }

    override fun getFormattedDate(instant: Instant?): String? = if (instant == null) {
        context.getString(R.string.sun_date_unknown)
    } else {
        simpleDateFormat.format(Date(instant.toEpochMilliseconds()))
    }

    override fun getFormattedTime(instant: Instant, includeSeconds: Boolean): String = when {
        use12HourTime() -> if (includeSeconds) {
            timeFormat12HourWithSeconds.format(Date(instant.toEpochMilliseconds()))
        } else {
            timeFormat12Hour.format(Date(instant.toEpochMilliseconds()))
        }
        else -> if (includeSeconds) {
            timeFormat24HourWithSeconds.format(Date(instant.toEpochMilliseconds()))
        } else {
            timeFormat24Hour.format(Date(instant.toEpochMilliseconds()))
        }
    }

    private fun use12HourTime(): Boolean =
        sharedPreferences.getString(timeFormatKey, timeFormatValue12Hour) == timeFormatValue12Hour
}