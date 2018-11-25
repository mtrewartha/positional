package io.trewartha.positional.ui.compass

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import io.trewartha.positional.compass.CompassLiveData

class CompassViewModel(application: Application) : AndroidViewModel(application) {

    val compassLiveData by lazy { CompassLiveData(getApplication()) }

    val deviceHasAccelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    val deviceHasMagnetometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
    }

    private val sensorManager by lazy {
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}