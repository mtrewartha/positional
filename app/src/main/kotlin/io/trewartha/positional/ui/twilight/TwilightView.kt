package io.trewartha.positional.ui.twilight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.data.twilight.DailyTwilights
import io.trewartha.positional.data.twilight.Twilights
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TwilightView(viewModel: TwilightViewModel = hiltViewModel()) {
    val todaysDate by viewModel.todaysDate.collectAsState(initial = null)
    val selectedDate by viewModel.selectedDate.collectAsState(initial = null)
    val selectedDateTwilights by viewModel.selectedDateTwilights.collectAsState(initial = null)
    TwilightView(
        todaysDate = todaysDate,
        selectedDate = selectedDate,
        selectedDateTwilights = selectedDateTwilights,
        onSelectedDateDecrement = viewModel::onSelectedDateDecrement,
        onSelectedDateIncrement = viewModel::onSelectedDateIncrement,
        onJumpToTodayClick = viewModel::onSelectedDateChangedToToday
    )
}

@Composable
private fun TwilightView(
    todaysDate: LocalDate?,
    selectedDate: LocalDate?,
    selectedDateTwilights: DailyTwilights?,
    onSelectedDateDecrement: () -> Unit,
    onSelectedDateIncrement: () -> Unit,
    onJumpToTodayClick: () -> Unit
) {
    Scaffold { contentPadding ->
        val outerPadding = 16.dp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(outerPadding)
        ) {
            val twilightVerticalPadding = 16.dp
            SunriseSunsetTimesRow(
                sunrise = selectedDateTwilights?.morningTwilights?.horizonTwilight,
                sunset = selectedDateTwilights?.eveningTwilights?.horizonTwilight,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            SunriseSunsetLabelsRow(
                modifier = Modifier.padding(bottom = twilightVerticalPadding)
            )
            TwilightsRow(
                label = stringResource(R.string.twilight_label_civil),
                morningValue = selectedDateTwilights?.morningTwilights?.civilTwilight,
                eveningValue = selectedDateTwilights?.eveningTwilights?.civilTwilight,
                modifier = Modifier.padding(bottom = twilightVerticalPadding)
            )
            TwilightsRow(
                label = stringResource(R.string.twilight_label_nautical),
                morningValue = selectedDateTwilights?.morningTwilights?.nauticalTwilight,
                eveningValue = selectedDateTwilights?.eveningTwilights?.nauticalTwilight,
                modifier = Modifier.padding(bottom = twilightVerticalPadding)
            )
            TwilightsRow(
                label = stringResource(R.string.twilight_label_astronomical),
                morningValue = selectedDateTwilights?.morningTwilights?.astronomicalTwilight,
                eveningValue = selectedDateTwilights?.eveningTwilights?.astronomicalTwilight,
                modifier = Modifier.padding(bottom = 36.dp)
            )
            DateControls(
                onPreviousDayClick = onSelectedDateDecrement,
                date = selectedDate,
                onNextDayClick = onSelectedDateIncrement,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedButton(
                onClick = onJumpToTodayClick,
                enabled = todaysDate != null && todaysDate != selectedDate
            ) {
                Text(stringResource(R.string.twilight_button_today))
            }
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        TwilightView(
            todaysDate = null,
            selectedDate = null,
            selectedDateTwilights = null,
            onSelectedDateDecrement = {},
            onSelectedDateIncrement = {},
            onJumpToTodayClick = {}
        )
    }
}

@ThemePreviews
@Composable
@Suppress("MagicNumber", "UnusedPrivateMember")
private fun LoadedPreview() {
    PositionalTheme {
        CompositionLocalProvider(LocalDateTimeFormatter provides SystemDateTimeFormatter()) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            TwilightView(
                todaysDate = today,
                selectedDate = today,
                selectedDateTwilights = DailyTwilights(
                    morningTwilights = Twilights(
                        horizonTwilight = LocalTime(11, 0, 0),
                        civilTwilight = LocalTime(12, 0, 0),
                        nauticalTwilight = null,
                        astronomicalTwilight = null,
                    ),
                    eveningTwilights = Twilights(
                        horizonTwilight = LocalTime(11, 0, 0),
                        civilTwilight = LocalTime(12, 0, 0),
                        nauticalTwilight = null,
                        astronomicalTwilight = null,
                    )
                ),
                onSelectedDateDecrement = {},
                onSelectedDateIncrement = {},
                onJumpToTodayClick = {}
            )
        }
    }
}
