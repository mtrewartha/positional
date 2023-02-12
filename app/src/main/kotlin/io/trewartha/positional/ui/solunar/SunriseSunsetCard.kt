package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Divider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun SunriseSunsetCard(
    sunrise: LocalTime?,
    sunset: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.solunar_title_sunrise),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.solunar_title_sunset),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            Divider(modifier = modifier.fillMaxWidth())
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                SunriseSunsetTime(
                    localTime = sunrise,
                    showPlaceholder = showPlaceholders,
                    modifier = Modifier.weight(1f, fill = true)
                )
                Spacer(modifier = Modifier.weight(1f, fill = true))
                SunriseSunsetTime(
                    localTime = sunset,
                    showPlaceholder = showPlaceholders,
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
        }
    }
}

@Composable
private fun SunriseSunsetTime(
    localTime: LocalTime?,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = localTime?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.solunar_text_time_none),
        modifier = modifier.placeholder(visible = showPlaceholder),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        SunriseSunsetCard(
            sunrise = null,
            sunset = null,
            showPlaceholders = true
        )
    }
}

@ThemePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        SunriseSunsetCard(
            sunrise = LocalTime(12, 0, 0),
            sunset = null,
            showPlaceholders = false
        )
    }
}

