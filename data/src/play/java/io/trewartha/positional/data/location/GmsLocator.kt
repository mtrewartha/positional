package io.trewartha.positional.data.location

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [Locator] implementation powered by the GMS [FusedLocationProviderClient] with a fallback in case
 * GMS locations are unavailable
 */
class GmsLocator @Inject constructor(
    private val coroutineContext: CoroutineContext,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val fallbackLocator: Locator
) : Locator {

    override val location: Flow<Location>
        get() = gmsLocations.catch { cause ->
            if (cause is ApiException) {
                Timber.w("GMS location unavailable, falling back")
                fallbackLocator.location.collect { location -> emit(location) }
            } else {
                throw cause
            }
        }.onStart {
            Timber.d("Starting location flow")
        }.onEach {
            Timber.d("Location update received")
        }.flowOn(coroutineContext)

    private val gmsLocations = callbackFlow {
        val request = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(LOCATION_UPDATE_INTERVAL_MS)
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .build()
        val executor =
            (this.coroutineContext[CoroutineDispatcher] ?: Dispatchers.Default).asExecutor()
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try {
                    locationResult.lastLocation?.let {
                        val result = trySend(it.toLocation())
                        if (result.isFailure) {
                            Timber.w(result.exceptionOrNull(), "Unable to send location")
                        }
                    }
                } catch (_: IllegalArgumentException) {
                    // Drop any Android locations that can't be converted
                    Timber.w("Dropping Android location that can't be converted")
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Timber.d("Location availability changed to $locationAvailability")
            }
        }

        Timber.i("Requesting location updates")
        fusedLocationProviderClient.requestLocationUpdates(request, executor, callback).await()

        awaitClose {
            Timber.i("Stopping location updates")
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
    }
}

private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
