package io.trewartha.positional.ui.twilight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
fun TwilightsRow(
    label: String,
    morningValue: LocalTime?,
    eveningValue: LocalTime?,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        TwilightTime(morningValue, modifier = Modifier.weight(1f, fill = true))
        Text(
            text = label,
            modifier = Modifier.weight(1f, fill = true),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TwilightTime(eveningValue, modifier = Modifier.weight(1f, fill = true))
    }
}

@Composable
private fun TwilightTime(value: LocalTime?, modifier: Modifier = Modifier) {
    Text(
        text = value
            ?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.twilight_time_none),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.defaultMinSize(minWidth = 64.dp)
    )
}
