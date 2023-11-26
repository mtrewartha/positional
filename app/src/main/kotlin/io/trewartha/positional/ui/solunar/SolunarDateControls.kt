package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalDate

@Composable
fun SolunarDateControls(
    selectedDate: LocalDate?,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = onPreviousDayClick,
            enabled = selectedDate != null,
            modifier = Modifier.weight(1f, fill = true)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.NavigateBefore,
                contentDescription = stringResource(
                    R.string.solunar_button_previous_day_content_description
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(1f, fill = true)
                .placeholder(visible = selectedDate == null)
        ) {
            val minTextWidth = 128.dp
            Text(
                text = selectedDate?.let { LocalDateTimeFormatter.current.formatFullDayOfWeek(it) }
                    ?: "",
                modifier = Modifier.widthIn(min = minTextWidth),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = selectedDate?.let { LocalDateTimeFormatter.current.formatDate(it) } ?: "",
                modifier = Modifier
                    .widthIn(min = minTextWidth),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            onClick = onNextDayClick,
            enabled = selectedDate != null,
            modifier = Modifier.weight(1f, fill = true)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                contentDescription = stringResource(
                    R.string.solunar_button_next_day_content_description
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            SolunarDateControls(
                selectedDate = null,
                onPreviousDayClick = {},
                onNextDayClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            SolunarDateControls(
                selectedDate = LocalDate(2020, 1, 1),
                onPreviousDayClick = {},
                onNextDayClick = {}
            )
        }
    }
}
