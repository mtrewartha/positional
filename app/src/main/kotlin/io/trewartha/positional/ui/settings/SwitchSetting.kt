package io.trewartha.positional.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.utils.placeholder

@Composable
fun SwitchSetting(
    icon: ImageVector,
    title: String,
    description: @Composable (Boolean) -> String,
    value: Boolean?,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholdersVisible = value == null
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.settings_row_arrangement_spacing)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.placeholder(placeholdersVisible),
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(placeholdersVisible),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = value?.let { description(it) }.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(placeholdersVisible),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Switch(
            checked = value ?: false,
            onCheckedChange = onValueChange,
            modifier = Modifier.placeholder(placeholdersVisible),
        )
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface(modifier = Modifier.width(320.dp)) {
            SwitchSetting(
                icon = Icons.Rounded.Settings,
                title = "Switch Setting Name",
                description = { "This is the description. It may reflect the current value." },
                value = null,
                onValueChange = {}
            )
        }
    }
}

@Preview
@Composable
private fun UncheckedPreview() {
    PositionalTheme {
        Surface(modifier = Modifier.width(320.dp)) {
            SwitchSetting(
                icon = Icons.Rounded.Settings,
                title = "Switch Setting Name",
                description = { "This is the description. It may reflect the current value." },
                value = false,
                onValueChange = {}
            )
        }
    }
}

@Preview
@Composable
private fun CheckedPreview() {
    PositionalTheme {
        Surface(modifier = Modifier.width(320.dp)) {
            SwitchSetting(
                icon = Icons.Rounded.Settings,
                title = "Switch Setting Name",
                description = { "This is the description. It may reflect the current value." },
                value = true,
                onValueChange = {}
            )
        }
    }
}
