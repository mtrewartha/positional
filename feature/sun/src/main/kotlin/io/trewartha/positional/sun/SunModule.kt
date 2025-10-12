package io.trewartha.positional.sun

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SunModule {

    @Binds
    fun solarTimesRepository(
        mainSolarTimesRepository: MainSolarTimesRepository
    ): SolarTimesRepository
}