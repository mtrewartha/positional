package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.trewartha.positional.R
import io.trewartha.positional.data.solunar.SolarTimes
import io.trewartha.positional.ui.IconButton
import io.trewartha.positional.ui.NavDestination.SolunarInfo
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
fun SolunarView(
    navController: NavController,
    viewModel: SolunarViewModel = hiltViewModel()
) {
    val todaysDate by viewModel.todaysDate.collectAsState(initial = null)
    val selectedDate by viewModel.selectedDate.collectAsState(initial = null)
    val selectedDateTwilights by viewModel.selectedDateTwilights.collectAsState(initial = null)
    SolunarView(
        todaysDate = todaysDate,
        selectedDate = selectedDate,
        selectedDateTwilights = selectedDateTwilights,
        onNavigateToInfo = { navController.navigate(SolunarInfo.route) },
        onSelectedDateDecrement = viewModel::onSelectedDateDecrement,
        onSelectedDateIncrement = viewModel::onSelectedDateIncrement,
        onJumpToTodayClick = viewModel::onSelectedDateChangedToToday
    )
}

@Composable
private fun SolunarView(
    todaysDate: LocalDate?,
    selectedDate: LocalDate?,
    selectedDateTwilights: SolarTimes?,
    onNavigateToInfo: () -> Unit,
    onSelectedDateDecrement: () -> Unit,
    onSelectedDateIncrement: () -> Unit,
    onJumpToTodayClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onNavigateToInfo) {
                        Icon(
                            Icons.Rounded.Info,
                            stringResource(R.string.solunar_button_info_content_description),
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        val outerPadding = 16.dp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(outerPadding)
            ) {
                // TODO: Do these need to be cards? Maybe the content is better off just sitting on the surface behind like the other screens
                SunriseSunsetCard(
                    sunrise = selectedDateTwilights?.sunrise,
                    sunset = selectedDateTwilights?.sunset,
                    showPlaceholders = selectedDateTwilights == null,
                    modifier = Modifier.fillMaxWidth()
                )
                DawnDuskCard(
                    astronomicalDawn = selectedDateTwilights?.astronomicalDawn,
                    nauticalDawn = selectedDateTwilights?.nauticalDawn,
                    civilDawn = selectedDateTwilights?.civilDawn,
                    civilDusk = selectedDateTwilights?.civilDusk,
                    nauticalDusk = selectedDateTwilights?.nauticalDusk,
                    astronomicalDusk = selectedDateTwilights?.astronomicalDusk,
                    showPlaceholders = selectedDateTwilights == null,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            // TODO: Move controls and jump button to bottom sheet once Compose M3 lib supports it
            SolunarDateControls(
                selectedDate = selectedDate,
                onPreviousDayClick = onSelectedDateDecrement,
                onNextDayClick = onSelectedDateIncrement,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onJumpToTodayClick,
                enabled = todaysDate != null && todaysDate != selectedDate
            ) {
                Text(stringResource(R.string.solunar_button_today))
            }
        }
    }
}

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        SolunarView(
            todaysDate = null,
            selectedDate = null,
            selectedDateTwilights = null,
            onNavigateToInfo = {},
            onSelectedDateDecrement = {},
            onSelectedDateIncrement = {},
            onJumpToTodayClick = {}
        )
    }
}

@ThemePreviews
@Composable
private fun LoadedPreview() {
    PositionalTheme {
        CompositionLocalProvider(LocalDateTimeFormatter provides SystemDateTimeFormatter()) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            SolunarView(
                todaysDate = today,
                selectedDate = today,
                selectedDateTwilights = SolarTimes(
                    astronomicalDawn = null,
                    nauticalDawn = null,
                    civilDawn = LocalTime(11, 58, 0),
                    sunrise = LocalTime(11, 59, 0),
                    sunset = LocalTime(12, 1, 0),
                    civilDusk = LocalTime(12, 2, 0),
                    nauticalDusk = null,
                    astronomicalDusk = null,
                ),
                onNavigateToInfo = {},
                onSelectedDateDecrement = {},
                onSelectedDateIncrement = {},
                onJumpToTodayClick = {}
            )
        }
    }
}
