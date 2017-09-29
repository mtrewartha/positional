package io.trewartha.positional.position

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.trewartha.positional.Log

class LocationLiveData(private val context: Context) : LiveData<Location>() {

    companion object {
        private const val TAG = "LocationLiveData"
    }

    private val locationClient = FusedLocationProviderClient(context)
    private val locationCallback = LocationCallback()

    var activity: Activity? = null
    var updateInterval = 10000L
    var updateMaxWaitTime = 5000L
    var updatePriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    override fun onActive() {
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
            value = locationResult?.lastLocation
        }
    }
}