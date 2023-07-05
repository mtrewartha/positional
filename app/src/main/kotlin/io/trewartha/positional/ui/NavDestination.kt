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

    sealed interface BottomNavDestination : NavDestination {
        val navIcon: ImageVector
        val navLabelRes: Int
    }

    object Compass : BottomNavDestination {
        override val route = "compass"
        override val navIcon = Icons.Rounded.Explore
        override val navLabelRes = R.string.bottom_navigation_compass
    }

    object CompassInfo : NavDestination {
        override val route = "compass/info"
    }

    object Location : BottomNavDestination {
        override val route = "location"
        override val navIcon = Icons.Rounded.MyLocation
        override val navLabelRes = R.string.bottom_navigation_location
    }

    object LocationInfo : NavDestination {
        override val route = "location/info"
    }

    object Settings : BottomNavDestination {
        override val route = "settings"
        override val navIcon = Icons.Rounded.Settings
        override val navLabelRes = R.string.bottom_navigation_settings
    }

    object Solunar : BottomNavDestination {
        override val route = "solunar"
        override val navIcon = Icons.Rounded.WbTwilight
        override val navLabelRes = R.string.bottom_navigation_solunar
    }

    object SolunarInfo : NavDestination {
        override val route = "solunar/info"
    }
}
