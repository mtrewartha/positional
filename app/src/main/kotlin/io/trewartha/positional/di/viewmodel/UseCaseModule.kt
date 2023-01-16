package io.trewartha.positional.di.viewmodel

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.domain.compass.CompassReadingsRepository
import io.trewartha.positional.domain.compass.GetCompassDeclinationUseCase
import io.trewartha.positional.domain.compass.GetCompassModeUseCase
import io.trewartha.positional.domain.compass.GetCompassReadingsUseCase
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.location.LocationRepository
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.twilight.GetDailyTwilightsUseCase
import io.trewartha.positional.domain.twilight.TwilightRepository

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

    @Provides
    fun getCompassDeclinationUseCase(
        getLocationUseCase: GetLocationUseCase
    ): GetCompassDeclinationUseCase = GetCompassDeclinationUseCase(getLocationUseCase)

    @Provides
    fun getCompassModeUseCase(
        settingsRepository: SettingsRepository
    ): GetCompassModeUseCase = GetCompassModeUseCase(settingsRepository)

    @Provides
    fun getCompassReadingsUseCase(
        compassReadingsRepository: CompassReadingsRepository
    ): GetCompassReadingsUseCase = GetCompassReadingsUseCase(compassReadingsRepository)

    @Provides
    fun getLocationUseCase(
        locationRepository: LocationRepository
    ): GetLocationUseCase = GetLocationUseCase(locationRepository)

    @Provides
    fun getSolarTimesUseCase(
        twilightRepository: TwilightRepository
    ): GetDailyTwilightsUseCase = GetDailyTwilightsUseCase(twilightRepository)
}
