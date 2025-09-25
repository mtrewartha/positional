package io.trewartha.positional.settings.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.R as CoreR
import io.trewartha.positional.settings.CompassMode
import io.trewartha.positional.settings.CompassNorthVibration
import io.trewartha.positional.settings.CoordinatesFormat
import io.trewartha.positional.settings.LocationAccuracyVisibility
import io.trewartha.positional.settings.Theme

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
public fun SettingsView(
    compassMode: CompassMode?,
    onCompassModeChange: (CompassMode) -> Unit,
    compassNorthVibration: CompassNorthVibration?,
    onCompassNorthVibrationChange: (CompassNorthVibration) -> Unit,
    coordinatesFormat: CoordinatesFormat?,
    onCoordinatesFormatChange: (CoordinatesFormat) -> Unit,
    locationAccuracyVisibility: LocationAccuracyVisibility?,
    onLocationAccuracyVisibilityChange: (LocationAccuracyVisibility) -> Unit,
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
                title = { Text(text = stringResource(R.string.feature_settings_ui_title)) },
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
                .padding(dimensionResource(CoreR.dimen.core_ui_standard_padding)),
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
            CompassNorthVibrationSetting(
                value = compassNorthVibration,
                onValueChange = onCompassNorthVibrationChange,
                modifier = Modifier.fillMaxWidth()
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocationAccuracyVisibilitySetting(
                    value = locationAccuracyVisibility,
                    onValueChange = onLocationAccuracyVisibilityChange,
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
        Text(text = stringResource(id = R.string.feature_settings_ui_license_title))
    }
}

@Composable
private fun PrivacyPolicyButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier) {
        Text(text = stringResource(id = R.string.feature_settings_ui_privacy_policy_title))
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreviews() {
    PositionalTheme {
        SettingsView(
            compassMode = null,
            onCompassModeChange = {},
            compassNorthVibration = null,
            onCompassNorthVibrationChange = {},
            coordinatesFormat = null,
            onCoordinatesFormatChange = {},
            locationAccuracyVisibility = null,
            onLocationAccuracyVisibilityChange = {},
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
            compassNorthVibration = CompassNorthVibration.SHORT,
            onCompassNorthVibrationChange = {},
            coordinatesFormat = CoordinatesFormat.DD,
            onCoordinatesFormatChange = {},
            locationAccuracyVisibility = LocationAccuracyVisibility.SHOW,
            onLocationAccuracyVisibilityChange = {},
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
