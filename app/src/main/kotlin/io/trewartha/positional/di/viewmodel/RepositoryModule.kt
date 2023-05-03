package io.trewartha.positional.di.viewmodel

import android.content.Context
import android.hardware.SensorManager
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.domain.compass.AndroidCompassReadingsRepository
import io.trewartha.positional.domain.compass.CompassReadingsRepository
import io.trewartha.positional.domain.location.AndroidFusedLocationRepository
import io.trewartha.positional.domain.location.LocationRepository
import io.trewartha.positional.domain.settings.DataStoreSettingsRepository
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.solunar.LocalSolarTimesRepository
import io.trewartha.positional.domain.solunar.SolarTimesRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun compassReadingsRepository(
        sensorManager: SensorManager
    ): CompassReadingsRepository = AndroidCompassReadingsRepository(sensorManager)

    @Provides
    fun locationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient
    ): LocationRepository = AndroidFusedLocationRepository(
        coroutineDispatcher = Dispatchers.IO,
        fusedLocationProviderClient = fusedLocationProviderClient
    )

    @Provides
    fun solarTimesRepository(): SolarTimesRepository = LocalSolarTimesRepository()
}
