package io.trewartha.positional.location

import android.content.Context
import android.location.Location
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import io.trewartha.positional.location.CoordinatesFormat.*
import java.util.*

class LocationFormatter(context: Context) {

    private val formatAccuracyImperial by lazy { context.getString(R.string.location_accuracy_imperial) }
    private val formatAccuracyMetric by lazy { context.getString(R.string.location_accuracy_metric) }
    private val formatBearing by lazy { context.getString(R.string.location_bearing) }
    private val formatCoordinateDecimal by lazy { context.getString(R.string.location_coordinate_decimal) }
    private val formatCoordinateUtm by lazy { context.getString(R.string.location_coordinate_utm) }
    private val formatElevationImperial by lazy { context.getString(R.string.location_elevation_imperial) }
    private val formatElevationMetric by lazy { context.getString(R.string.location_elevation_metric) }
    private val formatSpeedImperial by lazy { context.getString(R.string.location_speed_imperial) }
    private val formatSpeedMetric by lazy { context.getString(R.string.location_speed_metric) }
    private val locationValueUnknown by lazy { context.getString(R.string.location_value_unknown) }

    companion object {
        private const val HEMISPHERE_NORTH = "gov.nasa.worldwind.avkey.North"
    }

    fun getAccuracy(
        location: Location?,
        metric: Boolean
    ): String = if (location == null || !location.hasAccuracy()) {
        locationValueUnknown
    } else {
        val accuracy = if (metric)
            location.accuracy.toInt()
        else
            DistanceUtils.metersToFeet(location.accuracy).toInt()
        val format = if (metric)
            formatAccuracyMetric
        else
            formatAccuracyImperial
        String.format(Locale.getDefault(), format, accuracy)
    }

    fun getBearing(
        location: Location?
    ): String = if (location == null || !location.hasBearing()) {
        locationValueUnknown
    } else {
        val bearing = location.bearing.toInt()
        String.format(Locale.getDefault(), formatBearing, bearing)
    }

    fun getElevation(
        location: Location?,
        metric: Boolean
    ): String = if (location == null || !location.hasAltitude()) {
        locationValueUnknown
    } else {
        val elevation = if (metric)
            location.altitude.toInt()
        else
            DistanceUtils.metersToFeet(location.altitude.toFloat()).toInt()
        val format = if (metric)
            formatElevationMetric
        else
            formatElevationImperial
        String.format(Locale.getDefault(), format, elevation)
    }

    fun getDecimalLatitude(latitude: Double) =
        String.format(Locale.getDefault(), formatCoordinateDecimal, latitude)

    fun getDecimalLongitude(longitude: Double) =
        String.format(Locale.getDefault(), formatCoordinateDecimal, longitude)

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
        return "${String.format(
            Locale.getDefault(),
            formatCoordinateUtm,
            utmCoordinate.easting
        )}m E"
    }

    fun getUtmNorthing(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        return "${String.format(
            Locale.getDefault(),
            formatCoordinateUtm,
            utmCoordinate.northing
        )}m N"
    }

    private fun getUtmCoordinate(latitude: Double, longitude: Double): UTMCoord {
        return UTMCoord.fromLatLon(
            Angle.fromDegreesLatitude(latitude),
            Angle.fromDegreesLongitude(longitude)
        )
    }

    fun getCoordinates(
        latitude: Double,
        longitude: Double,
        coordinatesFormat: CoordinatesFormat
    ): String {
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

    fun getSpeed(
        location: Location?,
        metric: Boolean
    ): String = if (location == null || !location.hasSpeed()) {
        locationValueUnknown
    } else {
        val speed = if (metric)
            DistanceUtils.metersPerSecondToKilometersPerHour(location.speed)
        else
            DistanceUtils.metersPerSecondToMilesPerHour(location.speed)
        val format = if (metric)
            formatSpeedMetric
        else
            formatSpeedImperial
        String.format(Locale.getDefault(), format, speed)
    }

    private fun replaceDelimiters(string: String): String = string
        .replaceFirst(":".toRegex(), "° ")
        .replaceFirst(":".toRegex(), "' ") + "\""
}
