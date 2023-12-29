package io.trewartha.positional.di

import android.content.Context
import android.hardware.SensorManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.compass.AospCompass
import io.trewartha.positional.data.compass.Compass
import io.trewartha.positional.domain.compass.DefaultGetCompassReadingsUseCase
import io.trewartha.positional.domain.compass.GetCompassReadingsUseCase

@Module
@InstallIn(ViewModelComponent::class)
interface CompassModule {

    @Binds
    fun compass(aospCompass: AospCompass): Compass

    @Binds
    fun getCompassReadingsUseCase(
        defaultGetCompassReadingsUseCase: DefaultGetCompassReadingsUseCase
    ): GetCompassReadingsUseCase

    companion object {

        @Provides
        fun sensorManager(
            @ApplicationContext context: Context
        ): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
