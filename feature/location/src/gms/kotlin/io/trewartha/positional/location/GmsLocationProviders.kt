package io.trewartha.positional.location

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
public interface GmsLocationProviders {

    @Provides
    public fun fusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
}
