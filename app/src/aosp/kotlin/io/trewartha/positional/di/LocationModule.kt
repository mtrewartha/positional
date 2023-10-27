package io.trewartha.positional.di

import android.content.Context
import android.location.LocationManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.location.AospLocationRepository
import io.trewartha.positional.data.location.LocationRepository

@Module
@InstallIn(ViewModelComponent::class)
interface LocationModule {

    @Binds
    fun locationRepository(aospLocationRepository: AospLocationRepository): LocationRepository

    companion object {

        @Provides
        fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
