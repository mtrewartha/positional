package io.trewartha.positional.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        viewPager.adapter = MainFragmentPagerAdapter(supportFragmentManager)

        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationItemSelectedListener())
    }

    private inner class NavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.positionNavMenuItem -> viewPager.currentItem = 0
                R.id.compassNavMenuItem -> viewPager.currentItem = 1
                R.id.settingsNavMenuItem -> viewPager.currentItem = 2
            }
            return true
        }
    }
}
