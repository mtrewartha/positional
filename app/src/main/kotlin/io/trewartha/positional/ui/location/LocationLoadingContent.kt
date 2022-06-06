package io.trewartha.positional.ui.location

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.MyLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.WindowSizePreviews

@Composable
fun LocationLoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .requiredWidthIn(max = 384.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val animationEasing = CubicBezierEasing(0.75f, 0.25f, 0.25f, 0.75f)
        val animatedIconAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = animationEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val animatedIconRotationDegrees by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = animationEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val animatedIconBlur by infiniteTransition.animateValue(
            initialValue = 2.dp,
            targetValue = 0.dp,
            typeConverter = Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = animationEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Icon(
            imageVector = Icons.TwoTone.MyLocation,
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .alpha(animatedIconAlpha)
                .rotate(animatedIconRotationDegrees)
                .blur(animatedIconBlur, BlurredEdgeTreatment.Unbounded),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = stringResource(id = R.string.location_loading_headline),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.location_loading_body),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@ThemePreviews
@WindowSizePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            LocationLoadingContent()
        }
    }
}