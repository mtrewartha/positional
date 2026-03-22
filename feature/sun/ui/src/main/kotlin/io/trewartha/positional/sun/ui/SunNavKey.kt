package io.trewartha.positional.sun.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.core.ui.nav.MainNavKey
import kotlinx.serialization.Serializable

@Serializable
public data object SunNavKey : MainNavKey {
    override val navIcon: ImageVector = Icons.Rounded.WbTwilight
    override val navLabelRes: Int = R.string.feature_sun_ui_title
}
