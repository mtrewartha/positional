package io.trewartha.positional.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class CoordinatesFragmentPagerAdapter(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val fragments = arrayOf(
            CoordinatesDegreesDecimalFragment(),
            CoordinatesDegreesMinutesSecondsFragment(),
            CoordinatesUTMFragment(),
            CoordinatesMGRSFragment()
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
