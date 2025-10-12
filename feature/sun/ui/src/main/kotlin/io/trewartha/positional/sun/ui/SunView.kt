package io.trewartha.positional.sun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.trewartha.positional.core.ui.PositionalTheme
import io.trewartha.positional.core.ui.R as CoreR
import io.trewartha.positional.core.ui.State
import io.trewartha.positional.core.ui.locals.DefaultDateTimeFormatter
import io.trewartha.positional.core.ui.locals.LocalDateTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month

@Composable
public fun SunView(
    state: SunState,
    contentPadding: PaddingValues,
    onSelectedDateDecrement: () -> Unit,
    onDateSelection: (LocalDate) -> Unit,
    onSelectedDateIncrement: () -> Unit,
    onJumpToTodayClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(CoreR.dimen.core_ui_standard_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(36.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SunriseSunsetColumn(
                sunrise = state.sunrise,
                sunset = state.sunset,
                modifier = Modifier.fillMaxWidth()
            )
            DawnDuskColumn(
                astronomicalDawn = state.astronomicalDawn,
                nauticalDawn = state.nauticalDawn,
                civilDawn = state.civilDawn,
                civilDusk = state.civilDusk,
                nauticalDusk = state.nauticalDusk,
                astronomicalDusk = state.astronomicalDusk,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HelpButton(onClick = onHelpClick)
        Spacer(modifier = Modifier.height(24.dp))
        SunDateControls(
            selectedDate = state.selectedDate,
            onPreviousDayClick = onSelectedDateDecrement,
            onDateSelection = onDateSelection,
            onNextDayClick = onSelectedDateIncrement,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onJumpToTodayClick,
            enabled = state.todaysDate != state.selectedDate
        ) {
            Text(stringResource(R.string.feature_sun_ui_button_today))
        }
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Rounded.HelpOutline,
            stringResource(R.string.feature_sun_ui_button_help_content_description),
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        SunView(
            state = SunState(
                todaysDate = LocalDate(2024, Month.JANUARY, 1),
                selectedDate = LocalDate(2024, Month.JANUARY, 1),
                astronomicalDawn = State.Loading,
                nauticalDawn = State.Loading,
                civilDawn = State.Loading,
                sunrise = State.Loading,
                sunset = State.Loading,
                civilDusk = State.Loading,
                nauticalDusk = State.Loading,
                astronomicalDusk = State.Loading
            ),
            contentPadding = PaddingValues(),
            onSelectedDateDecrement = {},
            onDateSelection = {},
            onSelectedDateIncrement = {},
            onJumpToTodayClick = {},
            onHelpClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        Surface {
            CompositionLocalProvider(LocalDateTimeFormatter provides DefaultDateTimeFormatter) {
                SunView(
                    state = SunState(
                        todaysDate = LocalDate(2024, Month.JANUARY, 1),
                        selectedDate = LocalDate(2024, Month.JANUARY, 1),
                        astronomicalDawn = State.Loaded(LocalTime(12, 0, 0)),
                        nauticalDawn = State.Loaded(LocalTime(12, 0, 0)),
                        civilDawn = State.Loaded(LocalTime(12, 0, 0)),
                        sunrise = State.Loaded(LocalTime(12, 0, 0)),
                        sunset = State.Loaded(LocalTime(12, 0, 0)),
                        civilDusk = State.Loaded(LocalTime(12, 0, 0)),
                        nauticalDusk = State.Loaded(LocalTime(12, 0, 0)),
                        astronomicalDusk = State.Loaded(LocalTime(12, 0, 0))
                    ),
                    contentPadding = PaddingValues(),
                    onSelectedDateDecrement = {},
                    onDateSelection = {},
                    onSelectedDateIncrement = {},
                    onJumpToTodayClick = {},
                    onHelpClick = {}
                )
            }
        }
    }
}
