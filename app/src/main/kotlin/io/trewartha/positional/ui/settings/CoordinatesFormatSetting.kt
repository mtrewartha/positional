package io.trewartha.positional.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pin
import androidx.compose.material.icons.rounded._123
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews

@Composable
fun CoordinatesFormatSetting(
    value: CoordinatesFormat?,
    onValueChange: (CoordinatesFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.Pin,
        title = stringResource(R.string.settings_coordinates_format_title),
        values = CoordinatesFormat.values().toSet(),
        value = value,
        valueName = { coordinatesFormat ->
            stringResource(
                when (coordinatesFormat) {
                    CoordinatesFormat.DD -> R.string.settings_coordinates_format_value_dd_name
                    CoordinatesFormat.DDM -> R.string.settings_coordinates_format_value_ddm_name
                    CoordinatesFormat.DMS -> R.string.settings_coordinates_format_value_dms_name
                    CoordinatesFormat.MGRS -> R.string.settings_coordinates_format_value_mgrs_name
                    CoordinatesFormat.UTM -> R.string.settings_coordinates_format_value_utm_name
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.settings_coordinates_format_dialog_title),
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
            CoordinatesFormatSetting(value = CoordinatesFormat.DD, onValueChange = {})
        }
    }
}
