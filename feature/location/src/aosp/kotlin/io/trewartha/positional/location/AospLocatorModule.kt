package io.trewartha.positional.location

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface AospLocatorModule {

    @Binds
    @Singleton
    fun locator(aospLocator: AospLocator): Locator
}