package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Adjust
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews


@Composable
fun LocationStatRow(
    icon: ImageVector,
    name: String,
    value: String?,
    accuracy: String?,
    accuracyVisible: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE)
        )
        Text(
            text = name,
            fontSize = 18.sp,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(start = HORIZONTAL_PADDING)
                .weight(1f, true),
        )
        Text(
            text = value ?: "",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = HORIZONTAL_PADDING)
        )
        if (accuracyVisible) {
            Text(
                text = accuracy ?: "",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = HORIZONTAL_PADDING)
            )
        }
    }
}

@ThemePreviews
@Composable
fun LocationStatRowPreview() {
    PositionalTheme {
        Surface {
            LocationStatRow(
                icon = Icons.TwoTone.Adjust,
                name = "Accuracy",
                value = "100.0",
                accuracy = "± 10.0",
                accuracyVisible = true
            )
        }
    }
}

private val HORIZONTAL_PADDING = 16.dp
private val ICON_SIZE = 24.dp