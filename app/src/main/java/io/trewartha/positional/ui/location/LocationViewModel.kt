package io.trewartha.positional.ui.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.trewartha.positional.location.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val location by lazy { LocationLiveData(getApplication()) }

}