package io.trewartha.positional.di

import android.content.Context
import android.location.LocationManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.location.AospLocator
import io.trewartha.positional.data.location.Locator

@Module
@InstallIn(ViewModelComponent::class)
interface LocationModule {

    @Binds
    fun locator(aospLocator: AospLocator): Locator

    companion object {

        @Provides
        fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
