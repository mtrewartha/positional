package io.trewartha.positional.di.viewmodel

import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class AndroidModule {

    @Provides
    fun fusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    fun locationManager(
        @ApplicationContext context: Context
    ): LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    fun sensorManager(
        @ApplicationContext context: Context
    ): SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
}
