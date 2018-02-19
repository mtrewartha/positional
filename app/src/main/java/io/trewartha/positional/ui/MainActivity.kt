package io.trewartha.positional.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import io.trewartha.positional.R
import io.trewartha.positional.ui.auth.SignInActivity
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var fragmentPagerAdapter: MainFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        fragmentPagerAdapter = MainFragmentPagerAdapter(supportFragmentManager)
        viewPager.adapter = fragmentPagerAdapter
        viewPager.addOnPageChangeListener(PageChangeListener())

        bottomNavigationView.setOnNavigationItemSelectedListener(NavigationItemSelectedListener())

        if (!signedIn()) {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode != Activity.RESULT_OK) finish()
        }
    }

    private fun signedIn() = firebaseAuth.currentUser != null

    private inner class NavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.mapNavMenuItem -> viewPager.currentItem = 0
                R.id.tracksNavMenuItem -> viewPager.currentItem = 1
                R.id.positionNavMenuItem -> viewPager.currentItem = 2
                R.id.compassNavMenuItem -> viewPager.currentItem = 3
                R.id.profileNavMenuItem -> viewPager.currentItem = 4
            }
            return true
        }
    }

    private inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            for (i in 0 until bottomNavigationView.menu.size()) {
                val checked = i == position
                bottomNavigationView.menu.getItem(i).isChecked = checked
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1

        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        }
    }
}
