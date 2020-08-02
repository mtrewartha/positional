package io.trewartha.positional.location

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.core.os.LocaleListCompat
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.CoordinatesFormat
import io.trewartha.positional.domain.entities.CoordinatesFormat.*
import io.trewartha.positional.domain.entities.Units
import io.trewartha.positional.ui.utils.DateTimeFormatter
import org.threeten.bp.Instant
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class LocationFormatter(
        private val context: Context
) {

    private val dateTimeFormatter by lazy { DateTimeFormatter(context) }
    private val formatAccuracy by lazy { context.getString(R.string.location_accuracy) }
    private val formatAccuracyDistanceImperial by lazy { context.getString(R.string.location_accuracy_imperial) }
    private val formatAccuracyDistanceMetric by lazy { context.getString(R.string.location_accuracy_metric) }
    private val formatBearing by lazy { context.getString(R.string.location_bearing) }
    private val formatCoordinateDecimal by lazy { context.getString(R.string.location_coordinate_decimal) }
    private val formatCoordinateUtm by lazy { context.getString(R.string.location_coordinate_utm) }
    private val formatElevationImperial by lazy { context.getString(R.string.location_elevation_imperial) }
    private val formatElevationMetric by lazy { context.getString(R.string.location_elevation_metric) }
    private val formatSpeedImperial by lazy { context.getString(R.string.location_speed_imperial) }
    private val formatSpeedMetric by lazy { context.getString(R.string.location_speed_metric) }
    private val formatUpdatedAt by lazy { context.getString(R.string.location_updated_at) }
    private val locale: Locale
        get() = LocaleListCompat.getDefault()[0] ?: Locale.US
    private val locationValueUnknown by lazy { context.getString(R.string.location_value_unknown) }
    private val numberFormat = NumberFormat.getNumberInstance(locale)
            .apply {
                roundingMode = RoundingMode.HALF_UP
                maximumFractionDigits = 0
                minimumFractionDigits = 0
            }
    private val prefs = context.getSharedPreferences(
            context.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
    )
    private val prefsShowAccuracies: Boolean
        get() = prefs.getBoolean(context.getString(R.string.settings_show_accuracies_key), true)

    fun getBearing(location: Location?): String {
        if (location == null || !location.hasBearing()) return locationValueUnknown

        val bearing = location.bearing.toInt()
        return String.format(locale, formatBearing, bearing)
    }

    fun getBearingAccuracy(location: Location?): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !prefsShowAccuracies)
            return null

        if (location == null
                || !location.hasBearingAccuracy()
                || location.bearingAccuracyDegrees == 0f)
            return locationValueUnknown

        val accuracy = numberFormat.format(location.bearingAccuracyDegrees)
        return String.format(locale, formatAccuracy, accuracy)
    }

    fun getElevation(location: Location?, units: Units): String {
        if (location == null || !location.hasAltitude()) return locationValueUnknown

        val elevation = when (units) {
            Units.IMPERIAL -> DistanceUtils.metersToFeet(location.altitude.toFloat()).toInt()
            Units.METRIC -> location.altitude.toInt()
        }
        val format = when (units) {
            Units.IMPERIAL -> formatElevationImperial
            Units.METRIC -> formatElevationMetric
        }
        return String.format(locale, format, elevation)
    }

    fun getElevationAccuracy(location: Location?, units: Units?): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !prefsShowAccuracies)
            return null

        if (location == null
                || !location.hasVerticalAccuracy()
                || location.verticalAccuracyMeters == 0f
                || units == null)
            return locationValueUnknown

        val accuracy = numberFormat.format(location.verticalAccuracyMeters.let {
            when (units) {
                Units.IMPERIAL -> DistanceUtils.metersToFeet(it)
                Units.METRIC -> it
            }
        })
        return String.format(locale, formatAccuracy, accuracy)
    }

    fun getCoordinates(location: Location, format: CoordinatesFormat): Pair<String, Int> {
        return when (format) {
            DD -> getDDCoords(location.latitude, location.longitude)
            DDM -> getDdmCoords(location.latitude, location.longitude)
            DMS -> getDmsCoords(location.latitude, location.longitude)
            MGRS -> getMgrsCoords(location.latitude, location.longitude)
            UTM -> getUtmCoords(location.latitude, location.longitude)
        }
    }

    fun getCoordinatesAccuracy(location: Location?, units: Units): String {
        if (location == null || !location.hasAccuracy()) return locationValueUnknown
        val accuracy = numberFormat.format(location.accuracy.let {
            when (units) {
                Units.IMPERIAL -> DistanceUtils.metersToFeet(it).toInt()
                Units.METRIC -> it.toInt()
            }
        })
        val format = when (units) {
            Units.IMPERIAL -> formatAccuracyDistanceImperial
            Units.METRIC -> formatAccuracyDistanceMetric
        }
        return String.format(locale, format, accuracy)
    }

    fun getSpeed(location: Location?, units: Units?): String? {
        if (location == null || !location.hasSpeed() || location.speed == 0f || units == null)
            return null

        val speed = numberFormat.format(location.speed.let {
            when (units) {
                Units.IMPERIAL -> DistanceUtils.metersPerSecondToMilesPerHour(it)
                Units.METRIC -> DistanceUtils.metersPerSecondToKilometersPerHour(it)
            }
        })
        val format = when (units) {
            Units.IMPERIAL -> formatSpeedImperial
            Units.METRIC -> formatSpeedMetric
        }
        return String.format(locale, format, speed)
    }

    fun getSpeedAccuracy(location: Location?, units: Units?): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !prefsShowAccuracies)
            return null

        if (location == null
                || !location.hasSpeedAccuracy()
                || location.speedAccuracyMetersPerSecond == 0f
                || units == null)
            return locationValueUnknown

        val accuracy = numberFormat.format(location.speedAccuracyMetersPerSecond.let {
            when (units) {
                Units.IMPERIAL -> DistanceUtils.metersPerSecondToMilesPerHour(it)
                Units.METRIC -> DistanceUtils.metersPerSecondToKilometersPerHour(it)
            }
        })
        return String.format(locale, formatAccuracy, accuracy)
    }

    fun getTimestamp(location: Location?): String {
        return if (location == null || location.time == 0L) {
            locationValueUnknown
        } else {
            dateTimeFormatter.getFormattedTime(Instant.ofEpochMilli(location.time), true)
                    ?.let { String.format(formatUpdatedAt, it) }
                    ?: locationValueUnknown
        }
    }

    private fun getDDCoords(lat: Double, lon: Double): Pair<String, Int> {
        val formattedLat = String.format(locale, formatCoordinateDecimal, lat)
        val formattedLon = String.format(locale, formatCoordinateDecimal, lon)
        val maxLength = maxOf(formattedLat.length, formattedLon.length)
        return Pair("${formattedLat.padStart(maxLength)}\n${formattedLon.padStart(maxLength)}", 2)
    }

    private fun getDdmCoords(lat: Double, lon: Double): Pair<String, Int> {
        val formattedLat = getDdmLat(lat)
        val formattedLon = getDdmLon(lon)
        val maxLength = maxOf(formattedLat.length, formattedLon.length)
        return Pair("${formattedLat.padStart(maxLength)}\n${formattedLon.padStart(maxLength)}", 2)
    }

    private fun getDdmLat(lat: Double): String {
        val ddmLat = replaceDelimiters(Location.convert(lat, Location.FORMAT_MINUTES))
        return if (lat >= 0.0) {
            "$ddmLat N"
        } else {
            "${ddmLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun getDdmLon(lon: Double): String {
        val ddmLon = replaceDelimiters(Location.convert(lon, Location.FORMAT_MINUTES))
        return if (lon >= 0.0) {
            "$ddmLon E"
        } else {
            "${ddmLon.replaceFirst("-".toRegex(), "")} W"
        }
    }

    private fun getDmsCoords(lat: Double, lon: Double): Pair<String, Int> {
        val formattedLat = getDmsLat(lat)
        val formattedLon = getDmsLon(lon)
        val maxLength = maxOf(formattedLat.length, formattedLon.length)
        return Pair("${formattedLat.padStart(maxLength)}\n${formattedLon.padStart(maxLength)}", 2)
    }

    private fun getDmsLat(lat: Double): String {
        val dmsLat = replaceDelimiters(Location.convert(lat, Location.FORMAT_SECONDS))
        return if (lat >= 0.0) {
            "$dmsLat N"
        } else {
            "${dmsLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun getDmsLon(lon: Double): String {
        val dmsLon = replaceDelimiters(Location.convert(lon, Location.FORMAT_SECONDS))
        return if (lon >= 0.0) {
            "$dmsLon E"
        } else {
            "${dmsLon.replaceFirst("-".toRegex(), "")} W"
        }
    }

    private fun getUtmCoord(lat: Double, lon: Double): UTMCoord {
        return UTMCoord.fromLatLon(
                Angle.fromDegreesLatitude(lat),
                Angle.fromDegreesLongitude(lon)
        )
    }

    private fun getMgrsCoords(lat: Double, lon: Double): Pair<String, Int> {
        return Pair(
                MGRSCoord.fromLatLon(
                        Angle.fromDegreesLatitude(lat),
                        Angle.fromDegreesLongitude(lon)
                ).toString().replace(' ', '\n'),
                3
        )
    }

    private fun getUtmCoords(lat: Double, lon: Double): Pair<String, Int> {
        return Pair(
                "${getUtmZone(lat, lon)}\n${getUtmEasting(lat, lon)}\n${getUtmNorthing(lat, lon)}",
                3
        )
    }

    private fun getUtmEasting(lat: Double, lon: Double): String {
        return "${String.format(
                locale,
                formatCoordinateUtm,
                getUtmCoord(lat, lon).easting
        )}m E"
    }

    private fun getUtmLatBand(lat: Double): String {
        return when {
            -80.0 <= lat && lat < -72.0 -> "C"
            -72.0 <= lat && lat < -64.0 -> "D"
            -72.0 <= lat && lat < -56.0 -> "E"
            -72.0 <= lat && lat < -48.0 -> "F"
            -72.0 <= lat && lat < -40.0 -> "G"
            -72.0 <= lat && lat < -32.0 -> "H"
            -72.0 <= lat && lat < -24.0 -> "J"
            -72.0 <= lat && lat < -16.0 -> "K"
            -72.0 <= lat && lat < -8.0 -> "L"
            -72.0 <= lat && lat < 0.0 -> "M"
            00.0 <= lat && lat < 08.0 -> "N"
            08.0 <= lat && lat < 16.0 -> "P"
            16.0 <= lat && lat < 24.0 -> "Q"
            24.0 <= lat && lat < 32.0 -> "R"
            32.0 <= lat && lat < 40.0 -> "S"
            40.0 <= lat && lat < 48.0 -> "T"
            48.0 <= lat && lat < 56.0 -> "U"
            56.0 <= lat && lat < 64.0 -> "V"
            64.0 <= lat && lat < 72.0 -> "W"
            72.0 <= lat && lat < 84.0 -> "X"
            else -> ""
        }
    }

    private fun getUtmNorthing(lat: Double, lon: Double): String {
        val utmCoordinate = getUtmCoord(lat, lon)
        return "${String.format(
                locale,
                formatCoordinateUtm,
                utmCoordinate.northing
        )}m N"
    }

    private fun getUtmZone(lat: Double, lon: Double): String {
        return "${getUtmCoord(lat, lon).zone}${getUtmLatBand(lat)}"
    }

    private fun replaceDelimiters(string: String): String {
        return string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
    }
}
