package io.trewartha.positional.di.viewmodel

import android.hardware.SensorManager
import androidx.datastore.core.DataStore
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.data.settings.SettingsProto
import io.trewartha.positional.domain.compass.AndroidCompassReadingsRepository
import io.trewartha.positional.domain.compass.CompassReadingsRepository
import io.trewartha.positional.domain.location.AndroidFusedLocationRepository
import io.trewartha.positional.domain.location.LocationRepository
import io.trewartha.positional.domain.settings.DataStoreSettingsRepository
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.solunar.LocalSolarTimesRepository
import io.trewartha.positional.domain.solunar.SolarTimesRepository
import kotlinx.coroutines.Dispatchers

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
    fun settingsRepository(
        settingsDataStore: DataStore<SettingsProto.Settings>
    ): SettingsRepository = DataStoreSettingsRepository(settingsDataStore)

    @Provides
    fun solarTimesRepository(): SolarTimesRepository = LocalSolarTimesRepository()
}
