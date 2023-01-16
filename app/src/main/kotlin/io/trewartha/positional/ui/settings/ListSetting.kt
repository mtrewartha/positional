package io.trewartha.positional.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.utils.placeholder

@Composable
fun <T> ListSetting(
    icon: ImageVector,
    title: String,
    values: Set<T>?,
    value: T?,
    valueName: @Composable (T) -> String,
    valuesDialogTitle: String,
    valuesDialogText: String?,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholdersVisible = values == null || value == null
    var showValuesDialog by remember { mutableStateOf(false) }
    if (showValuesDialog && values != null && value != null) {
        ValuesDialog(
            icon = icon,
            title = valuesDialogTitle,
            text = valuesDialogText,
            values = values,
            value = value,
            valueName = valueName,
            onDismissRequest = { showValuesDialog = false },
            onValueChange = {
                showValuesDialog = false
                onValueChange(it)
            }
        )
    }
    Surface(
        onClick = { if (values != null && value != null) showValuesDialog = true }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.padding(16.dp)
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
                    text = value?.let { valueName(it) }.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(placeholdersVisible)
                )
            }
        }
    }
}

@Composable
private fun <T> ValuesDialog(
    icon: ImageVector,
    title: String,
    text: String?,
    values: Set<T>,
    value: T,
    valueName: @Composable (T) -> String,
    onDismissRequest: () -> Unit,
    onValueChange: (T) -> Unit
) {
    var selectedValue by remember { mutableStateOf(value) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        },
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                if (text != null) {
                    item {
                        Text(
                            text = text,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(values.toList()) { value ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { role = Role.RadioButton }
                            .selectable(
                                selected = value == selectedValue,
                                role = Role.RadioButton,
                                onClick = { selectedValue = value }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = value == selectedValue,
                            onClick = { selectedValue = value }
                        )
                        Text(valueName(value), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onValueChange(selectedValue) }) {
                Text(stringResource(R.string.settings_theme_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.settings_theme_dialog_dismiss))
            }
        }
    )
}

@Preview
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface(modifier = Modifier.width(320.dp)) {
            ListSetting(
                icon = Icons.Rounded.Settings,
                title = "List Setting Name",
                values = null,
                value = null,
                valueName = { "" },
                valuesDialogTitle = "Dialog title",
                valuesDialogText = "Dialog text",
                onValueChange = {}
            )
        }
    }
}

@Preview
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface(modifier = Modifier.width(320.dp)) {
            ListSetting(
                icon = Icons.Rounded.Settings,
                title = "List Setting Name",
                values = emptySet(),
                value = Unit,
                valueName = { it.toString() },
                valuesDialogTitle = "Dialog title",
                valuesDialogText = "Dialog text",
                onValueChange = {}
            )
        }
    }
}

@ThemePreviews
@Composable
private fun DialogPreviews() {
    PositionalTheme {
        ValuesDialog(
            icon = Icons.Rounded.Settings,
            title = "Some title",
            text = "Some text",
            values = setOf("Value 1", "Value 2", "Value 3"),
            value = "Value 1",
            valueName = { it },
            onDismissRequest = {}
        ) {}
    }
}
