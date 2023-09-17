package io.trewartha.positional.ui.compass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.material.color.MaterialColors
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Compass(
    azimuthDegrees: Float,
    modifier: Modifier = Modifier,
    northTickColor: Color = Color(
        MaterialColors.harmonize(
            Color.Red.toArgb(),
            MaterialTheme.colorScheme.primary.toArgb()
        )
    ),
    cardinalTickColor: Color = MaterialTheme.colorScheme.onSurface,
    majorTickColor: Color = cardinalTickColor,
    minorTickColor: Color = majorTickColor.copy(alpha = 0.3f),
    northTickLength: Dp = 32.dp,
    cardinalTickLength: Dp = 24.dp,
    majorTickLength: Dp = 16.dp,
    minorTickLength: Dp = 8.dp,
    northTickWidth: Dp = 24.dp,
    cardinalTickWidth: Dp = 12.dp,
    majorTickWidth: Dp = 12.dp,
    minorTickWidth: Dp = 8.dp,
    majorTickPeriodDegrees: Int = 45,
    minorTickPeriodDegrees: Int = 15
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.North,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = stringResource(
                when {
                    AZIMUTH_NW_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_NW_MAX ->
                        R.string.compass_direction_northwest
                    AZIMUTH_NE_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_NE_MAX ->
                        R.string.compass_direction_northeast
                    AZIMUTH_SW_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_SW_MAX ->
                        R.string.compass_direction_southwest
                    AZIMUTH_SE_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_SE_MAX ->
                        R.string.compass_direction_southeast
                    AZIMUTH_E_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_E_MAX ->
                        R.string.compass_direction_east
                    AZIMUTH_S_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_S_MAX ->
                        R.string.compass_direction_south
                    AZIMUTH_W_MIN <= azimuthDegrees && azimuthDegrees < AZIMUTH_W_MAX ->
                        R.string.compass_direction_west
                    else ->
                        R.string.compass_direction_north
                }
            ),
            style = MaterialTheme.typography.displayLarge
        )
        Box(
            modifier = Modifier.aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            ConstraintLayout(modifier = Modifier.widthIn(min = 64.dp)) {
                val (degreesText, symbolText) = createRefs()
                Text(
                    text = "${azimuthDegrees.roundToInt() % 360}",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.constrainAs(degreesText) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                )
                Text(
                    text = "°",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.constrainAs(symbolText) {
                        top.linkTo(degreesText.top)
                        start.linkTo(degreesText.end)
                    }
                )
            }
            CompassRose(
                azimuthDegrees = azimuthDegrees,
                northTickColor = northTickColor,
                cardinalTickColor = cardinalTickColor,
                majorTickColor = majorTickColor,
                minorTickColor = minorTickColor,
                northTickLength = northTickLength,
                cardinalTickLength = cardinalTickLength,
                majorTickLength = majorTickLength,
                minorTickLength = minorTickLength,
                northTickWidth = northTickWidth,
                cardinalTickWidth = cardinalTickWidth,
                majorTickWidth = majorTickWidth,
                minorTickWidth = minorTickWidth,
                majorTickPeriodDegrees = majorTickPeriodDegrees,
                minorTickPeriodDegrees = minorTickPeriodDegrees,
            )
        }
    }
}

