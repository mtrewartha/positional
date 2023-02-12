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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Divider
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import kotlinx.datetime.LocalTime

@Composable
fun DawnDuskCard(
    astronomicalDawn: LocalTime?,
    nauticalDawn: LocalTime?,
    civilDawn: LocalTime?,
    civilDusk: LocalTime?,
    nauticalDusk: LocalTime?,
    astronomicalDusk: LocalTime?,
    showPlaceholders: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.solunar_title_dawn),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.solunar_title_dusk),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            Divider(modifier = modifier.fillMaxWidth())
            DawnDuskRow(
                label = stringResource(R.string.solunar_label_dawn_dusk_civil),
                dawn = civilDawn,
                dusk = civilDusk,
                showPlaceholders = showPlaceholders
            )
            DawnDuskRow(
                label = stringResource(R.string.solunar_label_dawn_dusk_nautical),
                dawn = nauticalDawn,
                dusk = nauticalDusk,
                showPlaceholders = showPlaceholders
            )
            DawnDuskRow(
                label = stringResource(R.string.solunar_label_dawn_dusk_astronomical),
                dawn = astronomicalDawn,
                dusk = astronomicalDusk,
                showPlaceholders = showPlaceholders
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        DawnDuskCard(
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

@ThemePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        DawnDuskCard(
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
