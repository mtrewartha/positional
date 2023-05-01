package io.trewartha.positional.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light Theme",
    group = "Light Theme",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Theme",
    group = "Dark Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews
