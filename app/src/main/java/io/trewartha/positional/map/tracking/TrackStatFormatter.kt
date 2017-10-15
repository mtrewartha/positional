package io.trewartha.positional.map.tracking

import android.annotation.SuppressLint
import android.content.Context
import io.trewartha.positional.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class TrackStatFormatter(
        private val context: Context
) {

    companion object {

        @SuppressLint("SimpleDateFormat") val DATE_FORMAT = SimpleDateFormat("M/d/yy")
        @SuppressLint("SimpleDateFormat") val TIME_FORMAT = SimpleDateFormat("h:mm a")

        init {
            val symbols = DateFormatSymbols(Locale.getDefault())
            symbols.amPmStrings = arrayOf("am", "pm")
            TIME_FORMAT.dateFormatSymbols = symbols
        }
    }

//    fun getAltitude(meters: Double): String {
//        return context.getString(R.string.altitude, UnitConverter.getFeet(meters.toFloat()), context.getString(R.string.unit_feet))
//    }
//
//    fun getCoordinates(latitude: Double, longitude: Double): String {
//        return context.getString(R.string.coordinates, latitude, longitude)
//    }
//
//    fun getDistance(meters: Float): String {
//        return context.getString(R.string.distance, UnitConverter.getMiles(meters / UnitConverter.M_PER_KM), context.getString(R.string.unit_miles))
//    }
//
//    fun getTimestamp(timestampInMillis: Long): String {
//        val timestampInSeconds = TimeUtils.timeInSecondsFromTimeInMilliseconds(timestampInMillis)
//        val date = TimeUtils.getUTCDateForTimestamp(timestampInSeconds)
//        return context.getString(R.string.timestamp, TIME_FORMAT.format(date), DATE_FORMAT.format(date))
//    }
//
//    fun getDuration(durationInMillis: Long): String {
//        return context.getString(R.string.duration, TimeUtils.getHours(durationInMillis), TimeUtils.getMinutes(durationInMillis), TimeUtils.getSeconds(durationInMillis))
//    }
//
//    fun getSpeed(metersPerSecond: Float): String {
//        val kilometersPerHour = metersPerSecond * UnitConverter.SEC_PER_MIN * UnitConverter.MIN_PER_HR / UnitConverter.M_PER_KM
//        return context.getString(R.string.speed, UnitConverter.getMPH(kilometersPerHour), context.getString(R.string.unit_miles_per_hour))
//    }
}