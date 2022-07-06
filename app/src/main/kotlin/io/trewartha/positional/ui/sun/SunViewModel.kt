package io.trewartha.positional.ui.sun

import android.app.Application
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import ca.rmen.sunrisesunset.SunriseSunset
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

@HiltViewModel
class SunViewModel @Inject constructor(
    app: Application,
    private val dateTimeFormatter: DateTimeFormatter,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : AndroidViewModel(app) {

    val sunState: LiveData<SunState> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Timber.d("Received location update")
                trySend(locationResult.lastLocation)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                Timber.d("Location availability changed to $locationAvailability")
            }
        }
        try {
            val locationRequest = LocationRequest.create()
                .setPriority(LOCATION_UPDATE_PRIORITY)
                .setInterval(LOCATION_UPDATE_INTERVAL_MS)
            Timber.i("Requesting location updates: $locationRequest")
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Timber.w(e, "Don't have location permissions, no location updates will be received")
        }

        awaitClose {
            Timber.i("Suspending location updates")
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }.map {
        val calendar = Calendar.getInstance().apply { timeInMillis = it.time }
        val latitude = it.latitude
        val longitude = it.longitude

        val sunriseSunset =
            SunriseSunset.getSunriseSunset(calendar, latitude, longitude)
        val civilTwilights =
            SunriseSunset.getCivilTwilight(calendar, latitude, longitude)
        val nauticalTwilights =
            SunriseSunset.getNauticalTwilight(calendar, latitude, longitude)
        val astronomicalTwilights =
            SunriseSunset.getAstronomicalTwilight(calendar, latitude, longitude)

        SunState(
            formatDate(it.time),
            formatTime(astronomicalTwilights[0]?.timeInMillis),
            formatTime(nauticalTwilights[0]?.timeInMillis),
            formatTime(civilTwilights[0]?.timeInMillis),
            formatTime(sunriseSunset[0]?.timeInMillis),
            formatTime(sunriseSunset[1]?.timeInMillis),
            formatTime(civilTwilights[1]?.timeInMillis),
            formatTime(nauticalTwilights[1]?.timeInMillis),
            formatTime(astronomicalTwilights[1]?.timeInMillis),
            String.format(formatUpdatedAt, formatTime(it.time))
        )
    }.asLiveData()

    private val formatUpdatedAt = app.getString(R.string.location_updated_at)

    private fun formatDate(epochMillis: Long?): String? =
        epochMillis?.let { dateTimeFormatter.getFormattedDate(Instant.fromEpochMilliseconds(it)) }

    private fun formatTime(epochMillis: Long?): String? =
        epochMillis?.let { dateTimeFormatter.getFormattedTime(Instant.fromEpochMilliseconds(it)) }

    data class SunState(
        val date: String?,
        val astronomicalDawn: String?,
        val nauticalDawn: String?,
        val civilDawn: String?,
        val sunrise: String?,
        val sunset: String?,
        val civilDusk: String?,
        val nauticalDusk: String?,
        val astronomicalDusk: String?,
        val updatedAt: String?
    )

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 60_000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_LOW_POWER
    }
}