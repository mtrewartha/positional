package io.trewartha.positional.ui.location

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import io.trewartha.positional.R

@Composable
fun ShareButton(onShareClick: () -> Unit) {
    IconButton(onClick = onShareClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_twotone_share_24px),
            contentDescription = stringResource(R.string.location_share_button_content_description),
            tint = MaterialTheme.colors.primary
        )
    }
}