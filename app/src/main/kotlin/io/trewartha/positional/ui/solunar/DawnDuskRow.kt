package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun DawnDuskRow(
    label: String,
    dawn: LocalTime?,
    dusk: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        TwilightTime(
            time = dawn,
            showPlaceholder = showPlaceholders,
            modifier = Modifier.weight(1f, fill = true)
        )
        Text(
            text = label,
            modifier = Modifier.weight(1f, fill = true),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TwilightTime(
            time = dusk,
            showPlaceholder = showPlaceholders,
            modifier = Modifier.weight(1f, fill = true)
        )
    }
}

@Composable
private fun TwilightTime(
    time: LocalTime?,
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = time?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.solunar_text_time_none),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp)
            .placeholder(showPlaceholder)
    )
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            DawnDuskRow(
                label = "Label",
                dawn = null,
                dusk = null,
                showPlaceholders = true,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            DawnDuskRow(
                label = "Label",
                dawn = LocalTime(12, 50, 0),
                dusk = null,
                showPlaceholders = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
