package io.trewartha.positional.ui.sun

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import kotlinx.datetime.LocalTime

@Composable
fun SunriseSunsetTimesRow(
    sunrise: LocalTime?,
    sunset: LocalTime?,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        SunriseSunsetTime(sunrise, modifier = Modifier.weight(1f, fill = true))
        Spacer(modifier = Modifier.weight(1f, fill = true))
        SunriseSunsetTime(sunset, modifier = Modifier.weight(1f, fill = true))
    }
}

@Composable
private fun SunriseSunsetTime(value: LocalTime?, modifier: Modifier = Modifier) {
    Text(
        text = value
            ?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.sun_text_time_none),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier.defaultMinSize(minWidth = 64.dp)
    )
}