@Composable
private fun CompassRose(
    azimuthDegrees: Float,
    modifier: Modifier = Modifier,
    northTickColor: Color,
    cardinalTickColor: Color,
    majorTickColor: Color,
    minorTickColor: Color,
    northTickLength: Dp,
    cardinalTickLength: Dp,
    majorTickLength: Dp,
    minorTickLength: Dp,
    northTickWidth: Dp,
    cardinalTickWidth: Dp,
    majorTickWidth: Dp,
    minorTickWidth: Dp,
    majorTickPeriodDegrees: Int,
    minorTickPeriodDegrees: Int
) {
    val displayDensity = LocalDensity.current.density
    val northTickLengthPx = remember { northTickLength.value * displayDensity }
    val cardinalTickLengthPx = remember { cardinalTickLength.value * displayDensity }
    val majorTickLengthPx = remember { majorTickLength.value * displayDensity }
    val minorTickLengthPx = remember { minorTickLength.value * displayDensity }
    val northTickWidthPx = remember { northTickWidth.value * displayDensity }
    val cardinalTickWidthPx = remember { cardinalTickWidth.value * displayDensity }
    val majorTickWidthPx = remember { majorTickWidth.value * displayDensity }
    val minorTickWidthPx = remember { minorTickWidth.value * displayDensity }

    val northTickStyle = remember {
        TickStyle(
            color = northTickColor,
            lengthPx = northTickLengthPx,
            widthPx = northTickWidthPx
        )
    }
    val cardinalTickStyle = remember {
        TickStyle(
            color = cardinalTickColor,
            lengthPx = cardinalTickLengthPx,
            widthPx = cardinalTickWidthPx
        )
    }
    val majorTickStyle = remember {
        TickStyle(
            color = majorTickColor,
            lengthPx = majorTickLengthPx,
            widthPx = majorTickWidthPx
        )
    }
    val minorTickStyle = remember {
        TickStyle(
            color = minorTickColor,
            lengthPx = minorTickLengthPx,
            widthPx = minorTickWidthPx
        )
    }

    val boxPadding = maxOf(northTickLength, cardinalTickLength, majorTickLength, minorTickLength)
        .let { maxTickLength ->
            val maxTickCapLength = when (maxTickLength) {
                northTickLength -> northTickWidthPx
                cardinalTickLength -> cardinalTickWidthPx
                majorTickLength -> majorTickWidthPx
                else -> minorTickWidthPx
            }.let { it / 2 }.dp
            maxTickLength + maxTickCapLength
        }
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(boxPadding)
    ) {
        val canvasSizePx = remember { minOf(constraints.maxWidth, constraints.maxHeight).toFloat() }
        val canvasSizeDp = remember { (canvasSizePx * displayDensity).dp }
        val center = Offset(canvasSizePx / 2f, canvasSizePx / 2f)

        // We'll animate the compass rose to the negative of the azimuth so that the rose exactly
        // counteracts the rotation of the Android device, keeping the rose pointed north. We're
        // careful to transition naturally across the 0°/360° boundary because we don't want the
        // rose to completely spin around each time we cross the boundary. The approach has been
        // adapted from the following StackOverflow answer:
        // https://stackoverflow.com/a/68259116/1253644
        val (lastAnimatedAzimuth, setLastAnimatedAzimuth) = remember { mutableIntStateOf(0) }
        val animatedAzimuthInt = azimuthDegrees.roundToInt()
        var newAnimatedAzimuth = lastAnimatedAzimuth // We'll update this if necessary
        val modLastAnimatedAzimuth = if (lastAnimatedAzimuth > 0) {
            lastAnimatedAzimuth % DEGREES_360
        } else {
            DEGREES_360 - (-lastAnimatedAzimuth % DEGREES_360)
        }
        if (modLastAnimatedAzimuth != animatedAzimuthInt) {
            val clockwiseDiff = if (animatedAzimuthInt > modLastAnimatedAzimuth) {
                modLastAnimatedAzimuth + DEGREES_360 - animatedAzimuthInt
            } else {
                modLastAnimatedAzimuth - animatedAzimuthInt
            }
            val counterClockwiseDiff = if (animatedAzimuthInt > modLastAnimatedAzimuth) {
                animatedAzimuthInt - modLastAnimatedAzimuth
            } else {
                DEGREES_360 - modLastAnimatedAzimuth + animatedAzimuthInt
            }
            val rotateClockwise = clockwiseDiff < counterClockwiseDiff
            newAnimatedAzimuth = if (rotateClockwise) {
                lastAnimatedAzimuth - clockwiseDiff
            } else {
                lastAnimatedAzimuth + counterClockwiseDiff
            }

            setLastAnimatedAzimuth(newAnimatedAzimuth)
        }
        val animatedRotation: Float by animateFloatAsState(
            targetValue = -newAnimatedAzimuth.toFloat(),
            label = "Rotation"
        )

        Canvas(
            modifier = Modifier
                .size(canvasSizeDp)
                .rotate(animatedRotation)
        ) {
            for (degrees in 0..<DEGREES_360) {
                when {
                    degrees == CANVAS_DEGREES_NORTH -> northTickStyle
                    degrees % CARDINAL_DIRECTION_INTERVAL_DEGREES == 0 -> cardinalTickStyle
                    degrees % majorTickPeriodDegrees == 0 -> majorTickStyle
                    degrees % minorTickPeriodDegrees == 0 -> minorTickStyle
                    else -> null
                }?.let { tickStyle ->
                    drawTick(center, degrees, tickStyle)
                }
            }
        }
    }
}

