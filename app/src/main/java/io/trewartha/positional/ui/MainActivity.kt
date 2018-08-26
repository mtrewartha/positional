package io.trewartha.positional.ui

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.MenuItem
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == UI_MODE_NIGHT_YES) window.decorView.systemUiVisibility =
                SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_FULLSCREEN or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

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
