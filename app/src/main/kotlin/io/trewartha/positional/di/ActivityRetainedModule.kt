package io.trewartha.positional.di

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorManager
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.AndroidFusedLocator
import io.trewartha.positional.domain.entities.Locator
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    fun fusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    fun locator(fusedLocationProviderClient: FusedLocationProviderClient): Locator =
        AndroidFusedLocator(Dispatchers.IO, fusedLocationProviderClient)

    @Provides
    fun sensorManager(@ApplicationContext context: Context): SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @Provides
    @ActivityRetainedScoped
    fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return with(context) {
            getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE)
        }
    }

    @Provides
    fun windowManager(@ApplicationContext context: Context): WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}