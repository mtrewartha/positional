package io.trewartha.positional.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.settings.SettingsRepository
import io.trewartha.positional.data.ui.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: Flow<Theme> = settingsRepository.theme
}
