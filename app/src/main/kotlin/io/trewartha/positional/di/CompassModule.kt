package io.trewartha.positional.di

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.compass.AospCompass
import io.trewartha.positional.data.compass.Compass
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

@Module
@InstallIn(ViewModelComponent::class)
interface CompassModule {

    companion object {

        @Provides
        fun compass(sensorManager: SensorManager): Compass? =
            try {
                AospCompass(
                    Dispatchers.Default,
                    sensorManager,
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)),
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)),
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR))
                )
            } catch (_: IllegalArgumentException) {
                Timber.w("Unable to find sensors required for compass")
                null
            }

        @Provides
        fun sensorManager(
            @ApplicationContext context: Context
        ): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
