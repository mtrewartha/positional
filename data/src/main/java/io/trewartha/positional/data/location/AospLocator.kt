package io.trewartha.positional.data.location

import android.location.LocationManager
import android.location.LocationManager.FUSED_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat.removeUpdates
import androidx.core.location.LocationManagerCompat.requestLocationUpdates
import androidx.core.location.LocationRequestCompat
import androidx.core.location.LocationRequestCompat.QUALITY_HIGH_ACCURACY
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

/**
 * [Locator] implementation powered by the AOSP [LocationManager]
 */
class AospLocator @Inject constructor(
    private val coroutineContext: CoroutineContext,
    private val locationManager: LocationManager
) : Locator {

    override val location: Flow<Location>
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

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
private const val PERMISSION_RETRY_INTERVAL_MS = 1_000L
