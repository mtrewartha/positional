package io.trewartha.positional

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import io.trewartha.positional.common.LocationAwareFragment.REQUEST_CODE_GOOGLE_PLAY_SERVICES
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    companion object {
        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }
    }

    private lateinit var fragmentPagerAdapter: MainFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        fragmentPagerAdapter = MainFragmentPagerAdapter(supportFragmentManager)
        viewPager.adapter = fragmentPagerAdapter
        viewPager.addOnPageChangeListener(PageChangeListener())

        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationItemSelectedListener())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES) {
            fragmentPagerAdapter.notifyDataSetChanged()
        }
    }

    private inner class NavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.main_bottom_navigation_position_menu_item -> viewPager.currentItem = 0
                R.id.main_bottom_navigation_compass_menu_item -> viewPager.currentItem = 1
            }
            return true
        }
    }

    private inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            val menu = bottomNavigationView.menu
            val positionMenuItem = menu.getItem(0)
            val compassMenuItem = menu.getItem(1)

            if (position == 0) {
                positionMenuItem.isChecked = true
                compassMenuItem.isChecked = false
            } else {
                positionMenuItem.isChecked = false
                compassMenuItem.isChecked = true
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }
}
