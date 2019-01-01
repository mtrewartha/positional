package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.ui.location.LocationViewModel

abstract class LocationAwareFragment : Fragment() {

    abstract val locationUpdateInterval: Long
    abstract val locationUpdatePriority: Int

    abstract fun onLocationChanged(location: Location?)

    private lateinit var locationLiveData: LocationLiveData

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationLiveData = ViewModelProviders.of(this)
            .get(LocationViewModel::class.java)
            .location
        locationLiveData.updatePriority = locationUpdatePriority
        locationLiveData.updateInterval = locationUpdateInterval
    }

    override fun onResume() {
        super.onResume()
        if (haveLocationPermissions()) {
            locationLiveData.observe(this, Observer<Location> { onLocationChanged(it) })
        }
    }

    override fun onPause() {
        super.onPause()
        locationLiveData.removeObservers(this)
    }

    private fun haveLocationPermissions(): Boolean =
        checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
}
