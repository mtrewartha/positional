package io.trewartha.positional.core.ui.nav

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

/** Navigation key for a top-level tab destination. */
public interface MainNavKey : NavKey {
    /** Icon shown for this destination in the navigation bar */
    public val navIcon: ImageVector
    /** String resource for this destination's navigation bar label */
    public val navLabelRes: Int
}
