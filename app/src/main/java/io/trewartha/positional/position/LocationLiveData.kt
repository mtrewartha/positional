package io.trewartha.positional.position

import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import io.trewartha.positional.Log

class LocationLiveData(private val context: Context) : LiveData<Location>() {

    companion object {
        private const val COUNTER_ACCURACY_VERY_HIGH = "accuracy_very_high"
        private const val COUNTER_ACCURACY_HIGH = "accuracy_high"
        private const val COUNTER_ACCURACY_MEDIUM = "accuracy_medium"
        private const val COUNTER_ACCURACY_LOW = "accuracy_low"
        private const val COUNTER_ACCURACY_VERY_LOW = "accuracy_very_low"
        private const val TAG = "LocationLiveData"
    }

    private val locationClient = FusedLocationProviderClient(context)
    private val locationCallback = LocationCallback()

    private var firstLocationUpdateTrace: Trace? = null

    var updateInterval = 10000L
    var updateMaxWaitTime = 5000L
    var updatePriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    override fun onActive() {
        if (firstLocationUpdateTrace == null) {
            firstLocationUpdateTrace = FirebasePerformance.getInstance().newTrace("first_location")
            firstLocationUpdateTrace?.start()
        }

        Log.info(TAG, "Requesting location updates...")
        FusedLocationProviderClient(context).removeLocationUpdates(locationCallback)
        val locationRequest = LocationRequest.create()
                .setPriority(updatePriority)
                .setMaxWaitTime(updateMaxWaitTime)
                .setInterval(updateInterval)
        try {
            locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.warn(TAG, "Don't have location permissions, no location updates will be received")
        }
    }

    override fun onInactive() {
        Log.info(TAG, "Suspending location updates")
        locationClient.removeLocationUpdates(locationCallback)
    }

    private inner class LocationCallback : com.google.android.gms.location.LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            val location = locationResult?.lastLocation
            value = location

            if (location != null && firstLocationUpdateTrace != null) {
                val accuracyCounter = getAccuracyLevelCounter(location.accuracy)
                firstLocationUpdateTrace?.incrementCounter(accuracyCounter)
                firstLocationUpdateTrace?.stop()
                firstLocationUpdateTrace = null
            }
        }

        private fun getAccuracyLevelCounter(accuracy: Float): String {
            return when (accuracy) {
                in 0.0f.rangeTo(5.0f) -> COUNTER_ACCURACY_VERY_HIGH
                in 5.0f.rangeTo(10.0f) -> COUNTER_ACCURACY_HIGH
                in 10.0f.rangeTo(15.0f) -> COUNTER_ACCURACY_MEDIUM
                in 15.0f.rangeTo(20.0f) -> COUNTER_ACCURACY_LOW
                else -> COUNTER_ACCURACY_VERY_LOW
            }
        }
    }
}