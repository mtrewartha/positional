package io.trewartha.positional.ui.location

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.tooling.preview.Preview

data class CoordinatesState(val coordinates: String, val maxLines: Int)

@Composable
fun CoordinatesText(
    coordinatesState: CoordinatesState,
    modifier: Modifier = Modifier
) {
    val textStyleH1 = MaterialTheme.typography.h1
    var textStyle by remember { mutableStateOf(textStyleH1) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = coordinatesState.coordinates,
        style = textStyle,
        maxLines = coordinatesState.maxLines,
        softWrap = false,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Preview("Decimal Degrees", showBackground = true)
@Composable
fun DecimalDegreesPreview() {
    CoordinatesText(coordinatesState = CoordinatesState("123.456789\n123.456789", 2))
}

@Preview("Degrees Decimal Minutes", showBackground = true)
@Composable
fun DegreesDecimalMinutesPreview() {
    CoordinatesText(coordinatesState = CoordinatesState("123.456789\n123.456789", 2))
}

@Preview("MGRS", showBackground = true)
@Composable
fun MgrsPreview() {
    CoordinatesText(coordinatesState = CoordinatesState("123.456789\n123.456789", 2))
}

@Preview("UTM", showBackground = true)
@Composable
fun UtmPreview() {
    CoordinatesText(coordinatesState = CoordinatesState("123.456789\n123.456789", 2))
}