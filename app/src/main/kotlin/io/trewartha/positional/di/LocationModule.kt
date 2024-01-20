package io.trewartha.positional.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module(includes = [LocatorModule::class])
@InstallIn(ViewModelComponent::class)
interface LocationModule
