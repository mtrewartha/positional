package io.trewartha.positional.ui.twilight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NavigateBefore
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.LocalDate

@Composable
fun DateControls(
    onPreviousDayClick: () -> Unit,
    date: LocalDate?,
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
            enabled = date != null,
            modifier = Modifier.weight(1f, fill = true)
        ) {
            Icon(
                imageVector = Icons.Rounded.NavigateBefore,
                contentDescription = stringResource(
                    R.string.twilight_button_previous_day_content_description
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f, fill = true)
        ) {
            val minTextWidth = 128.dp
            Text(
                text = date?.let { LocalDateTimeFormatter.current.formatFullDayOfWeek(it) } ?: "",
                modifier = Modifier
                    .placeholder(visible = date == null)
                    .widthIn(min = minTextWidth),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = date?.let { LocalDateTimeFormatter.current.formatDate(it) } ?: "",
                modifier = Modifier
                    .placeholder(visible = date == null)
                    .widthIn(min = minTextWidth),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        IconButton(
            onClick = onNextDayClick,
            enabled = date != null,
            modifier = Modifier.weight(1f, fill = true)
        ) {
            Icon(
                imageVector = Icons.Rounded.NavigateNext,
                contentDescription = stringResource(
                    R.string.twilight_button_next_day_content_description
                )
            )
        }
    }
}
