package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import io.trewartha.positional.R
import io.trewartha.positional.ui.HorizontalDivider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun DawnDuskColumn(
    astronomicalDawn: LocalTime?,
    nauticalDawn: LocalTime?,
    civilDawn: LocalTime?,
    civilDusk: LocalTime?,
    nauticalDusk: LocalTime?,
    astronomicalDusk: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderRow(modifier = Modifier.fillMaxWidth())
        DawnDuskRow(
            label = stringResource(R.string.solunar_label_dawn_dusk_civil),
            dawn = civilDawn,
            dusk = civilDusk,
            showPlaceholders = showPlaceholders,
            modifier = Modifier.fillMaxWidth()
        )
        DawnDuskRow(
            label = stringResource(R.string.solunar_label_dawn_dusk_nautical),
            dawn = nauticalDawn,
            dusk = nauticalDusk,
            showPlaceholders = showPlaceholders,
            modifier = Modifier.fillMaxWidth()
        )
        DawnDuskRow(
            label = stringResource(R.string.solunar_label_dawn_dusk_astronomical),
            dawn = astronomicalDawn,
            dusk = astronomicalDusk,
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
                text = stringResource(R.string.solunar_title_dawn),
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
                text = stringResource(R.string.solunar_title_dusk),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun DawnDuskRow(
    label: String,
    dawn: LocalTime?,
    dusk: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TwilightTime(
            time = dawn,
            showPlaceholder = showPlaceholders,
            modifier = Modifier.weight(1f, fill = true)
        )
        AutoShrinkingText(
            text = label,
            modifier = Modifier.weight(1f, fill = true),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
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
    AutoShrinkingText(
        text = time?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.solunar_text_time_none),
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp)
            .placeholder(showPlaceholder),
        maxLines = 1,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge
    )
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            DawnDuskColumn(
                astronomicalDawn = null,
                nauticalDawn = null,
                civilDawn = null,
                civilDusk = null,
                nauticalDusk = null,
                astronomicalDusk = null,
                showPlaceholders = true
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            DawnDuskColumn(
                astronomicalDawn = LocalTime(12, 0, 0),
                nauticalDawn = LocalTime(12, 0, 1),
                civilDawn = LocalTime(12, 0, 2),
                civilDusk = null,
                nauticalDusk = null,
                astronomicalDusk = null,
                showPlaceholders = false
            )
        }
    }
}
