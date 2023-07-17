package io.trewartha.positional.domain.location

import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.core.location.LocationRequestCompat.QUALITY_HIGH_ACCURACY
import io.trewartha.positional.data.location.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    override val locationFlow: Flow<Location> = callbackFlow {
        val locationCallback = LocationListenerCompat { location -> trySendBlocking(location) }
        val locationProvider = if (SDK_INT >= S) FUSED_PROVIDER else GPS_PROVIDER
        val locationRequest = LocationRequestCompat.Builder(LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
            .setQuality(QUALITY_HIGH_ACCURACY)
            .build()
        try {
            Timber.i("Requesting location updates")
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
    }.flowOn(coroutineContext)
}

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
