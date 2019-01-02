package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import io.trewartha.positional.R
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber


class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel()
        viewModel.themeMode.observe(this, ThemeModeObserver())

        setContentView(R.layout.main_activity)

        bottomNavigationView.setupWithNavController(findNavController(R.id.navHost))
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

        if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
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
            val permissionResult = ActivityCompat.checkSelfPermission(this, it)
            permissionResult != PackageManager.PERMISSION_GRANTED
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

    private inner class ThemeModeObserver : Observer<ThemeMode> {

        override fun onChanged(themeMode: ThemeMode?) {
            delegate.setLocalNightMode(
                when (themeMode) {
                    ThemeMode.DAY -> MODE_NIGHT_NO
                    ThemeMode.NIGHT -> MODE_NIGHT_YES
                    else -> MODE_NIGHT_AUTO
                }
            )
        }
    }

    companion object {
        private val PERMISSIONS = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 1
    }
}
