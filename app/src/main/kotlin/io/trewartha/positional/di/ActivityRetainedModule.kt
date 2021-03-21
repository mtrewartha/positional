package io.trewartha.positional.di

import android.content.ClipboardManager
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
import io.trewartha.positional.R

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    fun clipboardManager(
        @ApplicationContext context: Context
    ): ClipboardManager {
        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    @Provides
    fun fusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun sensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    @Provides
    fun sharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.settings_filename),
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun windowManager(
        @ApplicationContext context: Context
    ): WindowManager {
        return context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}