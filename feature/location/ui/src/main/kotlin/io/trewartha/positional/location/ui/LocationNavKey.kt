package io.trewartha.positional.location.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.core.ui.nav.MainNavKey
import kotlinx.serialization.Serializable

@Serializable
public data object LocationNavKey : MainNavKey {
    override val navIcon: ImageVector = Icons.Rounded.MyLocation
    override val navLabelRes: Int = R.string.feature_location_ui_title
}
