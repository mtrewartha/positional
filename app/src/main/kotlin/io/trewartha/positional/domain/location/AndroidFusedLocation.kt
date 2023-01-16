package io.trewartha.positional.domain.location

import android.hardware.GeomagneticField
import android.os.Build
import io.trewartha.positional.data.location.Location
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AndroidFusedLocation(private val location: android.location.Location) : Location {

    override val latitude: Double
        get() = location.latitude

    override val longitude: Double
        get() = location.longitude

    override val horizontalAccuracyMeters: Float?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                location.hasAccuracy()
            ) location.accuracy
            else null
        }

    override val bearingDegrees: Float?
        get() {
            return if (
                location.hasBearing() &&
                location.speed >= MIN_SPEED_THRESHOLD
            ) location.bearing
            else null
        }

    override val bearingAccuracyDegrees: Float?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                location.hasBearingAccuracy() &&
                location.speed >= MIN_SPEED_THRESHOLD
            ) location.bearingAccuracyDegrees
            else null
        }

    override val altitudeMeters: Double?
        get() {
            return if (location.hasAltitude()) location.altitude
            else null
        }

    override val altitudeAccuracyMeters: Float?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                location.hasVerticalAccuracy() &&
                location.verticalAccuracyMeters >= 0
            ) location.verticalAccuracyMeters
            else null
        }

    override val magneticDeclinationDegrees: Float
        get() {
            return GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitudeMeters?.toFloat() ?: 0f,
                timestamp.toEpochMilliseconds()
            ).declination
        }

    override val speedMetersPerSecond: Float?
        get() {
            return if (
                location.hasSpeed() &&
                location.speed >= MIN_SPEED_THRESHOLD
            ) location.speed
            else null
        }

    override val speedAccuracyMetersPerSecond: Float?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                location.hasSpeedAccuracy() &&
                location.speedAccuracyMetersPerSecond > 0
            ) location.speed
            else null
        }

    override val timestamp: Instant
        get() {
            return if (location.time > 0) Instant.fromEpochMilliseconds(location.time)
            else Clock.System.now()
        }

    companion object {
        private const val MIN_SPEED_THRESHOLD = 0.3f
    }
}
