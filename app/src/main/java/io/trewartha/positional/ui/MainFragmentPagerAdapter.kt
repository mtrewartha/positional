package io.trewartha.positional.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val FRAGMENTS = arrayOf(
                MapFragment(),
                TracksFragment(),
                PositionFragment(),
                CompassFragment()
        )
    }

    override fun getItem(position: Int): Fragment {
        return FRAGMENTS[position]
    }

    override fun getCount(): Int {
        return FRAGMENTS.size
    }
}
