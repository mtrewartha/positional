package io.trewartha.positional.sun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.R as CoreR
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.core.ui.modifier.placeholder
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
public fun SunDateControls(
    selectedDate: LocalDate?,
    onPreviousDayClick: () -> Unit,
    onDateSelection: (LocalDate) -> Unit,
    onNextDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    if (showDatePicker) {
        DatePickerDialog(
            initialSelectedDate = selectedDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelection = { date ->
                showDatePicker = false
                onDateSelection(date)
            }
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        PreviousButton(
            onPreviousDayClick,
            enabled = selectedDate != null
        )
        TextButton(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .placeholder(visible = selectedDate == null)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                val minTextWidth = 128.dp
                Text(
                    text = selectedDate
                        ?.let { LocalDateTimeFormatter.current.formatFullDayOfWeek(it) }
                        ?: "",
                    modifier = Modifier.widthIn(min = minTextWidth),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = selectedDate?.let { LocalDateTimeFormatter.current.formatDate(it) }
                        ?: "",
                    modifier = Modifier
                        .widthIn(min = minTextWidth),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        NextButton(
            onNextDayClick,
            enabled = selectedDate != null
        )
    }
}

@Composable
private fun DatePickerDialog(
    initialSelectedDate: LocalDate?,
    onDismissRequest: () -> Unit,
    onDateSelection: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate?.atTime(0, 0)
            ?.toInstant(TimeZone.UTC)
            ?.toEpochMilliseconds()
    )
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = click@{
                    val date = datePickerState.selectedDateMillis
                        ?.let { Instant.fromEpochMilliseconds(it) }
                        ?.toLocalDateTime(TimeZone.UTC)
                        ?.date
                        ?: return@click
                    onDateSelection(date)
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(stringResource(CoreR.string.core_ui_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(CoreR.string.core_ui_cancel))
            }
        },
    ) {
        DatePicker(datePickerState)
    }
}

@Composable
private fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
            contentDescription = stringResource(
                R.string.feature_sun_ui_button_next_day_content_description
            )
        )
    }
}

@Composable
private fun PreviousButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.NavigateBefore,
            contentDescription = stringResource(
                R.string.feature_sun_ui_button_previous_day_content_description
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        Surface {
            SunDateControls(
                selectedDate = null,
                onPreviousDayClick = {},
                onDateSelection = {},
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
            SunDateControls(
                selectedDate = LocalDate(2020, 1, 1),
                onPreviousDayClick = {},
                onDateSelection = {},
                onNextDayClick = {}
            )
        }
    }
}
