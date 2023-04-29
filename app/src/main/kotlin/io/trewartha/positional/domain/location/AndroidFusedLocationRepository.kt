package io.trewartha.positional.domain.location

import android.hardware.GeomagneticField
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.trewartha.positional.data.location.Location
import kotlinx.coroutines.CoroutineDispatcher
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
                locationCallback,
                Looper.getMainLooper()
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

    private fun AndroidLocation.toLocation(): Location {
        val latitude = this.latitude
        val longitude = this.longitude
        val horizontalAccuracyMeters =
            if (SDK_INT >= O && this.hasAccuracy()) this.accuracy else null
        val bearingDegrees =
            if (this.hasBearing() && this.speed >= MIN_SPEED_THRESHOLD) this.bearing else null
        val bearingAccuracyDegrees =
            if (SDK_INT >= O && this.hasBearingAccuracy() && this.speed >= MIN_SPEED_THRESHOLD) {
                this.bearingAccuracyDegrees
            } else {
                null
            }
        val altitudeMeters = if (this.hasAltitude()) this.altitude else null
        val altitudeAccuracyMeters =
            if (SDK_INT >= O && this.hasVerticalAccuracy() && this.verticalAccuracyMeters >= 0) {
                this.verticalAccuracyMeters
            } else {
                null
            }
        val speedMetersPerSecond =
            if (this.hasSpeed() && this.speed >= MIN_SPEED_THRESHOLD) this.speed else null
        val speedAccuracyMetersPerSecond =
            if (SDK_INT >= O && this.hasSpeedAccuracy() && this.speedAccuracyMetersPerSecond > 0) {
                this.speed
            } else {
                null
            }
        val timestamp =
            if (this.time > 0) Instant.fromEpochMilliseconds(this.time) else Clock.System.now()
        val magneticDeclinationDegrees =
            GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitudeMeters?.toFloat() ?: 0f,
                timestamp.toEpochMilliseconds()
            ).declination

        return Location(
            latitude = latitude,
            longitude = longitude,
            horizontalAccuracyMeters = horizontalAccuracyMeters,
            bearingDegrees = bearingDegrees,
            bearingAccuracyDegrees = bearingAccuracyDegrees,
            altitudeMeters = altitudeMeters,
            altitudeAccuracyMeters = altitudeAccuracyMeters,
            speedMetersPerSecond = speedMetersPerSecond,
            speedAccuracyMetersPerSecond = speedAccuracyMetersPerSecond,
            timestamp = timestamp,
            magneticDeclinationDegrees = magneticDeclinationDegrees
        )
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val MIN_SPEED_THRESHOLD = 0.3f
    }
}
