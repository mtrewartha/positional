package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.ui.position.LocationViewModel

abstract class LocationAwareFragment : Fragment() {

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
                .location
        locationLiveData.updatePriority = getLocationUpdatePriority()
        locationLiveData.updateInterval = getLocationUpdateInterval()
        locationLiveData.updateMaxWaitTime = getLocationUpdateMaxWaitTime()
    }

    override fun onResume() {
        super.onResume()
        if (haveLocationPermissions()) {
            locationLiveData.observe(this, Observer<Location> {
                lastLocation = it
                onLocationChanged(it)
            })
        }
    }

    override fun onPause() {
        super.onPause()
        locationLiveData.removeObservers(this)
    }

    private fun haveLocationPermissions(): Boolean {
        val context = context ?: return false
        return arrayOf(
                ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION),
                ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
        ).all { it == PackageManager.PERMISSION_GRANTED }
    }
}
