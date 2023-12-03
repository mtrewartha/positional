package io.trewartha.positional.data.location

import android.location.Location as AndroidLocation
import android.hardware.GeomagneticField
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CoordinatesNaNException(coordinates: Coordinates) :
    IllegalStateException("Coordinate is NaN: $coordinates")

class DeclinationNaNException(coordinates: Coordinates, altitude: Distance?, timestamp: Instant) :
    IllegalStateException("Declination is NaN for $coordinates, $altitude, ${timestamp.toEpochMilliseconds()}")

fun AndroidLocation.toLocation(): Location = Location(
    coordinates = Coordinates(latitude, longitude),
    horizontalAccuracy = horizontalAccuracy,
    bearing = bearingObject,
    bearingAccuracy = bearingAccuracy,
    altitude = altitudeObject,
    altitudeAccuracy = altitudeAccuracy,
    speed = speedObject,
    speedAccuracy = speedAccuracy,
    timestamp = timestamp,
    magneticDeclination = magneticDeclination
)

internal val AndroidLocation.altitudeObject: Distance?
    get() = if (hasAltitude()) {
        Distance.Meters(altitude.toFloat())
    } else {
        null
    }

internal val AndroidLocation.altitudeAccuracy: Distance?
    get() = if (SDK_INT >= O && hasVerticalAccuracy() && verticalAccuracyMeters >= 0) {
        Distance.Meters(verticalAccuracyMeters)
    } else {
        null
    }

internal val AndroidLocation.bearingObject: Angle?
    get() = if (this.hasBearing() && speed >= MIN_SPEED_THRESHOLD) {
        Angle.Degrees(this.bearing)
    } else {
        null
    }

internal val AndroidLocation.bearingAccuracy: Angle?
    get() = if (SDK_INT >= O && hasBearingAccuracy() && speed >= MIN_SPEED_THRESHOLD) {
        Angle.Degrees(bearingAccuracyDegrees)
    } else {
        null
    }

internal val AndroidLocation.horizontalAccuracy: Distance?
    get() = if (SDK_INT >= O && hasAccuracy()) {
        Distance.Meters(accuracy)
    } else {
        null
    }

internal val AndroidLocation.magneticDeclination: Angle
    get() {
        val (lat, lon) = try {
            checkNotNull(latitude.toFloat().takeUnless { it.isNaN() }) to
                    checkNotNull(longitude.toFloat().takeUnless { it.isNaN() })
        } catch (_: IllegalStateException) {
            throw CoordinatesNaNException(Coordinates(latitude, longitude))
        }
        // It seems safe to ignore altitude (if we're not able to get it) in the magnetic
        // declination calculation. The error from doing so should be tiny, so the tiny error
        // seems like a great trade-off to make if it means we can still calculate/show true
        // north for the user. See this for more details:
        // https://earthscience.stackexchange.com/a/9613
        val alt = altitude.toFloat().takeUnless { it.isNaN() || !hasAltitude() } ?: 0f
        val millis = timestamp.toEpochMilliseconds()
        val declination = GeomagneticField(lat, lon, alt, millis).declination
            .takeUnless { it.isNaN() }
            ?: throw DeclinationNaNException(
                Coordinates(latitude, longitude),
                altitudeObject,
                timestamp
            )
        return Angle.Degrees(declination)
    }

internal val AndroidLocation.speedObject: Speed?
    get() = if (hasSpeed() && speed >= MIN_SPEED_THRESHOLD) {
        Speed.MetersPerSecond(speed)
    } else {
        null
    }

internal val AndroidLocation.speedAccuracy: Speed?
    get() = if (SDK_INT >= O && hasSpeedAccuracy() && speedAccuracyMetersPerSecond > 0) {
        Speed.MetersPerSecond(speedAccuracyMetersPerSecond)
    } else {
        null
    }

internal val AndroidLocation.timestamp: Instant
    get() = if (time > 0) {
        Instant.fromEpochMilliseconds(time)
    } else {
        Clock.System.now()
    }

private const val MIN_SPEED_THRESHOLD = 0.3f // meters per second
