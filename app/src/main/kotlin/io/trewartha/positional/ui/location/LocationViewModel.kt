package io.trewartha.positional.ui.location

import android.Manifest
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val app: Application,
    private val savedStateHandle: SavedStateHandle,
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

    private val coordinatesFormatFlow: Flow<CoordinatesFormat?> =
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

    private val havePermissions: MutableStateFlow<Boolean> = MutableStateFlow(checkPermissions())

    private val locationFlow: Flow<Location?> =
        havePermissions.filter { it }
            .flatMapLatest {
                callbackFlow {
                    var firstLocationUpdateTrace: Trace? =
                        FirebasePerformance.getInstance().newTrace("first_location")
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            val location = locationResult?.lastLocation ?: return
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

                    try {
                        val locationRequest = LocationRequest.create()
                            .setPriority(LOCATION_UPDATE_PRIORITY)
                            .setInterval(LOCATION_UPDATE_INTERVAL_MS)
                        Timber.i("Requesting location updates: $locationRequest")
                        if (firstLocationUpdateTrace == null) {
                            firstLocationUpdateTrace?.start()
                        }
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    } catch (e: SecurityException) {
                        Timber.w(
                            e,
                            "Don't have location permissions, no location updates will be received"
                        )
                    }

                    awaitClose {
                        Timber.i("Suspending location updates")
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    }
                }
            }.onStart {
                Timber.i("Starting location flow")
            }.onCompletion {
                if (it == null || it is CancellationException)
                    Timber.i("Completed location flow")
                else
                    Timber.e(it, "Completed location flow abnormally")
            }

    private val _events = MutableSharedFlow<LocationScreenEvent>()

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

    val coordinatesStateFlow: StateFlow<CoordinatesState> =
        combine(
            locationFlow.mapNotNull { it },
            coordinatesFormatFlow.mapNotNull { it }
        ) { location, format ->
            location.toCoordinatesState(format)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            CoordinatesState(app.getString(R.string.common_dash), 1)
        )

    val locationStatsStateFlow: StateFlow<LocationStatsState> =
        combine(
            accuracyVisibilityFlow,
            locationFlow,
            unitsFlow
        ) { accuracyVisibility, location, units ->
            LocationStatsState(
                accuracy = getAccuracy(location, units),
                bearing = getBearing(location),
                bearingAccuracy = getBearingAccuracy(location),
                elevation = getElevation(location, units),
                elevationAccuracy = getElevationAccuracy(location, units),
                speed = getSpeed(location, units),
                speedAccuracy = getSpeedAccuracy(location, units),
                showAccuracies = accuracyVisibility,
                updatedAt = getUpdatedAt(location)
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            initialValue = LocationStatsState(
                accuracy = app.getString(R.string.common_dash),
                bearing = app.getString(R.string.common_dash),
                bearingAccuracy = null,
                elevation = app.getString(R.string.common_dash),
                elevationAccuracy = null,
                speed = app.getString(R.string.common_dash),
                speedAccuracy = null,
                showAccuracies = false,
                updatedAt = ""
            )
        )

    val events: Flow<LocationScreenEvent>
        get() = _events

    val screenLockEnabledFlow: StateFlow<Boolean> =
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

    fun onCopyClick() {
        viewModelScope.launch {
            val coordinates = coordinatesStateFlow.value.coordinates
            val event = if (coordinates.isBlank()) {
                LocationScreenEvent.ShowCoordinatesCopyErrorSnackbar
            } else {
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        app.getString(R.string.location_coordinates_copy_label),
                        coordinates
                    )
                )
                LocationScreenEvent.ShowCoordinatesCopySuccessBothSnackbar
            }
            _events.emit(event)
        }
    }

    fun onHelpClick() {
        viewModelScope.launch { _events.emit(LocationScreenEvent.NavigateToLocationHelp) }
    }

    fun onPermissionsChange() {
        havePermissions.tryEmit(checkPermissions())
    }

    fun onScreenLockCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            Timber.i("Screen lock checked change = $checked")
            prefs.edit { putBoolean(prefsKeyScreenLock, checked) }
            val event = if (checked)
                LocationScreenEvent.ShowScreenLockedSnackbar
            else
                LocationScreenEvent.ShowScreenUnlockedSnackbar
            _events.emit(event)
        }
    }

    fun onShareClick() {
        viewModelScope.launch {
            val coordinates = coordinatesStateFlow.value.coordinates
            val event = if (coordinates.isBlank())
                LocationScreenEvent.ShowCoordinatesShareErrorSnackbar
            else
                LocationScreenEvent.ShowCoordinatesShareSheet(coordinates)
            _events.emit(event)
        }
    }

    private fun checkPermissions(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(app, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getAccuracy(location: Location?, units: Units?): String {
        return if (location == null || units == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getCoordinatesAccuracy(location, units)
        }
    }

    private fun getBearing(location: Location?): String {
        return if (location == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getBearing(location) ?: app.getString(R.string.common_dash)
        }
    }

    // The return type is optional here because only Android 8+ devices have accuracies. If we're on
    // a device running lower than 8, we don't show *anything* for this field, not even a dash (that
    // would indicate the device is capable of showing the accuracy but doesn't have one).
    private fun getBearingAccuracy(location: Location?): String? {
        return if (location == null)
            app.getString(R.string.common_dash)
        else
            locationFormatter.getBearingAccuracy(location)
    }

    private fun getElevation(location: Location?, units: Units?): String {
        return if (location == null || units == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getElevation(location, units) ?: app.getString(R.string.common_dash)
        }
    }

    // The return type is optional here because only Android 8+ devices have accuracies. If we're on
    // a device running lower than 8, we don't show *anything* for this field, not even a dash (that
    // would indicate the device is capable of showing the accuracy but doesn't have one).
    private fun getElevationAccuracy(location: Location?, units: Units?): String? {
        return if (location == null || units == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getElevationAccuracy(location, units)
        }
    }

    private fun getSpeed(location: Location?, units: Units?): String {
        return if (location == null || units == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getSpeed(location, units) ?: app.getString(R.string.common_dash)
        }
    }

    // The return type is optional here because only Android 8+ devices have accuracies. If we're on
    // a device running lower than 8, we don't show *anything* for this field, not even a dash (that
    // would indicate the device is capable of showing the accuracy but doesn't have one).
    private fun getSpeedAccuracy(location: Location?, units: Units?): String? {
        return if (location == null || units == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getSpeedAccuracy(location, units)
        }
    }

    private fun getUpdatedAt(location: Location?): String {
        return if (location == null) {
            app.getString(R.string.common_dash)
        } else {
            locationFormatter.getTimestamp(location)
                ?.let { app.getString(R.string.location_updated_at, it) }
                ?: app.getString(R.string.common_dash)
        }
    }

    private fun Location.toCoordinatesState(format: CoordinatesFormat): CoordinatesState {
        val (coordinates, coordinatesLines) = locationFormatter.getCoordinates(this, format)
        return CoordinatesState(coordinates, coordinatesLines)
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
        private val PERMISSIONS = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}