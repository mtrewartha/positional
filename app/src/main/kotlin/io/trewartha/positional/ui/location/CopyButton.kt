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
fun CopyButton(onCopyClick: () -> Unit) {
    IconButton(onClick = onCopyClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_twotone_file_copy_24px),
            contentDescription = stringResource(R.string.location_copy_coordinates_content_description),
            tint = MaterialTheme.colors.primary
        )
    }
}