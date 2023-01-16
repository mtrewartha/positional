package io.trewartha.positional.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R
import io.trewartha.positional.data.compass.CompassMode
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews

@Composable
fun CompassModeSetting(
    value: CompassMode?,
    onValueChange: (CompassMode) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.North,
        title = stringResource(R.string.settings_compass_mode_title),
        values = setOf(CompassMode.MAGNETIC_NORTH, CompassMode.TRUE_NORTH),
        value = value,
        valueName = { compassMode ->
            stringResource(
                when (compassMode) {
                    CompassMode.MAGNETIC_NORTH -> R.string.settings_compass_mode_value_magnetic_name
                    CompassMode.TRUE_NORTH -> R.string.settings_compass_mode_value_true_name
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.settings_compass_mode_dialog_title),
        valuesDialogText = null,
        onValueChange = onValueChange,
        modifier = modifier
    )
}

@ThemePreviews
@Composable
private fun Previews() {
    PositionalTheme {
        Surface {
            CompassModeSetting(value = CompassMode.MAGNETIC_NORTH, onValueChange = {})
        }
    }
}
