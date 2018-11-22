package io.trewartha.positional.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.trewartha.positional.ui.compass.CompassFragment
import io.trewartha.positional.ui.location.LocationFragment
import io.trewartha.positional.ui.settings.SettingsFragment

class MainFragmentPagerAdapter(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val fragments = arrayOf(
            LocationFragment(),
            CompassFragment(),
            SettingsFragment()
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}
