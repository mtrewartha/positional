package io.trewartha.positional.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showBackground = true)
fun Divider() {
    Row(
        modifier = Modifier
            .height(1.dp)
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                .fillMaxWidth()
        ) {}
    }
}