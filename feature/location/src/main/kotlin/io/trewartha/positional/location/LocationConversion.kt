package io.trewartha.positional.location

import android.hardware.GeomagneticField
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.Distance
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.Speed
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.measurement.meters
import io.trewartha.positional.core.measurement.mps
import kotlin.time.Clock
import kotlin.time.Instant

private const val MIN_SPEED_THRESHOLD = 0.3f // meters per second

internal fun android.location.Location.toLocation(): Location = Location(
    timestamp = timestamp,
    coordinates = GeodeticCoordinates(latitude.degrees, longitude.degrees),
    horizontalAccuracy = horizontalAccuracy,
    bearing = bearingObject,
    bearingAccuracy = bearingAccuracy,
    altitude = altitudeObject,
    altitudeAccuracy = altitudeAccuracy,
    magneticDeclination = magneticDeclination,
    speed = speedObject,
    speedAccuracy = speedAccuracy
)

private val android.location.Location.altitudeObject: Distance?
    get() = if (hasAltitude()) {
        altitude.meters
    } else {
        null
    }

private val android.location.Location.altitudeAccuracy: Distance?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasVerticalAccuracy() &&
        verticalAccuracyMeters >= 0
    ) {
        verticalAccuracyMeters.meters
    } else {
        null
    }

private val android.location.Location.bearingObject: Angle?
    get() = if (this.hasBearing() && speed >= MIN_SPEED_THRESHOLD) {
        this.bearing.degrees
    } else {
        null
    }

private val android.location.Location.bearingAccuracy: Angle?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasBearingAccuracy() &&
        speed >= MIN_SPEED_THRESHOLD
    ) {
        bearingAccuracyDegrees.degrees
    } else {
        null
    }

private val android.location.Location.horizontalAccuracy: Distance?
    get() = if (SDK_INT >= Build.VERSION_CODES.O && hasAccuracy()) {
        accuracy.meters
    } else {
        null
    }

private val android.location.Location.magneticDeclination: Angle?
    get() = try {
        val lat = latitude.toFloat()
        val lon = longitude.toFloat()
        require(lat.isFinite())
        require(lon.isFinite())
        // It seems safe to ignore altitude (if we're not able to get it) in the magnetic
        // declination calculation. The error from doing so should be tiny, so the tiny error
        // seems like a great trade-off to make if it means we can still calculate/show true
        // north for the user. See this for more details:
        // https://earthscience.stackexchange.com/a/9613
        val alt = altitude.toFloat().takeIf { hasAltitude() && it.isFinite() } ?: 0f
        val millis = timestamp.toEpochMilliseconds()
        GeomagneticField(lat, lon, alt, millis).declination.degrees
    } catch (_: IllegalArgumentException) {
        null
    }

private val android.location.Location.speedObject: Speed?
    get() = if (hasSpeed() && speed >= MIN_SPEED_THRESHOLD) {
        speed.mps
    } else {
        null
    }

private val android.location.Location.speedAccuracy: Speed?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasSpeedAccuracy() &&
        speedAccuracyMetersPerSecond > 0
    ) {
        speedAccuracyMetersPerSecond.mps
    } else {
        null
    }

private val android.location.Location.timestamp: Instant
    get() = if (time > 0) {
        Instant.fromEpochMilliseconds(time)
    } else {
        Clock.System.now()
    }
