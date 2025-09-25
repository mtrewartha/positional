package io.trewartha.positional.location.ui

import android.content.Context
import android.location.LocationManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.location.AospLocator
import io.trewartha.positional.location.Locator

@Module
@InstallIn(ViewModelComponent::class)
public interface AospLocatorModule {

    @Binds
    public fun aospLocator(aospLocator: AospLocator): Locator

    public companion object {

        @Provides
        public fun locationManager(
            @ApplicationContext context: Context
        ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}