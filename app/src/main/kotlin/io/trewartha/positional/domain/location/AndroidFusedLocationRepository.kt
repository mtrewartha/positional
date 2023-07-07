package io.trewartha.positional.domain.location

import android.hardware.GeomagneticField
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.data.measurement.Distance
import io.trewartha.positional.data.measurement.Speed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private typealias AndroidLocation = android.location.Location

class AndroidFusedLocationRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher,
    fusedLocationProviderClient: FusedLocationProviderClient,
) : LocationRepository {

    /**
     * [Flow] of the current [Location]. Before collecting this flow, make sure to obtain location
     * permissions. If you do not, this flow will retry indefinitely while waiting for permissions.
     * Once permissions have been obtained, location will be attempted and the result(s) will flow
     * through.
     */
    override val locationFlow: Flow<Location> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { trySendBlocking(it) }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Timber.d("Location availability changed to $locationAvailability")
            }
        }
        val locationRequest = LocationRequest
            .Builder(LOCATION_UPDATE_PRIORITY, LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
            .build()
        try {
            Timber.i("Requesting location updates: $locationRequest")
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                coroutineDispatcher.asExecutor(),
                locationCallback,
            ).await()
        } catch (securityException: SecurityException) {
            close(securityException)
        }
        awaitClose {
            Timber.i("Suspending location updates")
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }.map { androidLocation ->
        androidLocation.toLocation()
    }.onStart {
        Timber.i("Starting location flow")
    }.onEach {
        Timber.i("Location update received")
    }.retry { throwable ->
        if (throwable is SecurityException) {
            Timber.w("Waiting for location permissions to be granted")
            delay(1.seconds)
            true
        } else {
            Timber.w(throwable, "Waiting for location permissions to be granted")
            throw throwable
        }
    }.onCompletion {
        Timber.i("Location flow completed")
    }.flowOn(coroutineDispatcher)
}

private val AndroidLocation.altitudeObject: Distance?
    get() = if (hasAltitude()) {
        Distance.Meters(altitude.toFloat())
    } else {
        null
    }

private val AndroidLocation.altitudeAccuracy: Distance?
    get() = if (SDK_INT >= O && hasVerticalAccuracy() && verticalAccuracyMeters >= 0) {
        Distance.Meters(verticalAccuracyMeters)
    } else {
        null
    }

private val AndroidLocation.bearingObject: Angle?
    get() = if (this.hasBearing() && speed >= MIN_SPEED_THRESHOLD) {
        Angle.Degrees(this.bearing)
    } else {
        null
    }

private val AndroidLocation.bearingAccuracy: Angle?
    get() = if (SDK_INT >= O && hasBearingAccuracy() && speed >= MIN_SPEED_THRESHOLD) {
        Angle.Degrees(bearingAccuracyDegrees)
    } else {
        null
    }

private val AndroidLocation.horizontalAccuracy: Distance?
    get() = if (SDK_INT >= O && hasAccuracy()) {
        Distance.Meters(accuracy)
    } else {
        null
    }

private val AndroidLocation.magneticDeclination: Angle
    get() = Angle.Degrees(
        GeomagneticField(
            latitude.toFloat(),
            longitude.toFloat(),
            altitude.takeIf { hasAltitude() }?.toFloat() ?: 0f,
            timestamp.toEpochMilliseconds()
        ).declination
    )

private val AndroidLocation.speedObject: Speed?
    get() = if (hasSpeed() && speed >= MIN_SPEED_THRESHOLD) {
        Speed.MetersPerSecond(speed)
    } else {
        null
    }

private val AndroidLocation.speedAccuracy: Speed?
    get() = if (SDK_INT >= O && hasSpeedAccuracy() && speedAccuracyMetersPerSecond > 0) {
        Speed.MetersPerSecond(speedAccuracyMetersPerSecond)
    } else {
        null
    }

private val AndroidLocation.timestamp: Instant
    get() = if (time > 0) {
        Instant.fromEpochMilliseconds(time)
    } else {
        Clock.System.now()
    }

private fun AndroidLocation.toLocation(): Location =
    Location(
        latitude = latitude,
        longitude = longitude,
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

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
private const val MIN_SPEED_THRESHOLD = 0.3f // meters per second
