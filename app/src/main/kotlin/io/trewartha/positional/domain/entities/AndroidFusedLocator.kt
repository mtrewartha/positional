package io.trewartha.positional.domain.entities

import android.os.Build
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
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

class AndroidFusedLocator @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher,
    fusedLocationProviderClient: FusedLocationProviderClient,
) : Locator {

    /**
     * [Flow] of the current [Location]. Before collecting this flow, make sure to obtain location
     * permissions. If you do not, this flow will retry indefinitely while waiting for permissions.
     * Once permissions have been obtained, location will be attempted and the result(s) will flow
     * through.
     */
    override val locationFlow: Flow<Location> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                trySendBlocking(locationResult.lastLocation)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Timber.d("Location availability changed to $locationAvailability")
            }
        }
        val locationRequest = LocationRequest.create()
            .setPriority(LOCATION_UPDATE_PRIORITY)
            .setInterval(LOCATION_UPDATE_INTERVAL_MS)
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
        androidLocation.toDomainLocation()
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

    private val android.location.Location.domainLatitude: Double
        get() = latitude

    private val android.location.Location.domainLongitude: Double
        get() = longitude

    private val android.location.Location.domainHorizontalAccuracyMeters: Double?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                hasAccuracy()
            ) accuracy.toDouble()
            else null
        }

    private val android.location.Location.domainAltitudeMeters: Double?
        get() {
            return if (hasAltitude()) altitude
            else null
        }

    private val android.location.Location.domainAltitudeAccuracyMeters: Double?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                hasVerticalAccuracy() &&
                verticalAccuracyMeters >= 0
            ) verticalAccuracyMeters.toDouble()
            else null
        }

    private val android.location.Location.domainBearingDegrees: Double?
        get() {
            return if (
                hasBearing() &&
                speed >= MIN_SPEED_THRESHOLD
            ) bearing.toDouble()
            else null
        }

    private val android.location.Location.domainBearingAccuracyDegrees: Double?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                hasBearingAccuracy() &&
                speed >= MIN_SPEED_THRESHOLD
            ) bearingAccuracyDegrees.toDouble()
            else null
        }

    private val android.location.Location.domainSpeedMetersPerSecond: Double?
        get() {
            return if (hasSpeed() && speed >= MIN_SPEED_THRESHOLD) speed.toDouble()
            else null
        }

    private val android.location.Location.domainSpeedAccuracyMetersPerSecond: Double?
        get() {
            return if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                hasSpeedAccuracy() &&
                speedAccuracyMetersPerSecond > 0
            ) speed.toDouble()
            else null
        }

    private val android.location.Location.domainTimestamp: Instant
        get() {
            return if (time > 0) Instant.fromEpochMilliseconds(time)
            else Clock.System.now()
        }

    private fun android.location.Location.toDomainLocation(): Location {
        return Location(
            latitude = domainLatitude,
            longitude = domainLongitude,
            horizontalAccuracyMeters = domainHorizontalAccuracyMeters,
            bearingDegrees = domainBearingDegrees,
            bearingAccuracyDegrees = domainBearingAccuracyDegrees,
            altitudeMeters = domainAltitudeMeters,
            altitudeAccuracyMeters = domainAltitudeAccuracyMeters,
            speedMetersPerSecond = domainSpeedMetersPerSecond,
            speedAccuracyMetersPerSecond = domainSpeedAccuracyMetersPerSecond,
            timestamp = domainTimestamp,
        )
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val MIN_SPEED_THRESHOLD = 0.3f
    }
}