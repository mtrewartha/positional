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
    get() = if (this.hasBearing() && speed >= io.trewartha.positional.data.location.MIN_SPEED_THRESHOLD) {
        Angle.Degrees(this.bearing)
    } else {
        null
    }

internal val AndroidLocation.bearingAccuracy: Angle?
    get() = if (SDK_INT >= O && hasBearingAccuracy() && speed >= io.trewartha.positional.data.location.MIN_SPEED_THRESHOLD) {
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
    get() = Angle.Degrees(
        GeomagneticField(
            latitude.toFloat(),
            longitude.toFloat(),
            altitude.takeIf { hasAltitude() }?.toFloat() ?: 0f,
            timestamp.toEpochMilliseconds()
        ).declination
    )

internal val AndroidLocation.speedObject: Speed?
    get() = if (hasSpeed() && speed >= io.trewartha.positional.data.location.MIN_SPEED_THRESHOLD) {
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

private const val MIN_SPEED_THRESHOLD = 0.3f // meters per second
