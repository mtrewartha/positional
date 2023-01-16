package io.trewartha.positional.ui.twilight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.twilight.DailyTwilights
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.twilight.GetDailyTwilightsUseCase
import io.trewartha.positional.ui.utils.ForViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class TwilightViewModel @Inject constructor(
    getLocationUseCase: GetLocationUseCase,
    private val getDailyTwilightTimes: GetDailyTwilightsUseCase,
) : ViewModel() {

    private val today: LocalDate
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    /**
     * The currently selected date
     */
    val date: Flow<LocalDate>
        get() = _date
    private val _date = MutableStateFlow(today)

    /**
     * Today's twilight times at the device's current location
     */
    val dateTwilights: Flow<DailyTwilights>
        get() = combine(date, location) { date, location ->
            getDailyTwilightTimes(date, location.latitude, location.longitude)
        }

    fun setDate(date: LocalDate) {
        _date.update { date }
    }

    private val location: Flow<Location> =
        getLocationUseCase().shareIn(viewModelScope, SharingStarted.ForViewModel, replay = 1)
}
