package io.trewartha.positional.ui.position

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.trewartha.positional.location.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData by lazy { LocationLiveData(getApplication()) }

    fun getLocation(): LocationLiveData {
        return locationLiveData
    }
}