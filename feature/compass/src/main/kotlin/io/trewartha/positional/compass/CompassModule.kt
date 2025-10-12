package io.trewartha.positional.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
internal interface CompassModule {

    companion object {

        @Provides
        fun compass(sensorManager: SensorManager): Compass? =
            try {
                AospCompass(
                    sensorManager,
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)),
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)),
                    requireNotNull(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR))
                )
            } catch (_: IllegalArgumentException) {
                null
            }

        @Provides
        fun sensorManager(
            @ApplicationContext context: Context
        ): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}