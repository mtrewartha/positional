package io.trewartha.positional.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.trewartha.positional.model.settings.CompassMode
import io.trewartha.positional.ui.design.PositionalTheme

@Composable
fun CompassModeSetting(
    value: CompassMode?,
    onValueChange: (CompassMode) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.North,
        title = stringResource(R.string.ui_settings_compass_mode_title),
        values = setOf(CompassMode.MAGNETIC_NORTH, CompassMode.TRUE_NORTH),
        value = value,
        valueName = { compassMode ->
            stringResource(
                when (compassMode) {
                    CompassMode.MAGNETIC_NORTH ->
                        R.string.ui_settings_compass_mode_value_magnetic_name
                    CompassMode.TRUE_NORTH ->
                        R.string.ui_settings_compass_mode_value_true_name
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.ui_settings_compass_mode_dialog_title),
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
            CompassModeSetting(value = CompassMode.MAGNETIC_NORTH, onValueChange = {})
        }
    }
}
