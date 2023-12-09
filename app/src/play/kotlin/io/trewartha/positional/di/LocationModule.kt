package io.trewartha.positional.di

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.location.LocationRepository
import io.trewartha.positional.data.location.PlayLocationRepository

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

        @Provides
        fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
