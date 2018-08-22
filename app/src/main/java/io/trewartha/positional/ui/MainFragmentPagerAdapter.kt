package io.trewartha.positional.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.trewartha.positional.ui.compass.CompassFragment
import io.trewartha.positional.ui.position.PositionFragment
import io.trewartha.positional.ui.settings.SettingsFragment

class MainFragmentPagerAdapter(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val FRAGMENTS = arrayOf(
                PositionFragment(),
                CompassFragment(),
                SettingsFragment()
        )
    }

    override fun getItem(position: Int): Fragment = FRAGMENTS[position]

    override fun getCount(): Int = FRAGMENTS.size
}
