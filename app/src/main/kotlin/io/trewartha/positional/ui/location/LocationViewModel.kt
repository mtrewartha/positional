package io.trewartha.positional.ui.location

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.CoordinatesFormat
import io.trewartha.positional.domain.entities.Units
import io.trewartha.positional.ui.utils.format.LocationFormatter
import io.trewartha.positional.domain.entities.Locator
import io.trewartha.positional.ui.utils.ForViewModel
import io.trewartha.positional.ui.utils.mutableSharedViewModelEventFlow
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val app: Application,
    private val clipboardManager: ClipboardManager,
    private val locationFormatter: LocationFormatter,
    locator: Locator,
    private val prefs: SharedPreferences
) : AndroidViewModel(app) {

    private val accuracyVisibilityFlow: Flow<Boolean> =
        callbackFlow {
            if (prefs.contains(prefsKeyShowAccuracies))
                trySend(prefs.getBoolean(prefsKeyShowAccuracies, DEFAULT_SHOW_ACCURACIES))
            prefShowAccuraciesListener = PrefShowAccuraciesListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefShowAccuraciesListener)
            awaitClose {
                prefShowAccuraciesListener
                    ?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }

    private val coordinatesFormatFlow: Flow<CoordinatesFormat> =
        callbackFlow {
            if (prefs.contains(prefsKeyCoordinatesFormat))
                trySend(prefs.getString(prefsKeyCoordinatesFormat, null))
            prefCoordinatesFormatListener = PrefCoordinatesFormatListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefCoordinatesFormatListener)
            awaitClose {
                prefCoordinatesFormatListener?.let {
                    prefs.unregisterOnSharedPreferenceChangeListener(it)
                }
            }
        }.map {
            CoordinatesFormat.valueOf(it!!.uppercase())
        }.catch {
            emit(DEFAULT_COORDINATES_FORMAT)
        }

    private val prefsKeyCoordinatesFormat = app.getString(R.string.settings_coordinates_format_key)
    private val prefsKeyScreenLock = app.getString(R.string.settings_screen_lock_key)
    private val prefsKeyShowAccuracies = app.getString(R.string.settings_show_accuracies_key)
    private val prefsKeyUnits = app.getString(R.string.settings_units_key)
    private var prefCoordinatesFormatListener: PrefCoordinatesFormatListener? = null
    private var prefScreenLockListener: PrefScreenLockListener? = null
    private var prefShowAccuraciesListener: PrefShowAccuraciesListener? = null
    private var prefUnitsListener: PrefUnitsListener? = null

    private val unitsFlow: Flow<Units> =
        callbackFlow {
            if (prefs.contains(prefsKeyUnits))
                trySend(prefs.getString(prefsKeyUnits, null))
            prefUnitsListener = PrefUnitsListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefUnitsListener)
            awaitClose {
                prefUnitsListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map {
            Units.valueOf(it!!.uppercase())
        }.catch {
            emit(DEFAULT_UNITS)
        }

    private val screenLockEnabledFlow: StateFlow<Boolean> =
        callbackFlow {
            prefScreenLockListener = PrefScreenLockListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefScreenLockListener)
            awaitClose {
                prefScreenLockListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            prefs.getBoolean(prefsKeyScreenLock, DEFAULT_SCREEN_LOCK)
        )

    val state: StateFlow<LocationState?> = combine(
        accuracyVisibilityFlow,
        locator.locationFlow,
        coordinatesFormatFlow,
        screenLockEnabledFlow,
        unitsFlow,
    ) { showAccuracies, location, coordinatesFormat, screenLockEnabled, units ->
        val accuracy = locationFormatter.getCoordinatesAccuracy(location, units)
        val (coordinates, maxLines) = locationFormatter.getCoordinates(location, coordinatesFormat)
        val coordinatesForCopy =
            locationFormatter.getCoordinatesForCopy(location, coordinatesFormat)
        val bearing = locationFormatter.getBearing(location)
        val bearingAccuracy = locationFormatter.getBearingAccuracy(location)
        val altitude = locationFormatter.getAltitude(location, units)
        val altitudeAccuracy = locationFormatter.getAltitudeAccuracy(location, units)
        val speed = locationFormatter.getSpeed(location, units)
        val speedAccuracy = locationFormatter.getSpeedAccuracy(location, units)
        val updatedAt = locationFormatter.getTimestamp(location)

        LocationState(
            coordinates = coordinates,
            maxLines = maxLines,
            coordinatesForCopy = coordinatesForCopy,
            stats = LocationState.Stats(
                accuracy = accuracy,
                bearing = bearing,
                bearingAccuracy = bearingAccuracy,
                altitude = altitude,
                altitudeAccuracy = altitudeAccuracy,
                speed = speed,
                speedAccuracy = speedAccuracy,
                showAccuracies = showAccuracies,
                updatedAt = updatedAt,
            ),
            screenLockEnabled = screenLockEnabled,
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.ForViewModel,
        initialValue = null
    )

    val events: Flow<LocationEvent>
        get() = _events
    private val _events = mutableSharedViewModelEventFlow<LocationEvent>()

    fun onCopyClick() {
        viewModelScope.launch {
            val coordinates = state.value?.coordinatesForCopy
            val event = if (coordinates.isNullOrBlank()) {
                LocationEvent.ShowCoordinatesCopyErrorSnackbar
            } else {
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        app.getString(R.string.location_coordinates_copy_label),
                        coordinates
                    )
                )
                LocationEvent.ShowCoordinatesCopySuccessBothSnackbar
            }
            _events.emit(event)
        }
    }

    fun onHelpClick() {
        viewModelScope.launch { _events.emit(LocationEvent.NavigateToLocationHelp) }
    }

    fun onScreenLockCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            Timber.i("Screen lock checked change = $checked")
            prefs.edit { putBoolean(prefsKeyScreenLock, checked) }
            val event = if (checked)
                LocationEvent.ShowScreenLockedSnackbar
            else
                LocationEvent.ShowScreenUnlockedSnackbar
            _events.emit(event)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            val coordinates = state.value?.coordinates
            val event = if (coordinates.isNullOrBlank())
                LocationEvent.ShowCoordinatesShareErrorSnackbar
            else
                LocationEvent.ShowCoordinatesShareSheet(coordinates)
            _events.emit(event)
        }
    }

    private inner class PrefCoordinatesFormatListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCoordinatesFormat)
                producerScope.trySend(sharedPrefs.getString(key, null))
        }
    }

    private inner class PrefScreenLockListener(
        val producerScope: ProducerScope<Boolean>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyScreenLock)
                producerScope.trySend(sharedPrefs.getBoolean(key, DEFAULT_SCREEN_LOCK))
        }
    }

    private inner class PrefShowAccuraciesListener(
        val producerScope: ProducerScope<Boolean>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyShowAccuracies)
                producerScope.trySend(sharedPrefs.getBoolean(key, DEFAULT_SHOW_ACCURACIES))
        }
    }

    private inner class PrefUnitsListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyUnits)
                producerScope.trySend(sharedPrefs.getString(key, null))
        }
    }

    companion object {
        private val DEFAULT_COORDINATES_FORMAT = CoordinatesFormat.DD
        private const val DEFAULT_SCREEN_LOCK = false
        private const val DEFAULT_SHOW_ACCURACIES = true
        private val DEFAULT_UNITS = Units.METRIC
    }
}