package io.trewartha.positional.sun.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.core.ui.components.HelpView

@Composable
fun SunHelpView(
    contentPadding: PaddingValues,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HelpView(
        title = { Text(text = stringResource(id = R.string.feature_sun_ui_help_title)) },
        markdownRes = R.raw.feature_sun_ui_help,
        contentPadding = contentPadding,
        onUpClick = onUpClick,
        modifier = modifier
    )
}
