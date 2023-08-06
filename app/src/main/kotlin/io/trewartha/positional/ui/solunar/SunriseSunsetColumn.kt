package io.trewartha.positional.ui.solunar

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
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.HorizontalDivider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun SunriseSunsetColumn(
    sunrise: LocalTime?,
    sunset: LocalTime?,
    showPlaceholders: Boolean,
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
            showPlaceholders = showPlaceholders,
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
                text = stringResource(R.string.solunar_title_sunrise),
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
                text = stringResource(R.string.solunar_title_sunset),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun SunriseSunsetRow(
    sunrise: LocalTime?,
    sunset: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        SunriseSunsetTime(
            localTime = sunrise,
            showPlaceholder = showPlaceholders,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
        SunriseSunsetTime(
            localTime = sunset,
            showPlaceholder = showPlaceholders,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SunriseSunsetTime(
    localTime: LocalTime?,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    AutoShrinkingText(
        text = localTime?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.solunar_text_time_none),
        modifier = modifier.placeholder(visible = showPlaceholder),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            SunriseSunsetColumn(
                sunrise = null,
                sunset = null,
                showPlaceholders = true
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            SunriseSunsetColumn(
                sunrise = LocalTime(12, 0, 0),
                sunset = null,
                showPlaceholders = false
            )
        }
    }
}

