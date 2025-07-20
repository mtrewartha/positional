package io.trewartha.positional.compass.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.components.HelpView

@Composable
fun CompassHelpView(
    contentPadding: PaddingValues,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HelpView(
        title = { Text(text = stringResource(id = R.string.ui_compass_help_title)) },
        markdownRes = R.raw.ui_compass_help,
        contentPadding = contentPadding,
        onUpClick = onUpClick,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        CompassHelpView(PaddingValues(), onUpClick = {})
    }
}
