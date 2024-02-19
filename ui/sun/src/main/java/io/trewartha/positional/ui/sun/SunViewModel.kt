package io.trewartha.positional.ui.sun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.Locator
import io.trewartha.positional.data.sun.SolarTimesRepository
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.ui.core.State
import io.trewartha.positional.ui.core.flow.ForViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SunViewModel @Inject constructor(
    private val clock: Clock,
    locator: Locator,
    private val solarTimesRepository: SolarTimesRepository
) : ViewModel() {

    private val coordinates: Flow<Coordinates> =
        locator.location.map { it.coordinates }
            .shareIn(viewModelScope, SharingStarted.ForViewModel, replay = 1)

    private val today: LocalDate
        get() = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private val todaysDate: StateFlow<LocalDate> =
        flow {
            while (viewModelScope.isActive) {
                emit(today)
                delay(1.seconds)
            }
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.ForViewModel, today)

    private val selectedDate = MutableStateFlow(todaysDate.value)

    val state: StateFlow<SunState> =
        combine(todaysDate, selectedDate, coordinates) { todaysDate, selectedDate, coordinates ->
            with(solarTimesRepository) {
                SunState(
                    todaysDate = todaysDate,
                    selectedDate = selectedDate,
                    astronomicalDawn = State.Loaded(getAstronomicalDawn(coordinates, selectedDate)),
                    nauticalDawn = State.Loaded(getNauticalDawn(coordinates, selectedDate)),
                    civilDawn = State.Loaded(getCivilDawn(coordinates, selectedDate)),
                    sunrise = State.Loaded(getSunrise(coordinates, selectedDate)),
                    sunset = State.Loaded(getSunset(coordinates, selectedDate)),
                    civilDusk = State.Loaded(getCivilDusk(coordinates, selectedDate)),
                    nauticalDusk = State.Loaded(getNauticalDusk(coordinates, selectedDate)),
                    astronomicalDusk = State.Loaded(getAstronomicalDusk(coordinates, selectedDate))
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.ForViewModel,
            initialValue = SunState(
                todaysDate = todaysDate.value,
                selectedDate = selectedDate.value,
                astronomicalDawn = State.Loading,
                nauticalDawn = State.Loading,
                civilDawn = State.Loading,
                sunrise = State.Loading,
                sunset = State.Loading,
                civilDusk = State.Loading,
                nauticalDusk = State.Loading,
                astronomicalDusk = State.Loading
            )
        )

    fun onSelectedDateChange(date: LocalDate) {
        selectedDate.update { date }
    }

    fun onSelectedDateChangedToToday() {
        selectedDate.update { today }
    }

    fun onSelectedDateDecrement() {
        selectedDate.update { it - DatePeriod(days = 1) }
    }

    fun onSelectedDateIncrement() {
        selectedDate.update { it + DatePeriod(days = 1) }
    }
}
