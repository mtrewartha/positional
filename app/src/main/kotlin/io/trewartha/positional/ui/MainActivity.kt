package io.trewartha.positional.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PositionalTheme {
                // TODO: Handle window insets in the new way (see Accompanist page for why this isn't the correct way anymore)
                ProvideWindowInsets {
                    val navController = rememberNavController()
                    MainScreen(navController)
                }
            }
        }
    }
}
