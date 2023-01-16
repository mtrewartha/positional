package io.trewartha.positional.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews

@Composable
fun SettingsView(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val compassMode by viewModel.compassMode.collectAsState(initial = null)
    val coordinatesFormat by viewModel.coordinatesFormat.collectAsState(initial = null)
    val showAccuracies by viewModel.showAccuracies.collectAsState(initial = null)
    val theme by viewModel.theme.collectAsState(initial = null)
    val units by viewModel.units.collectAsState(initial = null)
    SettingsView(
        compassMode = compassMode,
        onCompassModeChange = viewModel::onCompassModeChange,
        coordinatesFormat = coordinatesFormat,
        onCoordinatesFormatChange = viewModel::onCoordinatesFormatChange,
        showAccuracies = showAccuracies,
        onShowAccuraciesChange = viewModel::onShowAccuraciesChange,
        theme = theme,
        onThemeChange = viewModel::onThemeChange,
        units = units,
        onUnitsChange = viewModel::onUnitsChange,
        onNavigateToPrivacyPolicy = {
            TODO()
        }
    )
}

@Composable
private fun SettingsView(
    compassMode: CompassMode?,
    onCompassModeChange: (CompassMode) -> Unit,
    coordinatesFormat: CoordinatesFormat?,
    onCoordinatesFormatChange: (CoordinatesFormat) -> Unit,
    showAccuracies: Boolean?,
    onShowAccuraciesChange: (Boolean) -> Unit,
    theme: Theme?,
    onThemeChange: (Theme) -> Unit,
    units: Units?,
    onUnitsChange: (Units) -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            ThemeSetting(
                value = theme,
                onValueChange = onThemeChange,
                modifier = Modifier.fillMaxWidth()
            )
            UnitsSetting(
                value = units,
                onValueChange = onUnitsChange,
                modifier = Modifier.fillMaxWidth()
            )
            CoordinatesFormatSetting(
                value = coordinatesFormat,
                onValueChange = onCoordinatesFormatChange,
                modifier = Modifier.fillMaxWidth()
            )
            CompassModeSetting(
                value = compassMode,
                onValueChange = onCompassModeChange,
                modifier = Modifier.fillMaxWidth()
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ShowAccuraciesSetting(
                    value = showAccuracies,
                    onValueChange = onShowAccuraciesChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // onNavigateToPrivacyPolicy()
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingPreviews() {
    SettingsView(
        compassMode = null,
        onCompassModeChange = {},
        coordinatesFormat = null,
        onCoordinatesFormatChange = {},
        showAccuracies = null,
        onShowAccuraciesChange = {},
        theme = null,
        onThemeChange = {},
        units = null,
        onUnitsChange = {},
        onNavigateToPrivacyPolicy = {}
    )
}

@ThemePreviews
@Composable
private fun LoadedPreviews() {
    PositionalTheme {
        SettingsView(
            compassMode = CompassMode.TRUE_NORTH,
            onCompassModeChange = {},
            coordinatesFormat = CoordinatesFormat.DD,
            onCoordinatesFormatChange = {},
            showAccuracies = true,
            onShowAccuraciesChange = {},
            theme = Theme.DEVICE,
            onThemeChange = {},
            units = Units.METRIC,
            onUnitsChange = {},
            onNavigateToPrivacyPolicy = {}
        )
    }
}
