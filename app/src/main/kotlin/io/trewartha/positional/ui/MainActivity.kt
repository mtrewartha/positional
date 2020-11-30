package io.trewartha.positional.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.trewartha.positional.R
import io.trewartha.positional.databinding.MainActivityBinding
import timber.log.Timber

class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var viewBinding: MainActivityBinding

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.setFormat(PixelFormat.RGBA_8888)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MainActivityBinding.inflate(layoutInflater)
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
