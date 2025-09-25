package io.trewartha.positional.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.settings.Theme

@Composable
public fun ThemeSetting(
    value: Theme?,
    onValueChange: (Theme) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.DarkMode,
        title = stringResource(R.string.feature_settings_ui_theme_title),
        values = Theme.entries.toSet(),
        value = value,
        valueName = { theme ->
            stringResource(
                when (theme) {
                    Theme.DEVICE -> R.string.feature_settings_ui_theme_value_device_name
                    Theme.DARK -> R.string.feature_settings_ui_theme_value_dark_name
                    Theme.LIGHT -> R.string.feature_settings_ui_theme_value_light_name
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.feature_settings_ui_theme_dialog_title),
        valuesDialogText = null,
        onValueChange = onValueChange,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun Previews() {
    PositionalTheme {
        Surface {
            ThemeSetting(value = Theme.DEVICE, onValueChange = {})
        }
    }
}
