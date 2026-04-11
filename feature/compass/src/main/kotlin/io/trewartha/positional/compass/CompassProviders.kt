package io.trewartha.positional.compass

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
public interface CompassProviders {

    @Provides
    public fun compass(sensorManager: SensorManager): Compass? =
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
    public fun sensorManager(application: Application): SensorManager =
        application.getSystemService(SensorManager::class.java)
}
