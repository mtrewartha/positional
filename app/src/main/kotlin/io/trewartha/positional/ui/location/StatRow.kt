package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.utils.placeholder

@Composable
fun StatRow(
    icon: ImageVector,
    name: String,
    value: String?,
    accuracy: String?,
    showAccuracy: Boolean,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.placeholder(visible = showPlaceholder),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE)
        )
        Text(
            text = name,
            modifier = Modifier.weight(1f, true),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value ?: "",
            modifier = Modifier
                .placeholder(showPlaceholder)
                .sizeIn(minWidth = 72.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End
        )
        if (showAccuracy) {
            Text(
                text = accuracy ?: "",
                fontSize = 14.sp,
                modifier = Modifier
                    .placeholder(showPlaceholder)
                    .sizeIn(minWidth = 36.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )
        }
    }
}

@PreviewFontScale
@PreviewLightDark
@Composable
fun LocationStatRowPreview() {
    PositionalTheme {
        Surface {
            StatRow(
                icon = Icons.Rounded.Adjust,
                name = "Accuracy",
                value = "100.0",
                accuracy = "± 10.0",
                showAccuracy = true,
                showPlaceholder = false
            )
        }
    }
}

@PreviewLightDark
@Composable
fun LocationStatRowPlaceholderPreview() {
    PositionalTheme {
        Surface {
            StatRow(
                icon = Icons.Rounded.Adjust,
                name = "Accuracy",
                value = "100.0",
                accuracy = "± 10.0",
                showAccuracy = true,
                showPlaceholder = true
            )
        }
    }
}

private val ICON_SIZE = 20.dp
