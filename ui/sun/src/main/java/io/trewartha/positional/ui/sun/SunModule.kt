package io.trewartha.positional.ui.sun

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.data.sun.MainSolarTimesRepository
import io.trewartha.positional.data.sun.SolarTimesRepository

@Module
@InstallIn(ViewModelComponent::class)
interface SunModule {

    @Binds
    fun solarTimesRepository(
        mainSolarTimesRepository: MainSolarTimesRepository
    ): SolarTimesRepository
}
