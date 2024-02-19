package io.trewartha.positional.ui.design.modifier

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.placeholder
import com.eygraber.compose.placeholder.shimmer

@Composable
fun Modifier.placeholder(visible: Boolean): Modifier = this.placeholder(
    visible = visible,
    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
    shape = RoundedCornerShape(16.dp),
    highlight = PlaceholderHighlight.shimmer(
        highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    )
)
