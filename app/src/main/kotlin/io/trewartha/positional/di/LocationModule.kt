package io.trewartha.positional.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.domain.location.DefaultGetLocationUseCase
import io.trewartha.positional.domain.location.GetLocationUseCase

@Module(includes = [LocatorModule::class])
@InstallIn(ViewModelComponent::class)
interface LocationModule {

    @Binds
    fun getLocationUseCase(defaultGetLocationUseCase: DefaultGetLocationUseCase): GetLocationUseCase
}
