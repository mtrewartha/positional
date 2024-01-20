package io.trewartha.positional.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.data.sun.LibrarySolarTimesRepository
import io.trewartha.positional.data.sun.SolarTimesRepository

@Module
@InstallIn(ViewModelComponent::class)
interface SunModule {

    @Binds
    fun solarTimesRepository(
        librarySolarTimesRepository: LibrarySolarTimesRepository
    ): SolarTimesRepository
}
