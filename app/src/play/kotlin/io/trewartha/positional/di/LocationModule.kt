package io.trewartha.positional.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.domain.location.PlayLocationRepository
import io.trewartha.positional.domain.location.LocationRepository

@Module
@InstallIn(ViewModelComponent::class)
interface LocationModule {

    @Binds
    fun locationRepository(playLocationRepository: PlayLocationRepository): LocationRepository

    companion object {

        @Provides
        fun fusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }
}
