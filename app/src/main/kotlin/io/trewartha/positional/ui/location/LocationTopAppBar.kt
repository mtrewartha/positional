package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.IconToggleButton

@Composable
fun LocationTopAppBar(
    state: LocationState?,
    onHelpClick: () -> Unit,
    onScreenLockCheckedChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = {},
        actions = {
            IconToggleButton(
                checked = state?.screenLockEnabled ?: false,
                onCheckedChange = onScreenLockCheckedChange,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ScreenLockPortrait,
                    contentDescription = stringResource(
                        if (state?.screenLockEnabled == true)
                            R.string.location_screen_lock_button_content_description_on
                        else
                            R.string.location_screen_lock_button_content_description_off
                    ),
                )
            }
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Rounded.HelpOutline,
                    contentDescription = stringResource(R.string.location_help_button_content_description),
                )
            }
        }
    )
}
