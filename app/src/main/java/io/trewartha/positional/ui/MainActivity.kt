package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber


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

    override fun onStart() {
        super.onStart()
        val permissionsToRequest = getPermissionsToRequest()
        if (permissionsToRequest.isNotEmpty()) requestPermissions(permissionsToRequest)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSIONS) return

        val allPermissionsPresent = permissions.contentEquals(PERMISSIONS)
        val allPermissionsGranted = grantResults.all { it == PermissionChecker.PERMISSION_GRANTED }
                && grantResults.size == permissions.size

        if (!allPermissionsPresent || !allPermissionsGranted) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.location_permission_explanation_title)
                    .setMessage(R.string.location_permission_explanation_message)
                    .setPositiveButton(R.string.location_permission_explanation_positive) { _, _ ->
                        goToSettings()
                    }
                    .setNegativeButton(R.string.location_permission_explanation_negative) { _, _ ->
                        exit()
                    }
                    .setCancelable(false)
                    .show()
        }
    }

    private fun exit() {
        finishAndRemoveTask()
    }

    private fun getPermissionsToRequest(): List<String> = PERMISSIONS
            .filter {
                val permissionResult = PermissionChecker.checkSelfPermission(this, it)
                permissionResult != PermissionChecker.PERMISSION_GRANTED
            }

    private fun goToSettings() {
        val intent = Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }

    private fun requestPermissions(permissions: List<String>) {
        Timber.i("Requesting permissions")
        ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
        )
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

    companion object {
        private val PERMISSIONS = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 1
    }
}
