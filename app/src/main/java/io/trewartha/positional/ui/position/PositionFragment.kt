package io.trewartha.positional.ui.position

import android.content.*
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.trewartha.positional.R
import io.trewartha.positional.location.CoordinatesFormat
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.ui.LocationAwareFragment
import kotlinx.android.synthetic.main.position_fragment.*
import timber.log.Timber
import java.util.*


class PositionFragment : LocationAwareFragment() {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L // ms
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 10000L // ms
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var coordinatesFragments: MutableList<CoordinatesFragment> = LinkedList()

    private lateinit var coordinatesFormat: CoordinatesFormat
    private lateinit var locationFormatter: LocationFormatter
    private lateinit var preferenceChangeListener: PreferenceChangeListener
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesUnitsKey: String
    private lateinit var sharedPreferencesUnitsImperial: String
    private lateinit var sharedPreferencesUnitsMetric: String

    private var location: Location? = null
    private var screenLock: Boolean = false
    private var useMetricUnits: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationFormatter = LocationFormatter(context)
        preferenceChangeListener = PreferenceChangeListener()
        sharedPreferences = context.getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE)
        sharedPreferencesUnitsKey = context.getString(R.string.settings_units_key)
        sharedPreferencesUnitsImperial = context.getString(R.string.settings_units_imperial_value)
        sharedPreferencesUnitsMetric = context.getString(R.string.settings_units_metric_value)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.position_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        copyButton.setOnClickListener { onCopyClicked() }
        shareButton.setOnClickListener { onShareClicked() }
        invertColorsButton.setOnClickListener { onInvertColorsClicked() }

        val unitOnClickListener = View.OnClickListener { onDistanceUnitClicked() }
        elevationUnitTextView.setOnClickListener(unitOnClickListener)
        speedUnitTextView.setOnClickListener(unitOnClickListener)
        accuracyUnitTextView.setOnClickListener(unitOnClickListener)

        screenLockSwitch.setOnClickListener { onScreenLockClicked() }

        useMetricUnits = try {
            sharedPreferences.getString(sharedPreferencesUnitsKey, sharedPreferencesUnitsMetric) == sharedPreferencesUnitsMetric
        } catch (e: ClassCastException) {
            sharedPreferences.getBoolean(sharedPreferencesUnitsKey, false)
        }
        coordinatesFormat = CoordinatesFormat.valueOf(
                sharedPreferences.getString(
                        getString(R.string.settings_coordinates_format_key),
                        CoordinatesFormat.DMS.name
                ) ?: CoordinatesFormat.DMS.name
        )
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false)
        screenLockSwitch.isSelected = screenLock
        lockScreen(screenLock)

        coordinatesViewPager.apply {
            val coordinatesPagerAdapter = CoordinatesFragmentPagerAdapter(childFragmentManager)
            adapter = coordinatesPagerAdapter
            offscreenPageLimit = coordinatesPagerAdapter.count
            setCurrentItem(getCoordinatesFragmentIndex(coordinatesFormat), true)
            addOnPageChangeListener(CoordinatesPageChangeListener())
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        if (fragment is CoordinatesFragment) {
            coordinatesFragments.add(fragment)
        }
    }

    override fun onDestroyView() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onDestroyView()
    }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (location == null) {
            progressBar.animate().alpha(1f).start()
        } else {
            if (progressBar.alpha != 0f) {
                progressBar.animate().alpha(0f)
            }
            updateLocationViews(location)
        }
    }

    override fun getLocationUpdateInterval(): Long = LOCATION_UPDATE_INTERVAL

    override fun getLocationUpdateMaxWaitTime(): Long = LOCATION_UPDATE_MAX_WAIT_TIME

    override fun getLocationUpdatePriority(): Int = LOCATION_UPDATE_PRIORITY

    private fun getCoordinatesFragmentIndex(coordinatesFormat: CoordinatesFormat): Int {
        return when (coordinatesFormat) {
            CoordinatesFormat.DECIMAL -> 0
            CoordinatesFormat.DMS -> 1
            CoordinatesFormat.UTM -> 2
            CoordinatesFormat.MGRS -> 3
        }
    }

    private fun lockScreen(lock: Boolean) {
        activity?.window?.apply {
            if (lock)
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else
                clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onCopyClicked() {
        val context = context ?: return
        val location = location

        if (location == null) {
            Snackbar.make(coordinatorLayout, R.string.copied_coordinates_failure, Snackbar.LENGTH_LONG).show()
            return
        }

        val bottomSheetDialog = BottomSheetDialog(context).apply {
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            setTitle(R.string.copy_coordinates_title)
            setContentView(R.layout.coordinates_copy_fragment)
        }

        val coordinatesCopier = CoordinatesCopier(location) { bottomSheetDialog.dismiss() }

        val bothTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyBothTextView)
        val latitudeTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyLatitudeTextView)
        val longitudeTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyLongitudeTextView)

        if (bothTextView == null || latitudeTextView == null || longitudeTextView == null) {
            Snackbar.make(coordinatorLayout, R.string.copied_coordinates_failure, Snackbar.LENGTH_LONG).show()
            return
        }

        bothTextView.setOnClickListener(coordinatesCopier)
        latitudeTextView.setOnClickListener(coordinatesCopier)
        longitudeTextView.setOnClickListener(coordinatesCopier)

        bottomSheetDialog.show()
    }

    private fun onDistanceUnitClicked() {
        Snackbar.make(coordinatorLayout, R.string.unit_change_snackbar, Snackbar.LENGTH_LONG).show()
    }

    private fun onInvertColorsClicked() {
        // TODO: Implement overriding the theme
    }

    private fun onScreenLockClicked() {
        screenLock = !screenLock
        screenLockSwitch.isSelected = screenLock
        lockScreen(screenLock)
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock)
        val textRes = if (screenLock) R.string.screen_lock_on else R.string.screen_lock_off
        Snackbar.make(coordinatorLayout, textRes, Snackbar.LENGTH_LONG).show()
    }

    private fun onShareClicked() {
        val safeLocation = location
        if (safeLocation == null) {
            Snackbar.make(coordinatorLayout, R.string.share_failure, Snackbar.LENGTH_LONG).show()
            return
        }

        val coordinatesText = locationFormatter.getCoordinates(
                safeLocation.latitude,
                safeLocation.longitude,
                coordinatesFormat
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, coordinatesText)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    private fun updateLocationViews(location: Location?) {
        updateCoordinatesFragments(location)
        accuracyValueTextView.text = locationFormatter.getAccuracy(location, useMetricUnits)
        accuracyUnitTextView.text = locationFormatter.getDistanceUnit(useMetricUnits)
        bearingValueTextView.text = locationFormatter.getBearing(location)
        elevationValueTextView.text = locationFormatter.getElevation(location, useMetricUnits)
        elevationUnitTextView.text = locationFormatter.getDistanceUnit(useMetricUnits)
        speedValueTextView.text = locationFormatter.getSpeed(location, useMetricUnits)
        speedUnitTextView.text = locationFormatter.getSpeedUnit(useMetricUnits)
    }

    private fun setBooleanPreference(key: String, value: Boolean) {
        Timber.i("Saving $key preference as $value")
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun setStringPreference(key: String, value: String?) {
        Timber.i("Saving $key preference as $value")
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun updateCoordinatesFragments(location: Location?) {
        val latitude: Double
        val longitude: Double
        if (location == null) {
            latitude = 0.0
            longitude = 0.0
        } else {
            latitude = location.latitude
            longitude = location.longitude
        }
        coordinatesFragments.forEach {
            it.setCoordinates(latitude, longitude)
        }
    }

    private inner class CoordinatesPageChangeListener : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // Don't do anything here
        }

        override fun onPageSelected(position: Int) {
            coordinatesFormat = when (position) {
                0 -> CoordinatesFormat.DECIMAL
                1 -> CoordinatesFormat.DMS
                2 -> CoordinatesFormat.UTM
                3 -> CoordinatesFormat.MGRS
                else -> CoordinatesFormat.DECIMAL
            }
            setStringPreference(getString(R.string.settings_coordinates_format_key), coordinatesFormat.name)
        }

        override fun onPageScrollStateChanged(state: Int) {
            // Don't do anything here
        }
    }

    private inner class CoordinatesCopier(
            val location: Location,
            val onCoordinatesCopied: () -> Unit
    ) : View.OnClickListener {

        override fun onClick(v: View) {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipDataLabel = requireContext().getString(R.string.copied_coordinates_label)
            var clipDataText = ""
            var snackbarText = ""

            when (v.id) {
                R.id.coordinatesCopyBothTextView -> {
                    clipDataText = String.format(Locale.US, "%f, %f", location.latitude, location.longitude)
                    snackbarText = requireContext().getString(R.string.copied_coordinates_both_success)
                }
                R.id.coordinatesCopyLatitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.latitude)
                    snackbarText = requireContext().getString(R.string.copied_coordinates_latitude_success)
                }
                R.id.coordinatesCopyLongitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.longitude)
                    snackbarText = requireContext().getString(R.string.copied_coordinates_longitude_success)
                }
            }

            clipboardManager.primaryClip = ClipData.newPlainText(clipDataLabel, clipDataText)

            Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG).show()

            onCoordinatesCopied()
        }
    }

    private inner class PreferenceChangeListener : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                sharedPreferencesUnitsKey -> {
                    useMetricUnits = sharedPreferences.getString(sharedPreferencesUnitsKey, sharedPreferencesUnitsMetric) == sharedPreferencesUnitsMetric
                    updateLocationViews(location)
                }
            }
        }
    }
}
