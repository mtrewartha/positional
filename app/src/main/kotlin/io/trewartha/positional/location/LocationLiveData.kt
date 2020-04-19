package io.trewartha.positional.location

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import timber.log.Timber

class LocationLiveData(context: Context) : LiveData<Location>() {

    private val locationCallback = LocationCallback()
    private val locationClient = FusedLocationProviderClient(context)

    private var firstLocationUpdateTrace: Trace? = null

    var updateInterval = 10_000L
    var updatePriority = LocationRequest.PRIORITY_HIGH_ACCURACY

    override fun onActive() {
        if (firstLocationUpdateTrace == null) {
            firstLocationUpdateTrace = FirebasePerformance.getInstance().newTrace("first_location")
            firstLocationUpdateTrace?.start()
        }

        try {
            val locationRequest = LocationRequest.create()
                    .setPriority(updatePriority)
                    .setInterval(updateInterval)
            Timber.i("Requesting location updates: $locationRequest")
            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Timber.w(e, "Don't have location permissions, no location updates will be received")
        }
    }

    override fun onInactive() {
        Timber.i("Suspending location updates")
        locationClient.removeLocationUpdates(locationCallback)
    }

    private inner class LocationCallback : com.google.android.gms.location.LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            val location = locationResult?.lastLocation ?: return
            value = location

            if (firstLocationUpdateTrace != null) {
                val accuracyCounter = getAccuracyLevelCounter(location.accuracy)
                firstLocationUpdateTrace?.incrementMetric(accuracyCounter, 1L)
                firstLocationUpdateTrace?.stop()
                firstLocationUpdateTrace = null
            }
        }

        private fun getAccuracyLevelCounter(accuracy: Float): String = when (accuracy) {
            in 0.0f.rangeTo(5.0f) -> COUNTER_ACCURACY_VERY_HIGH
            in 5.0f.rangeTo(10.0f) -> COUNTER_ACCURACY_HIGH
            in 10.0f.rangeTo(15.0f) -> COUNTER_ACCURACY_MEDIUM
            in 15.0f.rangeTo(20.0f) -> COUNTER_ACCURACY_LOW
            else -> COUNTER_ACCURACY_VERY_LOW
        }
    }

    companion object {
        private const val COUNTER_ACCURACY_VERY_HIGH = "accuracy_very_high"
        private const val COUNTER_ACCURACY_HIGH = "accuracy_high"
        private const val COUNTER_ACCURACY_MEDIUM = "accuracy_medium"
        private const val COUNTER_ACCURACY_LOW = "accuracy_low"
        private const val COUNTER_ACCURACY_VERY_LOW = "accuracy_very_low"
    }
}