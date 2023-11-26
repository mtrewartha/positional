package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.utils.placeholder

@Composable
fun StatBlock(
    icon: ImageVector,
    name: String,
    value: String?,
    accuracy: String?,
    showAccuracy: Boolean,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(12.dp)
            .widthIn(min = 160.dp)
            .placeholder(visible = showPlaceholder),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = value ?: stringResource(R.string.common_dash),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            if (showAccuracy) {
                Text(
                    text = accuracy ?: stringResource(R.string.common_dash),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Text(name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    }
}

@PreviewFontScale
@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            StatBlock(
                icon = Icons.Rounded.Terrain,
                name = "Accelerometer\n" +
                        "accuracy",
                value = null,
                accuracy = null,
                showAccuracy = true,
                showPlaceholder = true
            )
        }
    }
}

@PreviewFontScale
@PreviewLightDark
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            StatBlock(
                icon = Icons.Rounded.Terrain,
                name = "Accelerometer\naccuracy",
                value = "10,000 ft",
                accuracy = "±5",
                showAccuracy = true,
                showPlaceholder = false
            )
        }
    }
}
