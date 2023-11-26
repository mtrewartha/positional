package io.trewartha.positional.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.R

sealed interface NavDestination {

    val route: String

    sealed interface MainNavDestination : NavDestination {
        val navIcon: ImageVector
        val navLabelRes: Int
    }

    data object Compass : MainNavDestination {
        override val route = "compass"
        override val navIcon = Icons.Rounded.Explore
        override val navLabelRes = R.string.bottom_navigation_compass
    }

    data object CompassHelp : NavDestination {
        override val route = "compass/help"
    }

    data object Location : MainNavDestination {
        override val route = "location"
        override val navIcon = Icons.Rounded.MyLocation
        override val navLabelRes = R.string.bottom_navigation_location
    }

    data object LocationHelp : NavDestination {
        override val route = "location/help"
    }

    data object Settings : MainNavDestination {
        override val route = "settings"
        override val navIcon = Icons.Rounded.Settings
        override val navLabelRes = R.string.bottom_navigation_settings
    }

    data object Sun : MainNavDestination {
        override val route = "sun"
        override val navIcon = Icons.Rounded.WbTwilight
        override val navLabelRes = R.string.bottom_navigation_sun
    }

    data object SunHelp : NavDestination {
        override val route = "sun/help"
    }
}
