package io.trewartha.positional.ui

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// I'm pretty sure the Compose Material 3 implementation of IconButton uses incorrect colors, so
// this brings things into compliance with the spec:
// https://m3.material.io/components/icon-buttons/specs
@Composable
fun IconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        enabled = enabled,
        content = content,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}
