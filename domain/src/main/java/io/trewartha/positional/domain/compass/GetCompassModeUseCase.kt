package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompassModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<CompassMode> = settingsRepository.compassMode
}
