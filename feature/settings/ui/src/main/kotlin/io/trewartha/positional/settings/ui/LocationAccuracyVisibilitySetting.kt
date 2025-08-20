package io.trewartha.positional.settings.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.settings.LocationAccuracyVisibility

@Composable
fun LocationAccuracyVisibilitySetting(
    value: LocationAccuracyVisibility?,
    onValueChange: (LocationAccuracyVisibility) -> Unit,
    modifier: Modifier = Modifier
) {
    SwitchSetting(
        icon = Icons.Rounded.Adjust,
        title = stringResource(R.string.feature_settings_ui_show_accuracies_title),
        description = { stringResource(R.string.feature_settings_ui_show_accuracies_description) },
        value = when (value) {
            LocationAccuracyVisibility.HIDE -> false
            LocationAccuracyVisibility.SHOW -> true
            null -> null
        },
        onValueChange = {
            val visibility =
                if (it) LocationAccuracyVisibility.SHOW else LocationAccuracyVisibility.HIDE
            onValueChange(visibility)
        },
        modifier = modifier.fillMaxWidth()
    )
}
