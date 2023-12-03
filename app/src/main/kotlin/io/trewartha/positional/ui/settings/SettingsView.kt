package io.trewartha.positional.ui.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.ui.Theme
import io.trewartha.positional.ui.NavDestination.Settings
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.bottomNavEnterTransition
import io.trewartha.positional.ui.bottomNavExitTransition
import io.trewartha.positional.ui.bottomNavPopEnterTransition
import io.trewartha.positional.ui.bottomNavPopExitTransition

fun NavGraphBuilder.settingsView(
    contentPadding: PaddingValues,
    onLicenseClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    composable(
        Settings.route,
        enterTransition = bottomNavEnterTransition(),
        exitTransition = bottomNavExitTransition(),
        popEnterTransition = bottomNavPopEnterTransition(),
        popExitTransition = bottomNavPopExitTransition()
    ) {
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
            contentPadding = contentPadding,
            onUnitsChange = viewModel::onUnitsChange,
            onLicenseClick = onLicenseClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun SettingsView(
    compassMode: CompassMode?,
    onCompassModeChange: (CompassMode) -> Unit,
    coordinatesFormat: CoordinatesFormat?,
    onCoordinatesFormatChange: (CoordinatesFormat) -> Unit,
    showAccuracies: Boolean?,
    onShowAccuraciesChange: (Boolean) -> Unit,
    theme: Theme?,
    units: Units?,
    contentPadding: PaddingValues,
    onThemeChange: (Theme) -> Unit,
    onUnitsChange: (Units) -> Unit,
    onLicenseClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        )
    ) { innerContentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerContentPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.standard_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
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
            LicenseButton(onLicenseClick, modifier = Modifier.padding(top = 16.dp))
            PrivacyPolicyButton(onPrivacyPolicyClick)
        }
    }
}

@Composable
private fun LicenseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier) {
        Text(text = stringResource(id = R.string.settings_license_title))
    }
}

@Composable
private fun PrivacyPolicyButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier) {
        Text(text = stringResource(id = R.string.settings_privacy_policy_title))
    }
}

@PreviewLightDark
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
            units = null,
            contentPadding = PaddingValues(),
            onThemeChange = {},
            onUnitsChange = {},
            onLicenseClick = {},
            onPrivacyPolicyClick = {}
        )
    }
}

@PreviewLightDark
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
            units = Units.METRIC,
            contentPadding = PaddingValues(),
            onThemeChange = {},
            onUnitsChange = {},
            onLicenseClick = {},
            onPrivacyPolicyClick = {}
        )
    }
}
