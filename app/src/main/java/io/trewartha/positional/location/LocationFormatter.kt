package io.trewartha.positional.location

import android.content.Context
import android.location.Location
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import io.trewartha.positional.location.CoordinatesFormat.*
import java.util.*

class LocationFormatter(private val context: Context) {

    companion object {
        private const val FORMAT_ACCURACY = "%d"
        private const val FORMAT_BEARING = "%d"
        private const val FORMAT_ELEVATION = "%,d"
        private const val FORMAT_LAT = "% 10.5f"
        private const val FORMAT_LON = "% 10.5f"
        private const val FORMAT_SPEED = "%.0f"
        private const val FORMAT_UTM = "%7.0f"
        private const val HEMISPHERE_NORTH = "gov.nasa.worldwind.avkey.North"
        private val LOCALE = Locale.getDefault()
    }

    fun getAccuracy(location: Location?, metric: Boolean): String {
        val accuracy = when {
            location == null -> 0
            metric -> location.accuracy.toInt()
            else -> DistanceUtils.metersToFeet(location.accuracy).toInt()
        }
        return String.format(LOCALE, FORMAT_ACCURACY, accuracy)
    }

    fun getBearing(location: Location?): String {
        val bearing = location?.bearing?.toInt() ?: 0
        return String.format(LOCALE, FORMAT_BEARING, bearing)
    }

    fun getElevation(location: Location?, metric: Boolean): String {
        val elevation = when {
            location == null -> 0
            metric -> location.altitude.toInt()
            else -> DistanceUtils.metersToFeet(location.altitude.toFloat()).toInt()
        }
        return String.format(LOCALE, FORMAT_ELEVATION, elevation)
    }

    fun getDecimalLatitude(latitude: Double) = String.format(LOCALE, FORMAT_LAT, latitude)

    fun getDecimalLongitude(longitude: Double) = String.format(LOCALE, FORMAT_LON, longitude)

    fun getDmsLatitude(latitude: Double): String {
        var latitudeDMS = Location.convert(latitude, Location.FORMAT_SECONDS)
        latitudeDMS = replaceDelimiters(latitudeDMS)
        if (latitude >= 0.0) {
            latitudeDMS += " N"
        } else {
            latitudeDMS = latitudeDMS.replaceFirst("-".toRegex(), "") + " S"
        }
        return latitudeDMS
    }

    fun getDmsLongitude(longitude: Double): String {
        var longitudeDMS = Location.convert(longitude, Location.FORMAT_SECONDS)
        longitudeDMS = replaceDelimiters(longitudeDMS)
        if (longitude >= 0.0) {
            longitudeDMS += " E"
        } else {
            longitudeDMS = longitudeDMS.replaceFirst("-".toRegex(), "") + " W"
        }
        return longitudeDMS
    }

    fun getMgrsCoordinates(latitude: Double, longitude: Double): String {
        val latAngle = Angle.fromDegreesLatitude(latitude)
        val longAngle = Angle.fromDegreesLongitude(longitude)
        return MGRSCoord.fromLatLon(latAngle, longAngle).toString()
    }

    fun getUtmZone(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        val hemisphere = if (HEMISPHERE_NORTH == utmCoordinate.hemisphere) "N" else "S"
        return "${utmCoordinate.zone}$hemisphere "
    }

    fun getUtmEasting(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        return "${String.format(LOCALE, FORMAT_UTM, utmCoordinate.easting)}m E"
    }

    fun getUtmNorthing(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        return "${String.format(LOCALE, FORMAT_UTM, utmCoordinate.northing)}m N"
    }

    private fun getUtmCoordinate(latitude: Double, longitude: Double): UTMCoord {
        return UTMCoord.fromLatLon(
                Angle.fromDegreesLatitude(latitude),
                Angle.fromDegreesLongitude(longitude)
        )
    }

    fun getCoordinates(latitude: Double, longitude: Double, coordinatesFormat: CoordinatesFormat): String {
        return when (coordinatesFormat) {
            DECIMAL -> {
                val latitudeText = getDecimalLatitude(latitude)
                val longitudeText = getDecimalLongitude(longitude)
                "$latitudeText, $longitudeText"
            }
            DMS -> {
                val latitudeText = getDmsLatitude(latitude)
                val longitudeText = getDmsLongitude(longitude)
                "$latitudeText, $longitudeText"
            }
            MGRS -> {
                getMgrsCoordinates(latitude, longitude)
            }
            UTM -> {
                val zoneText = getUtmZone(latitude, longitude)
                val eastingText = getUtmEasting(latitude, longitude)
                val northingText = getUtmNorthing(latitude, longitude)
                "$zoneText $eastingText $northingText"
            }
        }
    }

    fun getSpeed(location: Location?, metric: Boolean): String {
        val speed = when {
            location == null -> 0.0f
            metric -> DistanceUtils.metersPerSecondToKilometersPerHour(location.speed)
            else -> DistanceUtils.metersPerSecondToMilesPerHour(location.speed)
        }
        return String.format(LOCALE, FORMAT_SPEED, speed)
    }

    fun getDistanceUnit(metric: Boolean): String {
        val stringRes = if (metric) R.string.unit_meters else R.string.unit_feet
        return context.getString(stringRes)
    }

    fun getSpeedUnit(metric: Boolean): String {
        val stringRes = if (metric) R.string.unit_kmh else R.string.unit_mph
        return context.getString(stringRes)
    }

    private fun replaceDelimiters(string: String): String {
        return string.replaceFirst(":".toRegex(), "° ").replaceFirst(":".toRegex(), "' ") + "\""
    }
}
