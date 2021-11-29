package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FileCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme

@Composable
fun CopyButton(onCopyClick: () -> Unit) {
    IconButton(onClick = onCopyClick) {
        Icon(
            imageVector = Icons.TwoTone.FileCopy,
            contentDescription = stringResource(R.string.location_coordinates_copy_content_description)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CopyButtonPreview() {
    PositionalTheme {
        CopyButton {}
    }
}