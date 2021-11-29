package io.trewartha.positional.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                .fillMaxWidth()
        ) {}
    }
}