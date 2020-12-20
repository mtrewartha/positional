package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.ElevationOverlayProvider
import io.trewartha.positional.R
import io.trewartha.positional.databinding.MainActivityBinding
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var viewBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.theme.observe(this) {
            val mode = when (it) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        super.onCreate(savedInstanceState)

        viewBinding = MainActivityBinding.inflate(layoutInflater)

        // While the navigation bar color can technically be changed started in API 21, we can't
        // specify that the navigation bar is light (and the icons should be dark) until API 27.
        // Therefore, we don't mess with navigation bar color until API 27.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.navigationBarColor = ElevationOverlayProvider(this@MainActivity)
                    .compositeOverlayWithThemeSurfaceColorIfNeeded(
                            viewBinding.bottomNavigationView.elevation,
                            viewBinding.bottomNavigationView
                    )
        }

        setContentView(viewBinding.root)

        viewBinding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHost))
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
            MaterialAlertDialogBuilder(this)
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
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

    private fun goToSettings() {
        startActivity(Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
        ))
    }

    private fun requestPermissions(permissions: List<String>) {
        Timber.i("Requesting permissions")
        ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
        )
    }

    companion object {
        private val PERMISSIONS = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 1
    }
}
