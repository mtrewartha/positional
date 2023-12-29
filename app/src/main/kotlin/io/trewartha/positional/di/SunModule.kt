package io.trewartha.positional.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.data.sun.LibrarySolarTimesRepository
import io.trewartha.positional.data.sun.SolarTimesRepository
import io.trewartha.positional.domain.sun.DefaultGetSolarTimesUseCase
import io.trewartha.positional.domain.sun.GetSolarTimesUseCase

@Module
@InstallIn(ViewModelComponent::class)
interface SunModule {

    @Binds
    fun getSolarTimesUseCase(
        defaultGetSolarTimesUseCase: DefaultGetSolarTimesUseCase
    ): GetSolarTimesUseCase

    @Binds
    fun solarTimesRepository(
        librarySolarTimesRepository: LibrarySolarTimesRepository
    ): SolarTimesRepository
}
