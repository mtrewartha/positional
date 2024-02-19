package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.trewartha.positional.ui.design.PositionalTheme
import io.trewartha.positional.ui.design.components.HelpView

@Composable
fun LocationHelpView(
    contentPadding: PaddingValues,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HelpView(
        title = { Text(text = stringResource(id = R.string.ui_location_help_title)) },
        markdownRes = R.raw.ui_location_help,
        contentPadding = contentPadding,
        onUpClick = onUpClick,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        LocationHelpView(PaddingValues(), onUpClick = {})
    }
}
