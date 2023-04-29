package io.trewartha.positional.ui.location

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.PositionalApplication
import io.trewartha.positional.R
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.units.Units
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.settings.SettingsRepository
import io.trewartha.positional.domain.utils.flow.throttleFirst
import io.trewartha.positional.ui.utils.format.coordinates.CoordinatesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DecimalDegreesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesDecimalMinutesFormatter
import io.trewartha.positional.ui.utils.format.coordinates.DegreesMinutesSecondsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.MgrsFormatter
import io.trewartha.positional.ui.utils.format.coordinates.UtmFormatter
import io.trewartha.positional.ui.utils.mutableSharedViewModelEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,
    getLocationUseCase: GetLocationUseCase,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val clipboardManager by lazy {
        application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    private val location: Flow<Location?> =
        (getLocationUseCase() as Flow<Location?>)
            .throttleFirst(LOCATION_FLOW_PERIOD)
            .onStart { emit(null) }
    private val locale: Locale
        get() = checkNotNull(LocaleListCompat.getDefault()[0])
    private val coordinatesFormat: Flow<CoordinatesFormat> = settingsRepository.coordinatesFormat
    private var copyShareFormatter: CoordinatesFormatter? = null
    private val accuracyVisibility: Flow<Boolean> = settingsRepository.hideAccuracies.map { !it }
    private val units: Flow<Units> = settingsRepository.units

    val events: Flow<LocationEvent>
        get() = _events
    private val _events = mutableSharedViewModelEventFlow<LocationEvent>()

    val locationState: StateFlow<LocationState> =
        combine(
            location.onStart { emit(null) },
            coordinatesFormat,
            units,
            accuracyVisibility,
        ) { location, coordinatesFormat, units, accuracyVisibility ->
            LocationState(
                location,
                coordinatesFormat,
                units,
                accuracyVisibility
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            initialValue = LocationState(
                location = null,
                coordinatesFormat = CoordinatesFormat.DD,
                units = Units.METRIC,
                showAccuracies = false
            )
        )

    val screenLockEnabled: StateFlow<Boolean?> =
        settingsRepository.screenLockEnabled
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    fun onCopyClick() {
        viewModelScope.launch {
            val (location, coordinatesFormat) = locationState.value
            if (location == null) return@launch
            val coordinates = Coordinates(location.latitude, location.longitude)
            val formattedCoordinates =
                getCopyShareFormatter(coordinatesFormat).formatForCopy(coordinates)
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    getApplication<PositionalApplication>()
                        .getString(R.string.location_coordinates_copy_label),
                    formattedCoordinates
                )
            )
            _events.emit(LocationEvent.ShowCoordinatesCopySuccessBothSnackbar)
        }
    }

    fun onLaunchClick() {
        viewModelScope.launch {
            val currentLocation = locationState.value.location ?: return@launch
            _events.emit(
                LocationEvent.NavigateToGeoActivity(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    currentLocation.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        }
    }

    fun onScreenLockCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            settingsRepository.setScreenLockEnabled(checked)
            val event = if (checked)
                LocationEvent.ShowScreenLockedSnackbar
            else
                LocationEvent.ShowScreenUnlockedSnackbar
            _events.emit(event)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            val (location, coordinatesFormat) = locationState.value
            if (location == null) return@launch
            val coordinates = Coordinates(location.latitude, location.longitude)
            val formattedCoordinates =
                getCopyShareFormatter(coordinatesFormat).formatForCopy(coordinates)
            _events.emit(LocationEvent.ShowCoordinatesShareSheet(formattedCoordinates))
        }
    }

    private fun getCopyShareFormatter(coordinatesFormat: CoordinatesFormat): CoordinatesFormatter =
        copyShareFormatter?.takeIf { it.format == coordinatesFormat }
            ?: run {
                val context = getApplication<PositionalApplication>()
                when (coordinatesFormat) {
                    CoordinatesFormat.DD -> DecimalDegreesFormatter(context, locale)
                    CoordinatesFormat.DDM -> DegreesDecimalMinutesFormatter(context, locale)
                    CoordinatesFormat.DMS -> DegreesMinutesSecondsFormatter(context, locale)
                    CoordinatesFormat.MGRS -> MgrsFormatter(context)
                    CoordinatesFormat.UTM -> UtmFormatter(context, locale)
                }.also { copyShareFormatter = it }
            }

    companion object {
        private val LOCATION_FLOW_PERIOD = 2.seconds
    }
}
