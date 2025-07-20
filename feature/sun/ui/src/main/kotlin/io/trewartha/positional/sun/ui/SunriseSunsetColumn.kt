package io.trewartha.positional.sun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.core.ui.components.AutoShrinkingText
import io.trewartha.positional.core.ui.components.HorizontalDivider
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.core.ui.modifier.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun SunriseSunsetColumn(
    sunrise: State<LocalTime?, *>,
    sunset: State<LocalTime?, *>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderRow(modifier = Modifier.fillMaxWidth())
        SunriseSunsetRow(
            sunrise = sunrise,
            sunset = sunset,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HeaderRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AutoShrinkingText(
                text = stringResource(R.string.ui_sun_title_sunrise),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            HorizontalDivider()
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AutoShrinkingText(
                text = stringResource(R.string.ui_sun_title_sunset),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun SunriseSunsetRow(
    sunrise: State<LocalTime?, *>,
    sunset: State<LocalTime?, *>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        SunriseSunsetTime(
            localTime = sunrise,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
        SunriseSunsetTime(
            localTime = sunset,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SunriseSunsetTime(
    localTime: State<LocalTime?, *>,
    modifier: Modifier = Modifier
) {
    AutoShrinkingText(
        text = localTime.dataOrNull?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.ui_sun_text_time_none),
        modifier = modifier.placeholder(visible = localTime is State.Loading),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            SunriseSunsetColumn(
                sunrise = State.Loading,
                sunset = State.Loading
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            SunriseSunsetColumn(
                sunrise = State.Loaded<LocalTime?>(LocalTime(12, 0, 0)),
                sunset = State.Loaded<LocalTime?>(null)
            )
        }
    }
}

