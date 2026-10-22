package io.trewartha.positional.core.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
public fun PositionalTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialExpressiveTheme(
        colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (useDarkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        } else {
            if (useDarkTheme) darkColorScheme
            else lightColorScheme
        },
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
