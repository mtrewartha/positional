package io.trewartha.positional.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.ui.PositionalTheme

@Composable
fun UnitsSetting(
    value: Units?,
    onValueChange: (Units) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSetting(
        icon = Icons.Rounded.Straighten,
        title = stringResource(R.string.feature_settings_ui_units_title),
        values = Units.entries.toSet(),
        value = value,
        valueName = { units ->
            stringResource(
                when (units) {
                    Units.IMPERIAL -> R.string.feature_settings_ui_units_value_imperial_name
                    Units.METRIC -> R.string.feature_settings_ui_units_value_metric_name
                }
            )
        },
        valuesDialogTitle = stringResource(R.string.feature_settings_ui_units_dialog_title),
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
            UnitsSetting(value = Units.METRIC, onValueChange = {})
        }
    }
}
