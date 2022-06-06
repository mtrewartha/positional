package io.trewartha.positional.ui

import io.trewartha.positional.R

sealed class Screen {

    abstract val route: String

    object Help : Screen() {
        override val route = "help"
    }

    sealed class BottomNavigable : Screen() {

        abstract val navIconRes: Int
        abstract val navLabelRes: Int

        object Compass : BottomNavigable() {
            override val route = "compass"
            override val navIconRes = R.drawable.ic_twotone_explore_24px
            override val navLabelRes = R.string.bottom_navigation_compass
        }

        object Location : BottomNavigable() {
            override val route = "location"
            override val navIconRes = R.drawable.ic_twotone_my_location_24px
            override val navLabelRes = R.string.bottom_navigation_location
        }

        object Settings : BottomNavigable() {
            override val route = "settings"
            override val navIconRes = R.drawable.ic_twotone_settings_24px
            override val navLabelRes = R.string.bottom_navigation_settings
        }

        object Sun : BottomNavigable() {
            override val route = "sun"
            override val navIconRes = R.drawable.ic_twotone_wb_sunny_24px
            override val navLabelRes = R.string.bottom_navigation_sun
        }
    }
}
