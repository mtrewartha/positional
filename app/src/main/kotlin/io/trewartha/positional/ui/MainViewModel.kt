package io.trewartha.positional.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.location.LocationRequest
import io.trewartha.positional.R
import io.trewartha.positional.common.SharedPrefsBooleanLiveData
import io.trewartha.positional.common.SharedPrefsStringLiveData
import io.trewartha.positional.compass.CompassLiveData
import io.trewartha.positional.compass.CompassViewData
import io.trewartha.positional.location.CoordinatesFormat
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.sun.SunLiveData
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val compassLiveData: LiveData<CompassViewData> by lazy { CompassLiveData(application) }

    val locationLiveData: LiveData<LocationViewData> by lazy { _locationLiveData }

    val screenLockLiveData: LiveData<Boolean> by lazy {
        SharedPrefsBooleanLiveData(prefs, prefsScreenLockKey, false)
    }

    val deviceHasAccelerometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    val deviceHasMagnetometer by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
    }

    val sunLiveData by lazy { SunLiveData(application) }

    private val _formatLiveData: LiveData<CoordinatesFormat> by lazy {
        Transformations.map(SharedPrefsStringLiveData(
                prefs, prefsCoordsFormatKey, ""
        )) {
            try {
                CoordinatesFormat.valueOf(it.toUpperCase(Locale.US))
            } catch (exception: Exception) {
                CoordinatesFormat.DD
            }
        }
    }

    private val _locationLiveData by lazy {
        MediatorLiveData<LocationViewData>()
                .apply {
                    addSource(_rawLocationLiveData) {
                        value = it.toViewData(
                                _formatLiveData.value ?: return@addSource,
                                _metricLiveData.value ?: return@addSource
                        )
                    }
                    addSource(_formatLiveData) {
                        value = _rawLocationLiveData.value?.toViewData(
                                it,
                                _metricLiveData.value ?: return@addSource
                        ) ?: return@addSource
                    }
                    addSource(_metricLiveData) {
                        value = _rawLocationLiveData.value?.toViewData(
                                _formatLiveData.value ?: return@addSource,
                                it
                        ) ?: return@addSource
                    }
                }
    }

    private val _metricLiveData: LiveData<Boolean> by lazy {
        Transformations.map(SharedPrefsStringLiveData(
                prefs, prefsUseMetricUnitsKey, ""
        )) {
            try {
                it == prefsUseMetricUnitsValue
            } catch (exception: Exception) {
                false
            }
        }
    }

    private val _rawLocationLiveData by lazy {
        LocationLiveData(application).apply {
            updateInterval = 3_000L
            updatePriority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationFormatter = LocationFormatter(application)

    private val prefsCoordsFormatKey = application
            .getString(R.string.settings_coordinates_format_key)

    private val prefsScreenLockKey = application
            .getString(R.string.settings_screen_lock_key)

    private val prefsUseMetricUnitsKey = application
            .getString(R.string.settings_units_key)

    private val prefsUseMetricUnitsValue = application
            .getString(R.string.settings_units_metric_value)

    private val sensorManager by lazy {
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val prefs: SharedPreferences = application
            .getSharedPreferences(
                    application.getString(R.string.settings_filename),
                    Context.MODE_PRIVATE
            ).apply { registerOnSharedPreferenceChangeListener(prefsListener) }

    private val prefsListener = OnSharedPreferenceChangeListener { _, _ ->
        _locationLiveData.value = null
    }

    private val prefsScreenLock: Boolean
        get() {
            return prefs.getBoolean(prefsScreenLockKey, false)
        }

    private val prefsUseMetric: Boolean
        get() {
            return try {
                prefs.getString(prefsUseMetricUnitsKey, prefsUseMetricUnitsValue) ==
                        prefsUseMetricUnitsValue
            } catch (e: ClassCastException) {
                prefs.getBoolean(prefsUseMetricUnitsKey, false)
            }
        }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    fun toggleScreenLock() {
        prefs.edit().putBoolean(prefsScreenLockKey, !prefsScreenLock).apply()
    }

    private fun Location.toViewData(
            format: CoordinatesFormat?,
            metric: Boolean?
    ): LocationViewData {
        if (format == null || metric == null) return LocationViewData(
                null, null, null, 1, null, null, null
        )

        val (coordinates, coordinatesLines) = locationFormatter.getCoordinates(this, format)
        return LocationViewData(
                accuracy = locationFormatter.getAccuracy(this, prefsUseMetric),
                bearing = locationFormatter.getBearing(this),
                coordinates = coordinates,
                coordinatesLines = coordinatesLines,
                elevation = locationFormatter.getElevation(this, metric),
                speed = locationFormatter.getSpeed(this, metric),
                updatedAt = locationFormatter.getTimestamp(this)
        )
    }

    data class LocationViewData(
            val accuracy: String?,
            val bearing: String?,
            val coordinates: String?,
            val coordinatesLines: Int,
            val elevation: String?,
            val speed: String?,
            val updatedAt: String?
    )
}