package io.trewartha.positional.location

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
import io.trewartha.positional.location.GmsLocator
import io.trewartha.positional.location.Locator

@Module
@InstallIn(ViewModelComponent::class)
public interface GmsLocatorModule {

    @Binds
    public fun gmsLocator(gmsLocator: GmsLocator): Locator

    public companion object {

        @Provides
        public fun fusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        @Provides
        public fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
