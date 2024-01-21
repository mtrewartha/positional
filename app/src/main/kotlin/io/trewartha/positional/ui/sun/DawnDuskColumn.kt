package io.trewartha.positional.ui.sun

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
import io.trewartha.positional.ui.State
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.AutoShrinkingText
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalTime

@Composable
fun DawnDuskColumn(
    astronomicalDawn: State<LocalTime?, *>,
    nauticalDawn: State<LocalTime?, *>,
    civilDawn: State<LocalTime?, *>,
    civilDusk: State<LocalTime?, *>,
    nauticalDusk: State<LocalTime?, *>,
    astronomicalDusk: State<LocalTime?, *>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeaderRow(modifier = Modifier.fillMaxWidth())
        DawnDuskRow(
            label = stringResource(R.string.sun_label_dawn_dusk_civil),
            dawn = civilDawn,
            dusk = civilDusk,
            modifier = Modifier.fillMaxWidth()
        )
        DawnDuskRow(
            label = stringResource(R.string.sun_label_dawn_dusk_nautical),
            dawn = nauticalDawn,
            dusk = nauticalDusk,
            modifier = Modifier.fillMaxWidth()
        )
        DawnDuskRow(
            label = stringResource(R.string.sun_label_dawn_dusk_astronomical),
            dawn = astronomicalDawn,
            dusk = astronomicalDusk,
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
                text = stringResource(R.string.sun_title_dawn),
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
                text = stringResource(R.string.sun_title_dusk),
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
    dawn: State<LocalTime?, *>,
    dusk: State<LocalTime?, *>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TwilightTime(
            time = dawn,
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
            modifier = Modifier.weight(1f, fill = true)
        )
    }
}

@Composable
private fun TwilightTime(time: State<LocalTime?, *>, modifier: Modifier = Modifier) {
    AutoShrinkingText(
        text = time.dataOrNull?.let { LocalDateTimeFormatter.current.formatTime(it) }
            ?: stringResource(R.string.sun_text_time_none),
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp)
            .placeholder(time is State.Loading),
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
                astronomicalDawn = State.Loading,
                nauticalDawn = State.Loading,
                civilDawn = State.Loading,
                civilDusk = State.Loading,
                nauticalDusk = State.Loading,
                astronomicalDusk = State.Loading
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
                astronomicalDawn = State.Loaded<LocalTime?>(LocalTime(12, 0, 0)),
                nauticalDawn = State.Loaded<LocalTime?>(LocalTime(12, 0, 1)),
                civilDawn = State.Loaded<LocalTime?>(LocalTime(12, 0, 2)),
                civilDusk = State.Loaded<LocalTime?>(null),
                nauticalDusk = State.Loaded<LocalTime?>(null),
                astronomicalDusk = State.Loaded<LocalTime?>(null)
            )
        }
    }
}
