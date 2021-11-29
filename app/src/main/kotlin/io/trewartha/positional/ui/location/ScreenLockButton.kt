package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ScreenLockPortrait
import androidx.compose.material.icons.twotone.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.trewartha.positional.R

@Composable
fun ScreenLockButton(
    screenLockEnabled: Boolean,
    onScreenLockCheckedChange: (Boolean) -> Unit
) {
    IconToggleButton(
        checked = screenLockEnabled,
        onCheckedChange = onScreenLockCheckedChange
    ) {
        Icon(
            imageVector = if (screenLockEnabled)
                Icons.TwoTone.Smartphone
            else
                Icons.TwoTone.ScreenLockPortrait,
            contentDescription = stringResource(
                if (screenLockEnabled) R.string.location_screen_lock_button_content_description_on
                else R.string.location_screen_lock_button_content_description_off
            )
        )
    }
}

@Preview("Screen locked")
@Composable
fun ScreenLockedPreview() {
    ScreenLockButton(screenLockEnabled = true, onScreenLockCheckedChange = {})
}

@Preview("Screen unlocked")
@Composable
fun ScreenUnlockedPreview() {
    ScreenLockButton(screenLockEnabled = false, onScreenLockCheckedChange = {})
}