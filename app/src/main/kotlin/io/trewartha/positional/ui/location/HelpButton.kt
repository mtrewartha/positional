package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme

@Composable
fun HelpButton(onHelpClick: () -> Unit) {
    IconButton(onClick = onHelpClick) {
        Icon(
            imageVector = Icons.TwoTone.Help,
            contentDescription = stringResource(R.string.location_help_button_content_description),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpButtonPreview() {
    PositionalTheme {
        HelpButton {}
    }
}