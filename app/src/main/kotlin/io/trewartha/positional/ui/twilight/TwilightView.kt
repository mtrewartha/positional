package io.trewartha.positional.ui.twilight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import io.trewartha.positional.R
import io.trewartha.positional.data.twilight.DailyTwilights
import io.trewartha.positional.data.twilight.Twilights
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.ThemePreviews
import io.trewartha.positional.ui.locals.LocalDateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter
import io.trewartha.positional.ui.utils.placeholder
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TwilightView(viewModel: TwilightViewModel = hiltViewModel()) {
    val date by viewModel.date.collectAsState(initial = null)
    val dateTwilights by viewModel.dateTwilights.collectAsState(initial = null)
    TwilightView(date, dateTwilights)
}

@Composable
private fun TwilightView(
    date: LocalDate?,
    dateTwilights: DailyTwilights?
) {
    Scaffold { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            val pagerState = rememberPagerState()
            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val (timeOfDay, twilights) = when (page) {
                    0 -> TimeOfDay.MORNING to dateTwilights?.morningTwilights
                    else -> TimeOfDay.EVENING to dateTwilights?.eveningTwilights
                }
                DateTwilights(
                    date = date,
                    timeOfDay = timeOfDay,
                    twilights = twilights,
                    modifier = Modifier.fillMaxSize()
                )
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                activeColor = MaterialTheme.colorScheme.onSurface,
                inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        }
    }
}

@Composable
private fun DateTwilights(
    date: LocalDate?,
    timeOfDay: TimeOfDay,
    twilights: Twilights?,
    modifier: Modifier = Modifier
) {
    val titleText = stringResource(
        id = when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.twilight_title_morning
            TimeOfDay.EVENING -> R.string.twilight_title_evening
        }
    )
    val horizonLabelText = stringResource(
        id = when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.twilight_body_morning_horizon
            TimeOfDay.EVENING -> R.string.twilight_body_evening_horizon
        }
    )
    val civilLabelText = stringResource(
        id = when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.twilight_body_morning_civil
            TimeOfDay.EVENING -> R.string.twilight_body_evening_civil
        }
    )
    val nauticalLabelText = stringResource(
        id = when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.twilight_body_morning_nautical
            TimeOfDay.EVENING -> R.string.twilight_body_evening_nautical
        }
    )
    val astronomicalLabelText = stringResource(
        id = when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.twilight_body_morning_astronomical
            TimeOfDay.EVENING -> R.string.twilight_body_evening_astronomical
        }
    )
    LazyColumn(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        stickyHeader {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = date?.let { LocalDateTimeFormatter.current.formatDate(it) }
                        .orEmpty(),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 64.dp)
                        .placeholder(visible = date == null),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        items(
            items = listOf(
                horizonLabelText to twilights?.horizonTwilight,
                civilLabelText to twilights?.civilTwilight,
                nauticalLabelText to twilights?.nauticalTwilight,
                astronomicalLabelText to twilights?.astronomicalTwilight,
            )
        ) { (label, value) ->
            TwilightRow(
                label = label,
                value = value,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .placeholder(visible = twilights == null)
            )
        }
    }
}

@Composable
private fun TwilightRow(label: String, value: LocalTime?, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value
                ?.let { LocalDateTimeFormatter.current.formatTime(it) }
                ?: stringResource(R.string.twilight_body_none),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.defaultMinSize(minWidth = 64.dp)
        )
    }
}

private enum class TimeOfDay { MORNING, EVENING }

@ThemePreviews
@Composable
private fun LoadingPreview() {
    PositionalTheme {
        TwilightView(date = null, dateTwilights = null)
    }
}

@ThemePreviews
@Composable
@Suppress("MagicNumber", "UnusedPrivateMember")
private fun LoadedPreview() {
    PositionalTheme {
        CompositionLocalProvider(LocalDateTimeFormatter provides SystemDateTimeFormatter()) {
            TwilightView(
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                dateTwilights = DailyTwilights(
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
                )
            )
        }
    }
}
