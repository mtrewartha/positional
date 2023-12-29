package io.trewartha.positional.di

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.data.location.AospLocator
import io.trewartha.positional.data.location.GmsLocator
import io.trewartha.positional.data.location.Locator
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
interface LocatorModule {

    companion object {

        @Provides
        fun fusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        @Provides
        fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        @Provides
        fun locator(
            aospLocator: AospLocator,
            fusedLocationProviderClient: FusedLocationProviderClient
        ): Locator = GmsLocator(Dispatchers.IO, fusedLocationProviderClient, aospLocator)
    }
}
