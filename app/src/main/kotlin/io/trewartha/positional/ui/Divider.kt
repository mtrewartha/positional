package io.trewartha.positional.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview(showBackground = true)
fun Divider(modifier: Modifier = Modifier) {
    androidx.compose.material3.Divider(modifier = modifier.alpha(0.3f))
}