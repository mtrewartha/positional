package io.trewartha.positional.ui.location.coordinates

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

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
