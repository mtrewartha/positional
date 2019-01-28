package io.trewartha.positional.ui.location.coordinates

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.trewartha.positional.ui.sun.DawnFragment
import io.trewartha.positional.ui.sun.DuskFragment

class SunFragmentPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val fragments = arrayOf(
        DawnFragment(),
        DuskFragment()
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}