/**
 * Draws a tick mark in a compass rose
 *
 * @param center the center of the compass rose
 * @param degrees the location of the tick mark in clockwise degrees from the X-axis
 * @param tickStyle the styling to apply to the tick mark
 */
private fun DrawScope.drawTick(center: Offset, degrees: Int, tickStyle: TickStyle) {
    val cos = cos(degrees.toFloat().toRadians())
    val sin = sin(degrees.toFloat().toRadians())
    val start = center + Offset(cos * center.x, sin * center.y)
    val end = when {
        cos >= 0 && sin >= 0 -> start + Offset(cos * tickStyle.lengthPx, sin * tickStyle.lengthPx)
        cos >= 0 && sin < 0 -> start + Offset(cos * tickStyle.lengthPx, sin * tickStyle.lengthPx)
        cos < 0 && sin >= 0 -> start + Offset(cos * tickStyle.lengthPx, sin * tickStyle.lengthPx)
        else -> start + Offset(cos * tickStyle.lengthPx, sin * tickStyle.lengthPx)
    }
    drawLine(
        color = tickStyle.color,
        start = start,
        end = end,
        strokeWidth = tickStyle.widthPx,
        cap = StrokeCap.Round
    )
}

private const val AZIMUTH_N_MIN = 337.5f
private const val AZIMUTH_N_MAX = 22.5f
private const val AZIMUTH_E_MIN = 67.5f
private const val AZIMUTH_E_MAX = 112.5f
private const val AZIMUTH_S_MIN = 157.5f
private const val AZIMUTH_S_MAX = 202.5f
private const val AZIMUTH_W_MIN = 247.5f
private const val AZIMUTH_W_MAX = 292.5f
private const val AZIMUTH_NE_MIN = AZIMUTH_N_MAX
private const val AZIMUTH_NE_MAX = AZIMUTH_E_MIN
private const val AZIMUTH_SE_MIN = AZIMUTH_E_MAX
private const val AZIMUTH_SE_MAX = AZIMUTH_S_MIN
private const val AZIMUTH_SW_MIN = AZIMUTH_S_MAX
private const val AZIMUTH_SW_MAX = AZIMUTH_W_MIN
private const val AZIMUTH_NW_MIN = AZIMUTH_W_MAX
private const val AZIMUTH_NW_MAX = AZIMUTH_N_MIN
private const val CANVAS_DEGREES_NORTH =
    270 // since canvas angles are measured clockwise off X-axis
private const val CARDINAL_DIRECTION_INTERVAL_DEGREES = 90
private const val DEGREES_180 = 180f
private const val DEGREES_360 = 360

private data class TickStyle(
    val color: Color,
    val lengthPx: Float,
    val widthPx: Float
)

private fun Float.toRadians(): Float = (this / DEGREES_180 * Math.PI).toFloat()

@PreviewLightDark
@Composable
private fun CompassPreview() {
    PositionalTheme {
        Surface {
            Compass(azimuthDegrees = 0f)
        }
    }
}
