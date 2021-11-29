package io.trewartha.positional.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

enum class Theme { SYSTEM, LIGHT, DARK }

@Composable
fun PositionalTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !useDarkTheme,
        )
    }
    MaterialTheme(
        colorScheme = if (useDarkTheme)
            dynamicDarkColorScheme(LocalContext.current)
        else
            dynamicLightColorScheme(LocalContext.current),
        content = content
    )
}