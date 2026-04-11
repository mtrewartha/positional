package io.trewartha.positional.location

import android.app.Application
import android.location.LocationManager
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
public interface LocationProviders {

    @Provides
    public fun locationManager(application: Application): LocationManager =
        application.getSystemService(LocationManager::class.java)
}
