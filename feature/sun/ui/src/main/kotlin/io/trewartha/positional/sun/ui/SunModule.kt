package io.trewartha.positional.sun.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.sun.MainSolarTimesRepository
import io.trewartha.positional.sun.SolarTimesRepository

@Module
@InstallIn(ViewModelComponent::class)
interface SunModule {

    @Binds
    fun solarTimesRepository(
        mainSolarTimesRepository: MainSolarTimesRepository
    ): SolarTimesRepository
}
