package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Location
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.support.v7.app.AlertDialog
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.ui.position.LocationViewModel

abstract class LocationAwareFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSIONS = 1
        private const val TAG = "LocationAwareFragment"
    }

    protected var lastLocation: Location? = null

    private lateinit var locationLiveData: LocationLiveData

    abstract fun onLocationChanged(location: Location?)
    abstract fun getLocationUpdateInterval(): Long
    abstract fun getLocationUpdateMaxWaitTime(): Long
    abstract fun getLocationUpdatePriority(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationLiveData = ViewModelProviders.of(this)
                .get(LocationViewModel::class.java)
                .getLocation()
        locationLiveData.updatePriority = getLocationUpdatePriority()
        locationLiveData.updateInterval = getLocationUpdateInterval()
        locationLiveData.updateMaxWaitTime = getLocationUpdateMaxWaitTime()
    }

    override fun onResume() {
        super.onResume()
        if (haveLocationPermissions()) {
            observeLocationChanges()
        } else {
            AlertDialog.Builder(context ?: return)
                    .setTitle(R.string.location_permission_explanation_title)
                    .setMessage(R.string.location_permission_explanation_message)
                    .setPositiveButton(
                            R.string.location_permission_explanation_positive,
                            { _, _ -> requestLocationPermissions() }
                    )
                    .show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationLiveData.removeObservers(this)
    }

    protected fun haveLocationPermissions(): Boolean {
        val context = context ?: return false
        val coarsePermission = checkSelfPermission(context, ACCESS_COARSE_LOCATION)
        val finePermission = checkSelfPermission(context, ACCESS_FINE_LOCATION)
        return coarsePermission == PERMISSION_GRANTED && finePermission == PERMISSION_GRANTED
    }

    private fun observeLocationChanges() {
        locationLiveData.observe(this, Observer<Location> {
            lastLocation = it
            onLocationChanged(it)
        })
    }

    private fun requestLocationPermissions() {
        Log.info(TAG, "Requesting permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION")
        requestPermissions(
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSIONS
        )
    }
}
