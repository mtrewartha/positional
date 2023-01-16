package io.trewartha.positional.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R

@Composable
fun ShowAccuraciesSetting(
    value: Boolean?,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SwitchSetting(
        icon = Icons.Rounded.Adjust,
        title = stringResource(R.string.settings_show_accuracies_title),
        description = { stringResource(R.string.settings_show_accuracies_description) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth()
    )
}
