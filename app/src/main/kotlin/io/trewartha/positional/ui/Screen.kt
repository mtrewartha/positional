package io.trewartha.positional.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import io.trewartha.positional.R

sealed class Screen {

    abstract val route: String

    object Help : Screen() {
        override val route = "help"
    }

    sealed class BottomNavigable : Screen() {

        abstract val navIcon: ImageVector
        abstract val navLabelRes: Int

        object Compass : BottomNavigable() {
            override val route = "compass"
            override val navIcon = Icons.Rounded.Explore
            override val navLabelRes = R.string.bottom_navigation_compass
        }

        object Location : BottomNavigable() {
            override val route = "location"
            override val navIcon = Icons.Rounded.MyLocation
            override val navLabelRes = R.string.bottom_navigation_location
        }

        object Settings : BottomNavigable() {
            override val route = "settings"
            override val navIcon = Icons.Rounded.Settings
            override val navLabelRes = R.string.bottom_navigation_settings
        }

        object Twilight : BottomNavigable() {
            override val route = "twilight"
            override val navIcon = Icons.Rounded.WbSunny
            override val navLabelRes = R.string.bottom_navigation_twilight
        }
    }
}
