package io.trewartha.positional.di.viewmodel

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.domain.compass.AndroidCompassReadingsRepository
import io.trewartha.positional.domain.compass.CompassReadingsRepository
import io.trewartha.positional.domain.location.GmsLocationRepository
import io.trewartha.positional.domain.location.LocationRepository
import io.trewartha.positional.domain.solunar.LocalSolarTimesRepository
import io.trewartha.positional.domain.solunar.SolarTimesRepository

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun compassReadingsRepository(
        androidCompassReadingsRepository: AndroidCompassReadingsRepository
    ): CompassReadingsRepository

    @Binds
    fun locationRepository(
        gmsLocationRepository: GmsLocationRepository
    ): LocationRepository

    @Binds
    fun solarTimesRepository(
        localSolarTimesRepository: LocalSolarTimesRepository
    ): SolarTimesRepository
}
