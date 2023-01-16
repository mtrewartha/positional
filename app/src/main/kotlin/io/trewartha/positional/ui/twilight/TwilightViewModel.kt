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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class TwilightViewModel @Inject constructor(
    getLocationUseCase: GetLocationUseCase,
    private val getDailyTwilightTimes: GetDailyTwilightsUseCase,
) : ViewModel() {

    /**
     * The current instant in time
     */
    val currentInstant: Flow<Instant>
        get() = lastLocationInstant

    /**
     * The date and time at which the twilight times were updated
     */
    val updateDateTime: Flow<Instant>
        get() = lastLocationInstant

    /**
     * Today's twilight times at the device's current location
     */
    val todaysTwilightTimes: Flow<DailyTwilights>
        get() = combine(currentInstant, currentLocation) { instant, location ->
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            getDailyTwilightTimes(localDate, location.latitude, location.longitude)
        }

    private val currentLocation: Flow<Location> =
        getLocationUseCase().shareIn(viewModelScope, SharingStarted.ForViewModel, replay = 1)

    private val lastLocationInstant = currentLocation.map { it.timestamp }
}
