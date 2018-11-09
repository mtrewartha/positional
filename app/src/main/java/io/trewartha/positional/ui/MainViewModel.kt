package io.trewartha.positional.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.trewartha.positional.R
import io.trewartha.positional.common.SharedPreferenceStringLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(
            application.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
    )

    private val themeModePreferenceLiveData = SharedPreferenceStringLiveData(
            sharedPreferences,
            application.getString(R.string.settings_theme_mode_key),
            ThemeMode.AUTO.name
    )

    val themeMode: LiveData<ThemeMode> = Transformations.map(themeModePreferenceLiveData) {
        ThemeMode.valueOf(it.toUpperCase())
    }
}