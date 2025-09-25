package io.trewartha.positional.settings.ui

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.trewartha.positional.settings.DataStoreSettingsRepository
import io.trewartha.positional.settings.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public class SettingsModule {

    @Provides
    @Singleton
    public fun settingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = DataStoreSettingsRepository(context)
}
