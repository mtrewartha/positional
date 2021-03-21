package io.trewartha.positional.ui.location.help

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import javax.inject.Inject

@HiltViewModel
class LocationHelpViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {

    val helpContent: String = app.resources.openRawResource(R.raw.location_help).reader().readText()
}