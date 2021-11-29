package io.trewartha.positional.ui.location

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.trewartha.positional.R

@Composable
fun ShareButton(onShareClick: () -> Unit) {
    IconButton(onClick = onShareClick) {
        Icon(
            imageVector = Icons.TwoTone.Share,
            contentDescription = stringResource(R.string.location_share_button_content_description),
        )
    }
}