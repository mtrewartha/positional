package io.trewartha.positional.ui.utils.format

import android.content.Context
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.utils.DistanceUtils
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

typealias AndroidLocation = android.location.Location

class AndroidLocationFormatter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dateTimeFormatter: DateTimeFormatter,
) : LocationFormatter {

    private val formatAccuracy by lazy { context.getString(R.string.location_accuracy) }
    private val formatAccuracyDistanceImperial by lazy { context.getString(R.string.location_accuracy_imperial) }
    private val formatAccuracyDistanceMetric by lazy { context.getString(R.string.location_accuracy_metric) }
    private val formatBearing by lazy { context.getString(R.string.location_bearing) }
    private val formatCoordinateDecimal by lazy { context.getString(R.string.location_coordinate_decimal) }
    private val formatCoordinateUtm by lazy { context.getString(R.string.location_coordinate_utm) }
    private val formatAltitudeImperial by lazy { context.getString(R.string.location_altitude_imperial) }
    private val formatAltitudeMetric by lazy { context.getString(R.string.location_altitude_metric) }
    private val formatSpeedImperial by lazy { context.getString(R.string.location_speed_imperial) }
    private val formatSpeedMetric by lazy { context.getString(R.string.location_speed_metric) }
    private val formatUpdatedAt by lazy { context.getString(R.string.location_updated_at) }
    private val locale: Locale
        get() = LocaleListCompat.getDefault()[0] ?: Locale.US
    private val numberFormat = NumberFormat.getNumberInstance(locale).apply {
        roundingMode = RoundingMode.HALF_UP
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    override fun getAltitude(location: Location, units: Units): String =
        location.altitudeMeters.let { altitude ->
            if (altitude == null) context.getString(R.string.common_dash)
            else {
                val altitudeInUnits = when (units) {
                    Units.IMPERIAL -> DistanceUtils.metersToFeet(altitude.toFloat()).toInt()
                    Units.METRIC -> altitude.toInt()
                }
                val format = when (units) {
                    Units.IMPERIAL -> formatAltitudeImperial
                    Units.METRIC -> formatAltitudeMetric
                }
                String.format(locale, format, altitudeInUnits)
            }
        }

    override fun getAltitudeAccuracy(location: Location, units: Units): String =
        location.altitudeAccuracyMeters.let { accuracy ->
            if (accuracy == null) context.getString(R.string.common_dash)
            else {
                val formattedAccuracy = numberFormat.format(accuracy.let {
                    when (units) {
                        Units.IMPERIAL -> DistanceUtils.metersToFeet(it)
                        Units.METRIC -> it
                    }
                })
                return String.format(locale, formatAccuracy, formattedAccuracy)
            }
        }

    override fun getBearing(location: Location): String =
        location.bearingDegrees.let { degrees ->
            if (degrees == null)
                context.getString(R.string.common_dash)
            else {
                val bearing = degrees.toInt()
                String.format(locale, formatBearing, bearing)
            }
        }

    override fun getBearingAccuracy(location: Location): String =
        location.bearingAccuracyDegrees.let { accuracy ->
            if (accuracy == null) context.getString(R.string.common_dash)
            else {
                val numberFormattedAccuracy = numberFormat.format(location.bearingAccuracyDegrees)
                String.format(locale, formatAccuracy, numberFormattedAccuracy)
            }
        }

    override fun getCoordinates(location: Location, format: CoordinatesFormat): Pair<String, Int> =
        when (format) {
            CoordinatesFormat.DD -> getDDCoords(location.latitude, location.longitude)
            CoordinatesFormat.DDM -> getDdmCoords(location.latitude, location.longitude)
            CoordinatesFormat.DMS -> getDmsCoords(location.latitude, location.longitude)
            CoordinatesFormat.MGRS -> getMgrsCoords(location.latitude, location.longitude)
            CoordinatesFormat.UTM -> getUtmCoords(location.latitude, location.longitude)
        }

    override fun getCoordinatesForCopy(location: Location, format: CoordinatesFormat): String =
        when (format) {
            CoordinatesFormat.DD -> getDDCoordsForCopy(location.latitude, location.longitude)
            CoordinatesFormat.DDM -> getDdmCoordsForCopy(location.latitude, location.longitude)
            CoordinatesFormat.DMS -> getDmsCoordsForCopy(location.latitude, location.longitude)
            CoordinatesFormat.MGRS -> getMgrsCoordsForCopy(location.latitude, location.longitude)
            CoordinatesFormat.UTM -> getUtmCoordsForCopy(location.latitude, location.longitude)
        }

    override fun getCoordinatesAccuracy(location: Location, units: Units): String {
        return location.horizontalAccuracyMeters.let { accuracy ->
            if (accuracy == null) context.getString(R.string.common_dash)
            else {
                val numberFormattedAccuracy = numberFormat.format(accuracy.let {
                    when (units) {
                        Units.IMPERIAL -> DistanceUtils.metersToFeet(it).toInt()
                        Units.METRIC -> it.toInt()
                    }
                })
                val format = when (units) {
                    Units.IMPERIAL -> formatAccuracyDistanceImperial
                    Units.METRIC -> formatAccuracyDistanceMetric
                }
                String.format(locale, format, numberFormattedAccuracy)
            }
        }
    }

    override fun getSpeed(location: Location, units: Units): String {
        return location.speedMetersPerSecond.let { speed ->
            if (speed == null) context.getString(R.string.common_dash)
            else {
                val numberFormattedSpeed = numberFormat.format(speed.let {
                    when (units) {
                        Units.IMPERIAL ->
                            DistanceUtils.metersPerSecondToMilesPerHour(it)
                        Units.METRIC ->
                            DistanceUtils.metersPerSecondToKilometersPerHour(it)
                    }
                })
                val format = when (units) {
                    Units.IMPERIAL -> formatSpeedImperial
                    Units.METRIC -> formatSpeedMetric
                }
                String.format(locale, format, numberFormattedSpeed)
            }
        }
    }

    override fun getSpeedAccuracy(location: Location, units: Units): String =
        location.speedAccuracyMetersPerSecond.let { accuracy ->
            if (accuracy == null) context.getString(R.string.common_dash)
            else {
                val numberFormattedAccuracy = numberFormat.format(accuracy.let {
                    when (units) {
                        Units.IMPERIAL ->
                            DistanceUtils.metersPerSecondToMilesPerHour(it)
                        Units.METRIC ->
                            DistanceUtils.metersPerSecondToKilometersPerHour(it)
                    }
                })
                String.format(locale, formatAccuracy, numberFormattedAccuracy)
            }
        }

    override fun getTimestamp(location: Location): String =
        String.format(
            locale,
            formatUpdatedAt,
            dateTimeFormatter.formatTime(
                location.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).time,
                includeSeconds = true
            )
        )

    private fun getDDCoords(lat: Double, lon: Double): Pair<String, Int> {
        val formattedLat = String.format(locale, formatCoordinateDecimal, lat)
        val formattedLon = String.format(locale, formatCoordinateDecimal, lon)
        val maxLength = maxOf(formattedLat.length, formattedLon.length)
        return Pair("${formattedLat.padStart(maxLength)}\n${formattedLon.padStart(maxLength)}", 2)
    }

    private fun getDDCoordsForCopy(lat: Double, lon: Double): String {
        val formattedLat = String.format(locale, formatCoordinateDecimal, lat)
        val formattedLon = String.format(locale, formatCoordinateDecimal, lon)
        return "$formattedLat, $formattedLon"
    }

    private fun getDdmCoords(lat: Double, lon: Double): Pair<String, Int> {
        val formattedLat = getDdmLat(lat)
        val formattedLon = getDdmLon(lon)
        val maxLength = maxOf(formattedLat.length, formattedLon.length)
        return Pair("${formattedLat.padStart(maxLength)}\n${formattedLon.padStart(maxLength)}", 2)
    }

    private fun getDdmCoordsForCopy(lat: Double, lon: Double) =
        "${getDdmLat(lat)}, ${getDdmLon(lon)}"

    private fun getDdmLat(lat: Double): String {
        val ddmLat = replaceDelimiters(AndroidLocation.convert(lat, AndroidLocation.FORMAT_MINUTES))
        return if (lat >= 0.0) {
            "$ddmLat N"
        } else {
            "${ddmLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun getDdmLon(lon: Double): String {
        val ddmLon = replaceDelimiters(AndroidLocation.convert(lon, AndroidLocation.FORMAT_MINUTES))
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

    private fun getDmsCoordsForCopy(lat: Double, lon: Double) =
        "${getDmsLat(lat)}, ${getDmsLon(lon)}"

    private fun getDmsLat(lat: Double): String {
        val dmsLat = replaceDelimiters(AndroidLocation.convert(lat, AndroidLocation.FORMAT_SECONDS))
        return if (lat >= 0.0) {
            "$dmsLat N"
        } else {
            "${dmsLat.replaceFirst("-".toRegex(), "")} S"
        }
    }

    private fun getDmsLon(lon: Double): String {
        val dmsLon = replaceDelimiters(AndroidLocation.convert(lon, AndroidLocation.FORMAT_SECONDS))
        return if (lon >= 0.0) {
            "$dmsLon E"
        } else {
            "${dmsLon.replaceFirst("-".toRegex(), "")} W"
        }
    }

    private fun getUtmCoord(lat: Double, lon: Double): UTMCoord =
        UTMCoord.fromLatLon(
            Angle.fromDegreesLatitude(lat),
            Angle.fromDegreesLongitude(lon)
        )

    private fun getMgrsCoords(lat: Double, lon: Double): Pair<String, Int> =
        Pair(
            MGRSCoord.fromLatLon(
                Angle.fromDegreesLatitude(lat),
                Angle.fromDegreesLongitude(lon)
            ).toString().replace(' ', '\n'),
            3
        )

    private fun getMgrsCoordsForCopy(lat: Double, lon: Double): String =
        MGRSCoord.fromLatLon(
            Angle.fromDegreesLatitude(lat),
            Angle.fromDegreesLongitude(lon)
        ).toString()

    private fun getUtmCoords(lat: Double, lon: Double): Pair<String, Int> =
        Pair("${getUtmZone(lat, lon)}\n${getUtmEasting(lat, lon)}\n${getUtmNorthing(lat, lon)}", 3)

    private fun getUtmCoordsForCopy(lat: Double, lon: Double) =
        "${getUtmZone(lat, lon)} ${getUtmEasting(lat, lon)} ${getUtmNorthing(lat, lon)}"

    private fun getUtmEasting(lat: Double, lon: Double): String {
        return "${
            String.format(
                locale,
                formatCoordinateUtm,
                getUtmCoord(lat, lon).easting
            )
        }m E"
    }

    @Suppress("MagicNumber")
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
        return "${
            String.format(
                locale,
                formatCoordinateUtm,
                utmCoordinate.northing
            )
        }m N"
    }

    private fun getUtmZone(lat: Double, lon: Double): String =
        "${getUtmCoord(lat, lon).zone}${getUtmLatBand(lat)}"

    private fun replaceDelimiters(string: String): String =
        string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
}
