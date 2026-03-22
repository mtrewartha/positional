package io.trewartha.positional.compass.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.core.ui.nav.MainNavKey
import kotlinx.serialization.Serializable

@Serializable
public data object CompassNavKey : MainNavKey {
    override val navIcon: ImageVector = Icons.Rounded.Explore
    override val navLabelRes: Int = R.string.feature_compass_ui_title
}
