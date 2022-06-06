package io.trewartha.positional.ui.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.R
import org.threeten.bp.Instant
import java.text.DateFormat.SHORT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DateTimeFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {

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

    fun getFormattedDate(instant: Instant?): String? = if (instant == null) {
        context.getString(R.string.sun_date_unknown)
    } else {
        simpleDateFormat.format(Date(instant.toEpochMilli()))
    }

    fun getFormattedTime(instant: Instant, includeSeconds: Boolean = false): String = when {
        use12HourTime() -> if (includeSeconds) {
            timeFormat12HourWithSeconds.format(Date(instant.toEpochMilli()))
        } else {
            timeFormat12Hour.format(Date(instant.toEpochMilli()))
        }
        else -> if (includeSeconds) {
            timeFormat24HourWithSeconds.format(Date(instant.toEpochMilli()))
        } else {
            timeFormat24Hour.format(Date(instant.toEpochMilli()))
        }
    }

    private fun use12HourTime(): Boolean =
        sharedPreferences.getString(timeFormatKey, timeFormatValue12Hour) == timeFormatValue12Hour
}