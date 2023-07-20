package io.trewartha.positional.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.domain.solunar.LibrarySolarTimesRepository
import io.trewartha.positional.domain.solunar.SolarTimesRepository

@Module
@InstallIn(ViewModelComponent::class)
interface SolunarModule {

    @Binds
    fun solarTimesRepository(
        librarySolarTimesRepository: LibrarySolarTimesRepository
    ): SolarTimesRepository
}
