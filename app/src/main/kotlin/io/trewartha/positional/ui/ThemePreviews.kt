package io.trewartha.positional.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Theme",
    group = "Theme Previews",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Theme",
    group = "Theme Previews",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews
