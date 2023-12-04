package io.trewartha.positional.data.location

import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.core.location.LocationRequestCompat.QUALITY_HIGH_ACCURACY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 * AOSP-based [LocationRepository] implementation with no third party dependencies (e.g.
 * Google Play Services)
 */
class AospLocationRepository @Inject constructor(
    coroutineContext: CoroutineContext,
    locationManager: LocationManager
) : LocationRepository {

    /**
     * [Flow] of the current [Location]. Before collecting this flow, make sure to obtain location
     * permissions. If you do not, this flow will retry indefinitely while waiting for permissions.
     * Once permissions have been obtained, location will be attempted and the result(s) will flow
     * through.
     */
    override val location: Flow<Location> = callbackFlow {
        val locationCallback = LocationListenerCompat { androidLocation ->
            try {
                trySendBlocking(androidLocation.toLocation())
            } catch (_: IllegalArgumentException) {
                // Drop any Android locations that can't be converted
                Timber.w("Dropping Android location that can't be converted")
            }
        }
        val locationProvider = if (SDK_INT >= S) FUSED_PROVIDER else GPS_PROVIDER
        val locationRequest = LocationRequestCompat.Builder(LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
            .setQuality(QUALITY_HIGH_ACCURACY)
            .build()
        Timber.i("Requesting location updates")
        try {
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                locationProvider,
                locationRequest,
                (coroutineContext[CoroutineDispatcher] ?: Dispatchers.Default).asExecutor(),
                locationCallback
            )
        } catch (securityException: SecurityException) {
            close(securityException)
        }
        awaitClose {
            Timber.i("Stopping location updates")
            try {
                LocationManagerCompat.removeUpdates(locationManager, locationCallback)
            } catch (securityException: SecurityException) {
                Timber.w(securityException, "Unable to stop location updates")
            }
        }
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
    }.flowOn(coroutineContext)
}

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
