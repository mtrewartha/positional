package io.trewartha.positional.location

import android.annotation.SuppressLint
import android.hardware.GeomagneticField
import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat.removeUpdates
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.core.location.LocationRequestCompat
import androidx.core.location.LocationRequestCompat.QUALITY_HIGH_ACCURACY
import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.Distance
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import io.trewartha.positional.core.measurement.Speed
import io.trewartha.positional.core.measurement.degrees
import io.trewartha.positional.core.measurement.meters
import io.trewartha.positional.core.measurement.mps
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * [Locator] implementation powered by the AOSP [LocationManager]
 */
class AospLocator @Inject constructor(
    private val locationManager: LocationManager,
    private val coroutineContext: CoroutineContext = Dispatchers.Default
) : Locator {

    override val location: Flow<Location>
        @SuppressLint("MissingPermission")
        get() = callbackFlow {
            val provider = if (SDK_INT >= S) FUSED_PROVIDER else GPS_PROVIDER
            val request = LocationRequestCompat.Builder(LOCATION_UPDATE_INTERVAL_MS)
                .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
                .setQuality(QUALITY_HIGH_ACCURACY).build()
            val callbackExecutor =
                (this.coroutineContext[CoroutineDispatcher] ?: Dispatchers.Default).asExecutor()
            val callback = LocationListenerCompat { androidLocation ->
                try {
                    val result = trySend(androidLocation.toLocation())
                    if (result.isFailure) {
                        Timber.w(result.exceptionOrNull(), "Unable to send location")
                    }
                } catch (exception: IllegalArgumentException) {
                    // Drop any Android locations that can't be converted
                    Timber.w(exception, "Dropping Android location that can't be converted")
                }
            }

            Timber.d("Requesting location updates")
            requestLocationUpdates(locationManager, provider, request, callbackExecutor, callback)

            awaitClose {
                Timber.d("Stopping location updates")
                try {
                    removeUpdates(locationManager, callback)
                } catch (_: SecurityException) {
                    // If we don't have permissions to remove updates, we're probably closing
                    // because of that lack of permission in the first place, which means we don't
                    // need to remove updates. Just silently ignore this situation.
                }
            }
        }.onStart {
            Timber.d("Starting location flow")
        }.onEach {
            Timber.d("Location update received")
        }.retry { cause ->
            if (cause is SecurityException) {
                delay(PERMISSION_RETRY_INTERVAL_MS)
                true
            } else {
                Timber.w(cause, "Unable to request location updates")
                false
            }
        }.flowOn(coroutineContext)
}

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

internal val android.location.Location.altitudeObject: Distance?
    get() = if (hasAltitude()) {
        altitude.meters
    } else {
        null
    }

internal val android.location.Location.altitudeAccuracy: Distance?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasVerticalAccuracy() &&
        verticalAccuracyMeters >= 0
    ) {
        verticalAccuracyMeters.meters
    } else {
        null
    }

internal val android.location.Location.bearingObject: Angle?
    get() = if (this.hasBearing() && speed >= MIN_SPEED_THRESHOLD) {
        this.bearing.degrees
    } else {
        null
    }

internal val android.location.Location.bearingAccuracy: Angle?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasBearingAccuracy() &&
        speed >= MIN_SPEED_THRESHOLD
    ) {
        bearingAccuracyDegrees.degrees
    } else {
        null
    }

internal val android.location.Location.horizontalAccuracy: Distance?
    get() = if (SDK_INT >= Build.VERSION_CODES.O && hasAccuracy()) {
        accuracy.meters
    } else {
        null
    }

internal val android.location.Location.magneticDeclination: Angle?
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
    } catch (exception: IllegalArgumentException) {
        Timber.w(exception, "Unable to calculate magnetic declination")
        null
    }

internal val android.location.Location.speedObject: Speed?
    get() = if (hasSpeed() && speed >= MIN_SPEED_THRESHOLD) {
        speed.mps
    } else {
        null
    }

internal val android.location.Location.speedAccuracy: Speed?
    get() = if (
        SDK_INT >= Build.VERSION_CODES.O &&
        hasSpeedAccuracy() &&
        speedAccuracyMetersPerSecond > 0
    ) {
        speedAccuracyMetersPerSecond.mps
    } else {
        null
    }

internal val android.location.Location.timestamp: Instant
    get() = if (time > 0) {
        Instant.fromEpochMilliseconds(time)
    } else {
        Clock.System.now()
    }

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
private const val MIN_SPEED_THRESHOLD = 0.3f // meters per second
private const val PERMISSION_RETRY_INTERVAL_MS = 1_000L
