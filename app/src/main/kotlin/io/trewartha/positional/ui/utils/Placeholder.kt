package io.trewartha.positional.ui.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun Modifier.placeholder(visible: Boolean): Modifier = placeholder(
    visible = visible,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
    shape = RoundedCornerShape(16.dp),
    highlight = PlaceholderHighlight.shimmer(
        highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    )
)
