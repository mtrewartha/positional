package io.trewartha.positional.ui

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import io.trewartha.positional.R
import io.trewartha.positional.common.SharedPreferenceStringLiveData
import io.trewartha.positional.compass.CompassLiveData
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.sun.SunLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val compassViewData by lazy { CompassLiveData(getApplication()) }

    val deviceHasAccelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    val deviceHasMagnetometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
    }

    val location by lazy { LocationLiveData(getApplication()) }

    val sunViewData by lazy { SunLiveData(getApplication()) }

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
                application.getString(R.string.settings_theme_key),
                Theme.SYSTEM.name
        )
    }
}