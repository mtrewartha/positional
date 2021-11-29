package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.tooling.preview.Preview
import io.trewartha.positional.ui.PositionalTheme

data class CoordinatesState(val coordinates: String, val maxLines: Int)

@Composable
fun CoordinatesText(
    coordinatesState: CoordinatesState,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val textStyleTitleLarge = MaterialTheme.typography.titleLarge
    var textStyle by remember { mutableStateOf(textStyleTitleLarge) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = coordinatesState.coordinates,
        style = textStyle,
        maxLines = coordinatesState.maxLines,
        softWrap = false,
        modifier = modifier
            .drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CoordinatesTextPreview() {
    PositionalTheme {
        CoordinatesText(coordinatesState = CoordinatesState("123.456789\n123.456789", 2))
    }
}
