package io.trewartha.positional.ui

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// I'm pretty sure the Compose Material 3 implementation of IconButton uses incorrect colors, so
// this brings things into compliance with the spec:
// https://m3.material.io/components/icon-buttons/specs
@Composable
fun IconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        content = content,
        colors = IconButtonDefaults.iconToggleButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = if (checked) MaterialTheme.colorScheme.inverseSurface
            else Color.Transparent
        )
    )
}
