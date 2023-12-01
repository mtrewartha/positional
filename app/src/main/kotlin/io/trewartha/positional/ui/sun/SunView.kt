package io.trewartha.positional.ui.sun

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.data.sun.SolarTimes
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.bottomNavEnterTransition
import io.trewartha.positional.ui.bottomNavExitTransition
import io.trewartha.positional.ui.bottomNavPopEnterTransition
import io.trewartha.positional.ui.bottomNavPopExitTransition
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.locals.LocalLocale
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun NavGraphBuilder.sunView(navController: NavController, contentPadding: PaddingValues) {
    composable(
        NavDestination.Sun.route,
        enterTransition = bottomNavEnterTransition(),
        exitTransition = bottomNavExitTransition(NavDestination.SunHelp.route),
        popEnterTransition = bottomNavPopEnterTransition(NavDestination.SunHelp.route),
        popExitTransition = bottomNavPopExitTransition()
    ) {
        val viewModel: SunViewModel = hiltViewModel()
        val todaysDate by viewModel.todaysDate
            .collectAsStateWithLifecycle(initialValue = null)
        val selectedDate by viewModel.selectedDate
            .collectAsStateWithLifecycle(initialValue = null)
        val selectedDateTwilights by viewModel.selectedDateTwilights
            .collectAsStateWithLifecycle(initialValue = null)
        SunView(
            todaysDate = todaysDate,
            selectedDate = selectedDate,
            selectedDateTwilights = selectedDateTwilights,
            contentPadding = contentPadding,
            onSelectedDateDecrement = viewModel::onSelectedDateDecrement,
            onDateSelection = viewModel::onSelectedDateChange,
            onSelectedDateIncrement = viewModel::onSelectedDateIncrement,
            onJumpToTodayClick = viewModel::onSelectedDateChangedToToday,
            onHelpClick = { navController.navigate(NavDestination.SunHelp.route) }
        )
    }
}

@Composable
private fun SunView(
    todaysDate: LocalDate?,
    selectedDate: LocalDate?,
    selectedDateTwilights: SolarTimes?,
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
            .padding(dimensionResource(R.dimen.standard_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(36.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SunriseSunsetColumn(
                sunrise = selectedDateTwilights?.sunrise,
                sunset = selectedDateTwilights?.sunset,
                showPlaceholders = selectedDateTwilights == null,
                modifier = Modifier.fillMaxWidth()
            )
            DawnDuskColumn(
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
        Spacer(modifier = Modifier.height(12.dp))
        HelpButton(onClick = onHelpClick)
        Spacer(modifier = Modifier.height(24.dp))
        SunDateControls(
            selectedDate = selectedDate,
            onPreviousDayClick = onSelectedDateDecrement,
            onDateSelection = onDateSelection,
            onNextDayClick = onSelectedDateIncrement,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onJumpToTodayClick,
            enabled = todaysDate != null && todaysDate != selectedDate
        ) {
            Text(stringResource(R.string.sun_button_today))
        }
    }
}

@Composable
private fun HelpButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Rounded.HelpOutline,
            stringResource(R.string.sun_button_help_content_description),
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        SunView(
            todaysDate = null,
            selectedDate = null,
            selectedDateTwilights = null,
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
            val locale = LocalLocale.current
            CompositionLocalProvider(
                LocalDateTimeFormatter provides SystemDateTimeFormatter(locale)
            ) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                SunView(
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
