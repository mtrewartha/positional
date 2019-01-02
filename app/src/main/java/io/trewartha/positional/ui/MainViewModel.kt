package io.trewartha.positional.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.trewartha.positional.R
import io.trewartha.positional.common.SharedPreferenceStringLiveData
import io.trewartha.positional.compass.CompassLiveData
import io.trewartha.positional.location.LocationLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val compass by lazy { CompassLiveData(getApplication()) }

    val deviceHasAccelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    val deviceHasMagnetometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
    }

    val location by lazy { LocationLiveData(getApplication()) }

    val themeMode: LiveData<ThemeMode> by lazy {
        Transformations.map(themeModePreferenceLiveData) { ThemeMode.valueOf(it.toUpperCase()) }
    }

    private val sensorManager by lazy {
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val sharedPreferences by lazy {
        application.getSharedPreferences(
            application.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
        )
    }

    private val themeModePreferenceLiveData by lazy {
        SharedPreferenceStringLiveData(
            sharedPreferences,
            application.getString(R.string.settings_theme_mode_key),
            ThemeMode.AUTO.name
        )
    }
}