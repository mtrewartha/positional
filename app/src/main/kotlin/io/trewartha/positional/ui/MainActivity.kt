package io.trewartha.positional.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.elevation.ElevationOverlayProvider
import io.trewartha.positional.R
import io.trewartha.positional.databinding.MainActivityBinding


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
}
