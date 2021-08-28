package io.trewartha.positional.ui.location

import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
            imageVector = ImageVector.vectorResource(
                if (screenLockEnabled) R.drawable.ic_twotone_smartphone_24px
                else R.drawable.ic_twotone_screen_lock_portrait_24px
            ),
            contentDescription = stringResource(
                if (screenLockEnabled) R.string.location_screen_lock_button_content_description_on
                else R.string.location_screen_lock_button_content_description_off
            ),
            tint = MaterialTheme.colors.primary
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