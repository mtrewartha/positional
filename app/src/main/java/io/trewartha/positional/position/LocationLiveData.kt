package io.trewartha.positional.position

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import io.trewartha.positional.Log
import io.trewartha.positional.R

class LocationLiveData(private val context: Context) : LiveData<Location>(), LocationListener {

    companion object {
        private const val TAG = "LocationLiveData"
        private const val REQUEST_CODE = 1
    }

    var activity: Activity? = null
    var updateInterval = 10000L
    var updateMaxWaitTime = 5000L
    var updatePriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    private val googleConnectionCallbacks = GoogleConnectionCallbacks()
    private val googleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(googleConnectionCallbacks)
            .addOnConnectionFailedListener(googleConnectionCallbacks)
            .addApi(LocationServices.API)
            .build()

    override fun onActive() {
        requestLocationUpdates()
    }

    override fun onInactive() {
        suspendLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        value = location
    }

    private fun connectToGooglePlayServices() {
        Log.info(TAG, "Connecting to Google Play Services")
        googleApiClient.connect()
    }

    private fun suspendLocationUpdates() {
        Log.info(TAG, "Suspending location updates")
        if (googleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
        }
    }

    private fun requestLocationUpdates() {
        if (!googleApiClient.isConnected && !googleApiClient.isConnecting) {
            connectToGooglePlayServices()
        } else if (!googleApiClient.isConnecting) {
            Log.info(TAG, "Requesting location updates...")
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            val locationRequest = LocationRequest.create()
                    .setPriority(updatePriority)
                    .setMaxWaitTime(updateMaxWaitTime)
                    .setInterval(updateInterval)
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
            } catch (e: SecurityException) {
                Log.warn(TAG, "Don't have location permissions, no location updates will be received")
            }
        } else {
            Log.info(TAG, "Location updates will resume once the Google API client is connected")
        }
    }

    private inner class GoogleConnectionCallbacks : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        override fun onConnected(bundle: Bundle?) {
            Log.info(TAG, "Google Play Services connection established")
            requestLocationUpdates()
        }

        override fun onConnectionSuspended(i: Int) {
            Log.info(TAG, "Google Play Services connection suspended")
            suspendLocationUpdates()
        }

        override fun onConnectionFailed(connectionResult: ConnectionResult) {
            Log.error(TAG, "Google Play Services connection failed (error code " + connectionResult.errorCode + ")")
            if (connectionResult.hasResolution()) {
                resolvePlayServicesConnectionError(connectionResult)
            } else {
                showPlayServicesUpdateDialog(connectionResult.errorCode)
            }
        }

        private fun resolvePlayServicesConnectionError(connectionResult: ConnectionResult) {
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_CODE)
            } catch (e: IntentSender.SendIntentException) {
                Log.error(TAG, "There was a fatal exception when trying to connect to the Google Play Services", e)
                showPlayServicesFatalDialog()
            }
        }

        private fun showPlayServicesFatalDialog() {
            AlertDialog.Builder(context)
                    .setTitle(R.string.google_play_services_connection_failed_title)
                    .setMessage(R.string.google_play_services_connection_failed_message)
                    .setPositiveButton(R.string.exit) { _, _ -> activity?.finish() }
                    .show()
        }

        private fun showPlayServicesUpdateDialog(errorCode: Int) {
            GooglePlayServicesUtil.showErrorDialogFragment(
                    errorCode,
                    activity,
                    null,
                    REQUEST_CODE,
                    { activity?.finish() }
            )
        }
    }
}