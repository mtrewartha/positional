package io.trewartha.positional.location

import android.content.Context
import android.location.Location
import gov.nasa.worldwind.geom.Angle
import gov.nasa.worldwind.geom.coords.MGRSCoord
import gov.nasa.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.R
import io.trewartha.positional.location.CoordinatesFormat.*
import java.text.SimpleDateFormat
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
    private val simpleDateFormat by lazy { SimpleDateFormat.getTimeInstance() }

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

    fun getDecimalLatitude(latitude: Double) = String
        .format(Locale.getDefault(), formatCoordinateDecimal, latitude)
        .padStart(10)

    fun getDecimalLongitude(longitude: Double) = String
        .format(Locale.getDefault(), formatCoordinateDecimal, longitude)
        .padStart(10)

    fun getDegreesAndDecimalMinutesLatitude(latitude: Double): String {
        var latitudeDM = Location.convert(latitude, Location.FORMAT_MINUTES)
        latitudeDM = replaceDelimiters(latitudeDM)
        if (latitude >= 0.0) {
            latitudeDM += " N"
        } else {
            latitudeDM = latitudeDM.replaceFirst("-".toRegex(), "") + " S"
        }
        return latitudeDM.padStart(17)
    }

    fun getDegreesAndDecimalMinutesLongitude(longitude: Double): String {
        var longitudeDM = Location.convert(longitude, Location.FORMAT_MINUTES)
        longitudeDM = replaceDelimiters(longitudeDM)
        if (longitude >= 0.0) {
            longitudeDM += " E"
        } else {
            longitudeDM = longitudeDM.replaceFirst("-".toRegex(), "") + " W"
        }
        return longitudeDM.padStart(17)
    }

    fun getDmsLatitude(latitude: Double): String {
        var latitudeDMS = Location.convert(latitude, Location.FORMAT_SECONDS)
        latitudeDMS = replaceDelimiters(latitudeDMS)
        if (latitude >= 0.0) {
            latitudeDMS += " N"
        } else {
            latitudeDMS = latitudeDMS.replaceFirst("-".toRegex(), "") + " S"
        }
        return latitudeDMS.padStart(20)
    }

    fun getDmsLongitude(longitude: Double): String {
        var longitudeDMS = Location.convert(longitude, Location.FORMAT_SECONDS)
        longitudeDMS = replaceDelimiters(longitudeDMS)
        if (longitude >= 0.0) {
            longitudeDMS += " E"
        } else {
            longitudeDMS = longitudeDMS.replaceFirst("-".toRegex(), "") + " W"
        }
        return longitudeDMS.padStart(20)
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

    fun getMgrsCoordinates(latitude: Double, longitude: Double): String {
        val latAngle = Angle.fromDegreesLatitude(latitude)
        val longAngle = Angle.fromDegreesLongitude(longitude)
        return MGRSCoord.fromLatLon(latAngle, longAngle).toString()
    }

    fun getUtmEasting(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        return "${String.format(
            Locale.getDefault(),
            formatCoordinateUtm,
            utmCoordinate.easting
        )}m E"
    }

    fun getUtmLatitudeBand(latitude: Double): String = when {
        -80.0 <= latitude && latitude < -72.0 -> "C"
        -72.0 <= latitude && latitude < -64.0 -> "D"
        -72.0 <= latitude && latitude < -56.0 -> "E"
        -72.0 <= latitude && latitude < -48.0 -> "F"
        -72.0 <= latitude && latitude < -40.0 -> "G"
        -72.0 <= latitude && latitude < -32.0 -> "H"
        -72.0 <= latitude && latitude < -24.0 -> "J"
        -72.0 <= latitude && latitude < -16.0 -> "K"
        -72.0 <= latitude && latitude < -8.0 -> "L"
        -72.0 <= latitude && latitude < 0.0 -> "M"
        00.0 <= latitude && latitude < 08.0 -> "N"
        08.0 <= latitude && latitude < 16.0 -> "P"
        16.0 <= latitude && latitude < 24.0 -> "Q"
        24.0 <= latitude && latitude < 32.0 -> "R"
        32.0 <= latitude && latitude < 40.0 -> "S"
        40.0 <= latitude && latitude < 48.0 -> "T"
        48.0 <= latitude && latitude < 56.0 -> "U"
        56.0 <= latitude && latitude < 64.0 -> "V"
        64.0 <= latitude && latitude < 72.0 -> "W"
        72.0 <= latitude && latitude < 84.0 -> "X"
        else -> ""
    }

    fun getUtmNorthing(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        return "${String.format(
            Locale.getDefault(),
            formatCoordinateUtm,
            utmCoordinate.northing
        )}m N"
    }

    fun getUtmZone(latitude: Double, longitude: Double): String {
        val utmCoordinate = getUtmCoordinate(latitude, longitude)
        val latitudeBand = getUtmLatitudeBand(latitude)
        return "${utmCoordinate.zone}$latitudeBand "
    }

    private fun getUtmCoordinate(latitude: Double, longitude: Double): UTMCoord = UTMCoord
        .fromLatLon(
            Angle.fromDegreesLatitude(latitude),
            Angle.fromDegreesLongitude(longitude)
        )

    fun getCoordinates(
        latitude: Double,
        longitude: Double,
        coordinatesFormat: CoordinatesFormat
    ): String = when (coordinatesFormat) {
        DECIMAL -> {
            val latitudeText = getDecimalLatitude(latitude)
            val longitudeText = getDecimalLongitude(longitude)
            "$latitudeText, $longitudeText"
        }
        DDM -> {
            val latitudeText = getDegreesAndDecimalMinutesLatitude(latitude)
            val longitudeText = getDegreesAndDecimalMinutesLongitude(longitude)
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

    fun getSpeed(
        location: Location?,
        metric: Boolean
    ): String = if (location == null || !location.hasSpeed() || location.speed == 0f) {
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

    fun getTimestamp(location: Location?): String = if (location == null || location.time == 0L) {
        locationValueUnknown
    } else {
        simpleDateFormat.format(Date(location.time))
    }

    private fun replaceDelimiters(string: String): String = string
        .replaceFirst(":".toRegex(), "° ")
        .replaceFirst(":".toRegex(), "' ") + "\""
}
