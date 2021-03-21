package io.trewartha.positional.ui.location

import android.Manifest
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dagger.hilt.android.lifecycle.HiltViewModel
import io.trewartha.positional.PositionalApplication
import io.trewartha.positional.R
import io.trewartha.positional.domain.entities.CoordinatesFormat
import io.trewartha.positional.domain.entities.Units
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.ui.ViewModelEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val app: Application,
    private val clipboardManager: ClipboardManager,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationFormatter: LocationFormatter,
    private val prefs: SharedPreferences
) : AndroidViewModel(app) {

    val accuracyVisibility: LiveData<Int> by lazy {
        callbackFlow {
            if (prefs.contains(prefsKeyShowAccuracies))
                offer(prefs.getBoolean(prefsKeyShowAccuracies, DEFAULT_SHOW_ACCURACIES))
            prefShowAccuraciesListener = PrefShowAccuraciesListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefShowAccuraciesListener)
            awaitClose {
                prefShowAccuraciesListener
                    ?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map {
            if (it) View.VISIBLE else View.GONE
        }.asLiveData()
    }

    val bearing: LiveData<String> by lazy {
        location.mapNotNull { it }
            .map { locationFormatter.getBearing(it) ?: app.getString(R.string.common_dash) }
            .asLiveData()
    }

    val bearingAccuracy: LiveData<String?> by lazy {
        location.mapNotNull { it }
            .map { locationFormatter.getBearingAccuracy(it) }
            .asLiveData()
    }

    val coordinates: LiveData<Coordinates> by lazy {
        _coordinates.mapNotNull { it }.asLiveData()
    }

    val coordinatesAccuracy: LiveData<String> by lazy {
        combine(location.mapNotNull { it }, units) { location, units ->
            locationFormatter.getCoordinatesAccuracy(location, units)
        }.asLiveData()
    }

    val elevation: LiveData<String> by lazy {
        combine(location.mapNotNull { it }, units) { location, units ->
            locationFormatter.getElevation(location, units) ?: app.getString(R.string.common_dash)
        }.asLiveData()
    }

    val elevationAccuracy: LiveData<String?> by lazy {
        combine(location.mapNotNull { it }, units) { location, units ->
            locationFormatter.getElevationAccuracy(location, units)
        }.asLiveData()
    }

    val events: LiveData<Event>
        get() = _events

    val screenLockState: LiveData<ScreenLockState> by lazy {
        callbackFlow {
            if (prefs.contains(prefsKeyScreenLock))
                offer(prefs.getBoolean(prefsKeyScreenLock, DEFAULT_SCREEN_LOCK))
            prefScreenLockListener = PrefScreenLockListener(this)
            prefs.registerOnSharedPreferenceChangeListener(prefScreenLockListener)
            awaitClose {
                prefScreenLockListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
            }
        }.map {
            val icon = ContextCompat.getDrawable(
                app,
                if (it) R.drawable.ic_twotone_smartphone_24px
                else R.drawable.ic_twotone_screen_lock_portrait_24px
            )!!
            val contentDescription = app.getString(
                if (it) R.string.location_screen_lock_button_content_description_on
                else R.string.location_screen_lock_button_content_description_off
            )
            val tooltip = app.getString(
                if (it) R.string.location_screen_lock_button_tooltip_on
                else R.string.location_screen_lock_button_tooltip_off
            )
            ScreenLockState(it, icon, contentDescription, tooltip)
        }.asLiveData()
    }

    val speed: LiveData<String> by lazy {
        combine(location.mapNotNull { it }, units) { location, units ->
            locationFormatter.getSpeed(location, units) ?: app.getString(R.string.common_dash)
        }.asLiveData()
    }

    val speedAccuracy: LiveData<String?> by lazy {
        combine(location.mapNotNull { it }, units) { location, units ->
            locationFormatter.getSpeedAccuracy(location, units)
        }.asLiveData()
    }

    val updatedAt: LiveData<String> by lazy {
        location.mapNotNull { it }
            .map { locationFormatter.getTimestamp(it) ?: app.getString(R.string.common_dash) }
            .asLiveData()
    }

    private val _coordinates: StateFlow<Coordinates?> by lazy {
        combine(
            location.mapNotNull { it },
            coordinatesFormat.mapNotNull { it }
        ) { location, format ->
            location.toCoordinates(format)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }
    private val _events = MutableLiveData<Event>()
    private val coordinatesFormat: StateFlow<CoordinatesFormat?> = callbackFlow {
        if (prefs.contains(prefsKeyCoordinatesFormat))
            offer(prefs.getString(prefsKeyCoordinatesFormat, null))
        prefCoordinatesFormatListener = PrefCoordinatesFormatListener(this)
        prefs.registerOnSharedPreferenceChangeListener(prefCoordinatesFormatListener)
        awaitClose {
            prefCoordinatesFormatListener?.let {
                prefs.unregisterOnSharedPreferenceChangeListener(it)
            }
        }
    }.map {
        CoordinatesFormat.valueOf(it!!.toUpperCase(Locale.US))
    }.catch {
        emit(DEFAULT_COORDINATES_FORMAT)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val location: StateFlow<Location?> by lazy {
        havePermissions.filter { it }.flatMapLatest {
            callbackFlow<Location> {
                var firstLocationUpdateTrace: Trace? =
                    FirebasePerformance.getInstance().newTrace("first_location")
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        val location = locationResult?.lastLocation ?: return
                        offer(location)

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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }

    private val havePermissions: MutableStateFlow<Boolean> = MutableStateFlow(checkPermissions())

    private val units: SharedFlow<Units> = callbackFlow {
        if (prefs.contains(prefsKeyUnits))
            offer(prefs.getString(prefsKeyUnits, null))
        prefUnitsListener = PrefUnitsListener(this)
        prefs.registerOnSharedPreferenceChangeListener(prefUnitsListener)
        awaitClose {
            prefUnitsListener?.let { prefs.unregisterOnSharedPreferenceChangeListener(it) }
        }
    }.map {
        Units.valueOf(it!!.toUpperCase(Locale.US))
    }.catch {
        emit(DEFAULT_UNITS)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    private val prefsKeyCoordinatesFormat = app.getString(R.string.settings_coordinates_format_key)
    private val prefsKeyScreenLock = app.getString(R.string.settings_screen_lock_key)
    private val prefsKeyShowAccuracies = app.getString(R.string.settings_show_accuracies_key)
    private val prefsKeyUnits = app.getString(R.string.settings_units_key)
    private var prefCoordinatesFormatListener: PrefCoordinatesFormatListener? = null
    private var prefScreenLockListener: PrefScreenLockListener? = null
    private var prefShowAccuraciesListener: PrefShowAccuraciesListener? = null
    private var prefUnitsListener: PrefUnitsListener? = null

    init {
        if (!checkPermissions()) _events.value = Event.RequestPermissions(PERMISSIONS)
    }

    fun handleViewEvent(event: LocationFragment.Event) {
        when (event) {
            is LocationFragment.Event.CopyClick -> handleCopyClick()
            is LocationFragment.Event.HelpClick -> handleHelpClick()
            is LocationFragment.Event.PermissionsResult -> handlePermissionsResult(event)
            is LocationFragment.Event.ScreenLockClick -> handleScreenLockClick()
            is LocationFragment.Event.SystemSettingsResult -> handleSystemSettingsResult()
            is LocationFragment.Event.ShareClick -> handleShareClick()
        }
    }

    private fun checkPermissions(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(app, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun handleCopyClick() {
        val location = location.value
        val format = coordinatesFormat.value
        _events.value = if (location == null || format == null) {
            Event.CoordinatesCopy.Error()
        } else {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                    getApplication<PositionalApplication>()
                        .getString(R.string.location_copied_coordinates_label),
                    locationFormatter.getSharedCoordinates(location, format)
                )
            )
            Event.CoordinatesCopy.Success()
        }
    }

    private fun handleHelpClick() {
        _events.value = Event.NavigateToLocationHelp()
    }

    private fun handlePermissionsResult(event: LocationFragment.Event.PermissionsResult) {
        val allPermissionsGranted = event.result.all { it.value }
        havePermissions.tryEmit(allPermissionsGranted)
        if (!allPermissionsGranted) _events.value = Event.ShowPermissionsDeniedDialog()
    }

    private fun handleScreenLockClick() {
        val locked = !prefs.getBoolean(prefsKeyScreenLock, false)
        prefs.edit { putBoolean(prefsKeyScreenLock, locked) }
        _events.value = Event.ScreenLock(locked)
    }

    private fun handleShareClick() {
        val location = location.value
        val format = coordinatesFormat.value
        _events.value = if (location == null || format == null)
            Event.CoordinatesShare.Error()
        else
            Event.CoordinatesShare.Success(locationFormatter.getSharedCoordinates(location, format))
    }

    private fun handleSystemSettingsResult() {
        val havePermissionsNow = checkPermissions()
        havePermissions.tryEmit(havePermissionsNow)
        if (!havePermissionsNow) _events.value = Event.ShowPermissionsDeniedDialog()
    }

    private fun Location.toCoordinates(format: CoordinatesFormat): Coordinates {
        val (coordinates, coordinatesLines) = locationFormatter.getCoordinates(this, format)
        return Coordinates(coordinatesLines, coordinates)
    }

    data class Coordinates(val maxLines: Int, val text: String)

    data class ScreenLockState(
        val locked: Boolean,
        val icon: Drawable,
        val contentDescription: String,
        val tooltip: String
    )

    sealed class Event : ViewModelEvent() {

        sealed class CoordinatesCopy : Event() {
            class Error : CoordinatesCopy()
            class Success : CoordinatesCopy()
        }

        sealed class CoordinatesShare : Event() {
            class Error : CoordinatesShare()
            data class Success(val coordinates: String) : CoordinatesShare()
        }

        class NavigateToLocationHelp : Event()

        class ShowPermissionsDeniedDialog : Event()

        data class RequestPermissions(val permissions: List<String>) : Event()

        data class ScreenLock(val locked: Boolean) : Event()
    }

    private inner class PrefCoordinatesFormatListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyCoordinatesFormat)
                producerScope.offer(sharedPrefs.getString(key, null))
        }
    }

    private inner class PrefScreenLockListener(
        val producerScope: ProducerScope<Boolean>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyScreenLock)
                producerScope.offer(sharedPrefs.getBoolean(key, DEFAULT_SCREEN_LOCK))
        }
    }

    private inner class PrefShowAccuraciesListener(
        val producerScope: ProducerScope<Boolean>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyShowAccuracies)
                producerScope.offer(sharedPrefs.getBoolean(key, DEFAULT_SHOW_ACCURACIES))
        }
    }

    private inner class PrefUnitsListener(
        val producerScope: ProducerScope<String?>
    ) : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
            if (key == prefsKeyUnits)
                producerScope.offer(sharedPrefs.getString(key, null))
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