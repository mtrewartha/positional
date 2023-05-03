package io.trewartha.positional.di.viewmodel

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.trewartha.positional.domain.settings.DataStoreSettingsRepository
import io.trewartha.positional.domain.settings.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    @Provides
    @Singleton
    fun settingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = DataStoreSettingsRepository(context)
}
