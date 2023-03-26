package io.trewartha.positional.ui.settings

//import com.google.accompanist.navigation.animation.composable
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews

fun NavGraphBuilder.settingsView(
    onNavigateToPrivacyPolicy: () -> Unit
) {
    composable(Settings.route) {
        val viewModel: SettingsViewModel = hiltViewModel()
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
            onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy
        )
    }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
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
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            TextButton(onClick = onNavigateToPrivacyPolicy) {
                Text(text = stringResource(id = R.string.settings_privacy_policy_title))
            }
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingPreviews() {
    PositionalTheme {
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
