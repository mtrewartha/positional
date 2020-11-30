package io.trewartha.positional.ui.location.help

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.trewartha.positional.R

class LocationHelpViewModel(private val app: Application) : AndroidViewModel(app) {

    val helpContent: String = app.resources.openRawResource(R.raw.location_help).reader().readText()
}