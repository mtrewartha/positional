package io.trewartha.positional

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.trewartha.positional.compass.CompassFragment
import io.trewartha.positional.position.PositionFragment

class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PositionFragment()
            else -> CompassFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
