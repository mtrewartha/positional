package io.trewartha.positional.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.trewartha.positional.ui.compass.CompassFragment
import io.trewartha.positional.ui.map.MapFragment
import io.trewartha.positional.ui.position.PositionFragment
import io.trewartha.positional.ui.profile.ProfileFragment
import io.trewartha.positional.ui.tracks.TracksFragment

class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val FRAGMENTS = arrayOf(
                MapFragment(),
                TracksFragment(),
                PositionFragment(),
                CompassFragment(),
                ProfileFragment()
        )
    }

    override fun getItem(position: Int): Fragment = FRAGMENTS[position]

    override fun getCount(): Int = FRAGMENTS.size
}
