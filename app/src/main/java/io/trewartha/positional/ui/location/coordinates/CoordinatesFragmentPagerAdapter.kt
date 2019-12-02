package io.trewartha.positional.ui.location.coordinates

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CoordinatesFragmentPagerAdapter(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = arrayOf(
            CoordinatesDecimalDegreesFragment(),
            CoordinatesDegreesDecimalMinutesFragment(),
            CoordinatesDegreesMinutesSecondsFragment(),
            CoordinatesUTMFragment(),
            CoordinatesMGRSFragment()
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}
