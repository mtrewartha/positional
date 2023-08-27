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

    data object CompassInfo : NavDestination {
        override val route = "compass/info"
    }

    data object Location : MainNavDestination {
        override val route = "location"
        override val navIcon = Icons.Rounded.MyLocation
        override val navLabelRes = R.string.bottom_navigation_location
    }

    data object LocationInfo : NavDestination {
        override val route = "location/info"
    }

    data object Settings : MainNavDestination {
        override val route = "settings"
        override val navIcon = Icons.Rounded.Settings
        override val navLabelRes = R.string.bottom_navigation_settings
    }

    data object Solunar : MainNavDestination {
        override val route = "solunar"
        override val navIcon = Icons.Rounded.WbTwilight
        override val navLabelRes = R.string.bottom_navigation_solunar
    }

    data object SolunarInfo : NavDestination {
        override val route = "solunar/info"
    }
}
