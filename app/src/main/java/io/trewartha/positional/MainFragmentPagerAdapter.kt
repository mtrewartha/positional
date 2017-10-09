package io.trewartha.positional

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.trewartha.positional.compass.CompassFragment
import io.trewartha.positional.map.MapFragment
import io.trewartha.positional.position.PositionFragment

class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    companion object {
        private val FRAGMENTS = arrayOf(
                MapFragment(),
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
