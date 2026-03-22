package io.trewartha.positional.settings.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.core.ui.nav.MainNavKey
import kotlinx.serialization.Serializable

@Serializable
public data object SettingsNavKey : MainNavKey {
    override val navIcon: ImageVector = Icons.Rounded.Settings
    override val navLabelRes: Int = R.string.feature_settings_ui_title
}
