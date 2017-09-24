package io.trewartha.positional.position

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationLiveData by lazy { LocationLiveData(getApplication()) }

    fun getLocation(): LocationLiveData {
        return locationLiveData
    }
}