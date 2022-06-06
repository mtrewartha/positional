package io.trewartha.positional.ui.location

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.location.Location
import android.os.Looper
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.CoordinatesFormat
import io.trewartha.positional.domain.entities.Units
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.ui.utils.ForViewModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val app: Application,
    private val clipboardManager: ClipboardManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationFormatter: LocationFormatter,
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

    @SuppressLint("MissingPermission") // The linter doesn't know this is handled in a Flow.catch
    private val locationFlow: Flow<Location> =
        callbackFlow {
            var firstLocationUpdateTrace: Trace? =
                FirebasePerformance.getInstance().newTrace("first_location")
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    trySend(location)
                    if (firstLocationUpdateTrace != null) {
                        val accuracyCounter = when (location.accuracy) {
                            in 0.0f.rangeTo(5.0f) -> COUNTER_ACCURACY_VERY_HIGH
                            in 5.0f.rangeTo(10.0f) -> COUNTER_ACCURACY_HIGH
                            in 10.0f.rangeTo(15.0f) -> COUNTER_ACCURACY_MEDIUM
                            in 15.0f.rangeTo(20.0f) -> COUNTER_ACCURACY_LOW
                            else -> COUNTER_ACCURACY_VERY_LOW
                        }
                        firstLocationUpdateTrace?.incrementMetric(accuracyCounter, 1L)
                        firstLocationUpdateTrace?.stop()
                        firstLocationUpdateTrace = null
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    Timber.d("Location availability changed to $locationAvailability")
                }
            }

            val locationRequest = LocationRequest.create()
                .setPriority(LOCATION_UPDATE_PRIORITY)
                .setInterval(LOCATION_UPDATE_INTERVAL_MS)
            Timber.i("Requesting location updates: $locationRequest")
            if (firstLocationUpdateTrace == null) {
                firstLocationUpdateTrace?.start()
            }

            suspendCancellableCoroutine<Exception?> { continuation ->
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ).addOnCompleteListener { continuation.resume(it.exception) {} }
            }?.let { throw it }

            awaitClose {
                Timber.i("Suspending location updates")
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }.onStart {
            Timber.i("Starting location flow")
        }.onEach {
            Timber.i("Location update received")
        }.retry { throwable ->
            if (throwable is SecurityException) {
                Timber.w("Waiting for location permissions to be granted")
                delay(1.seconds)
                true
            } else {
                Timber.w(throwable, "Waiting for location permissions to be granted")
                throw throwable
            }
        }.onCompletion {
            Timber.i("Location flow completed")
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
        locationFlow,
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
        val elevation = locationFormatter.getElevation(location, units)
        val elevationAccuracy = locationFormatter.getElevationAccuracy(location, units)
        val speed = locationFormatter.getSpeed(location, units)
        val speedAccuracy = locationFormatter.getSpeedAccuracy(location, units)
        val updatedAt = locationFormatter.getTimestamp(location)

        LocationState(
            coordinates = coordinates,
            maxLines = maxLines,
            coordinatesForCopy = coordinatesForCopy,
            accuracy = accuracy,
            bearing = bearing,
            bearingAccuracy = bearingAccuracy,
            elevation = elevation,
            elevationAccuracy = elevationAccuracy,
            speed = speed,
            speedAccuracy = speedAccuracy,
            showAccuracies = showAccuracies,
            updatedAt = updatedAt,
            screenLockEnabled = screenLockEnabled,
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.ForViewModel,
        initialValue = null
    )

    val events: Flow<LocationEvent>
        get() = _events
    private val _events = MutableSharedFlow<LocationEvent>()

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
        private const val COUNTER_ACCURACY_VERY_HIGH = "accuracy_very_high"
        private const val COUNTER_ACCURACY_HIGH = "accuracy_high"
        private const val COUNTER_ACCURACY_MEDIUM = "accuracy_medium"
        private const val COUNTER_ACCURACY_LOW = "accuracy_low"
        private const val COUNTER_ACCURACY_VERY_LOW = "accuracy_very_low"
        private val DEFAULT_COORDINATES_FORMAT = CoordinatesFormat.DD
        private const val DEFAULT_SCREEN_LOCK = false
        private const val DEFAULT_SHOW_ACCURACIES = true
        private val DEFAULT_UNITS = Units.METRIC
        private const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}