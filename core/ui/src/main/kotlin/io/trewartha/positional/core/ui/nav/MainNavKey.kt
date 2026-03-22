package io.trewartha.positional.core.ui.nav

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

/** Navigation key for a top-level tab destination. */
public interface MainNavKey : NavKey {
    public val navIcon: ImageVector
    public val navLabelRes: Int
}
