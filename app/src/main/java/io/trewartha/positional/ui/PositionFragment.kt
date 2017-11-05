package io.trewartha.positional.ui

import android.content.*
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import com.google.android.gms.location.LocationRequest
import io.trewartha.positional.location.CoordinatesFormat
import io.trewartha.positional.Log
import io.trewartha.positional.R
import io.trewartha.positional.location.LocationFormatter
import kotlinx.android.synthetic.main.position_fragment.*
import java.util.*


class PositionFragment : LocationAwareFragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        private val TAG = "PositionFragment"
        private val LOCATION_UPDATE_INTERVAL = 5000L // ms
        private val LOCATION_UPDATE_MAX_WAIT_TIME = 10000L // ms
        private val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var coordinatesFragments: MutableList<CoordinatesFragment> = LinkedList()

    private lateinit var coordinatesFormat: CoordinatesFormat
    private lateinit var locationFormatter: LocationFormatter
    private lateinit var sharedPreferences: SharedPreferences

    private var location: Location? = null
    private var screenLock: Boolean = false
    private var useMetricUnits: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationFormatter = LocationFormatter(context)
        sharedPreferences = context.getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.position_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        copyButton.setOnClickListener { onCopyClicked() }
        shareButton.setOnClickListener { onShareClicked() }

        val unitOnClickListener = View.OnClickListener { onDistanceUnitClicked() }
        elevationUnitTextView.setOnClickListener(unitOnClickListener)
        speedUnitTextView.setOnClickListener(unitOnClickListener)
        accuracyUnitTextView.setOnClickListener(unitOnClickListener)

        screenLockSwitch.setOnClickListener { onScreenLockClicked() }

        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false)
        coordinatesFormat = CoordinatesFormat.valueOf(
                sharedPreferences.getString(getString(R.string.settings_coordinates_format_key), CoordinatesFormat.DMS.name)
        )
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false)
        screenLockSwitch.isSelected = screenLock

        val coordinatesPagerAdapter = CoordinatesFragmentPagerAdapter(
                childFragmentManager
        )
        coordinatesViewPager.adapter = coordinatesPagerAdapter
        coordinatesViewPager.offscreenPageLimit = coordinatesPagerAdapter.count
        coordinatesViewPager.setCurrentItem(getCoordinatesFragmentIndex(coordinatesFormat), true)
        coordinatesViewPager.addOnPageChangeListener(CoordinatesPageChangeListener())

        updateLocationViews(null)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        if (fragment is CoordinatesFragment) {
            coordinatesFragments.add(fragment)
        }
    }

    override fun onLocationChanged(location: Location?) {
        this.location = location
        if (location == null) {
            progressBar.visibility = View.VISIBLE
        } else {
            if (progressBar.visibility == View.VISIBLE) {
                progressBar.visibility = View.INVISIBLE
            }
            updateLocationViews(location)
        }
    }

    override fun getLocationUpdateInterval(): Long {
        return LOCATION_UPDATE_INTERVAL
    }

    override fun getLocationUpdateMaxWaitTime(): Long {
        return LOCATION_UPDATE_MAX_WAIT_TIME
    }

    override fun getLocationUpdatePriority(): Int {
        return LOCATION_UPDATE_PRIORITY
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, checked: Boolean) {
        if (compoundButton.id == R.id.screenLockSwitch) {
            setBooleanPreference(getString(R.string.settings_screen_lock_key), checked)
            lockScreen(checked)
        }
    }

    private fun getCoordinatesFragmentIndex(coordinatesFormat: CoordinatesFormat): Int {
        return when (coordinatesFormat) {
            CoordinatesFormat.DECIMAL -> 0
            CoordinatesFormat.DMS -> 1
            CoordinatesFormat.UTM -> 2
            CoordinatesFormat.MGRS -> 3
        }
    }

    private fun lockScreen(lock: Boolean) {
        if (lock) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onCopyClicked() {
        val safeLocation = location
        if (safeLocation == null) {
            Toast.makeText(context, R.string.copied_coordinates_failure, Toast.LENGTH_SHORT).show()
            return
        }

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setTitle(R.string.settings_copy_coordinates_title)
        bottomSheetDialog.setContentView(R.layout.coordinates_copy_fragment)

        val coordinatesCopier = CoordinatesCopier(context, safeLocation, object : OnCoordinatesCopiedListener {
            override fun onCopy() {
                bottomSheetDialog.dismiss()
            }
        })

        val bothTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyBothTextView)
        val latitudeTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyLatitudeTextView)
        val longitudeTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyLongitudeTextView)

        if (bothTextView == null || latitudeTextView == null || longitudeTextView == null) {
            Toast.makeText(context, R.string.copied_coordinates_failure, Toast.LENGTH_SHORT).show()
            return
        }

        bothTextView.setOnClickListener(coordinatesCopier)
        latitudeTextView.setOnClickListener(coordinatesCopier)
        longitudeTextView.setOnClickListener(coordinatesCopier)

        bottomSheetDialog.show()
    }

    private fun onDistanceUnitClicked() {
        useMetricUnits = !useMetricUnits
        updateLocationViews(location)
        setBooleanPreference(getString(R.string.settings_metric_units_key), useMetricUnits)
    }

    private fun onScreenLockClicked() {
        screenLock = !screenLock
        screenLockSwitch.isSelected = screenLock
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock)
        val textRes = if (screenLock) R.string.screen_lock_on else R.string.screen_lock_off
        Toast.makeText(context, textRes, Toast.LENGTH_SHORT).show()
    }

    private fun onShareClicked() {
        val safeLocation = location
        if (safeLocation == null) {
            Toast.makeText(context, R.string.shared_coordinates_failure, Toast.LENGTH_SHORT).show()
            return
        }

        val coordinatesText = locationFormatter.getCoordinates(
                safeLocation.latitude,
                safeLocation.longitude,
                coordinatesFormat
        )

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, coordinatesText)
        sendIntent.type = "text/plain"
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
        Log.info(TAG, "Saving $key preference as $value")
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun setStringPreference(key: String, value: String?) {
        Log.info(TAG, "Saving $key preference as $value")
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

    private class CoordinatesCopier(val context: Context, val location: Location, val onCoordinatesCopiedListener: OnCoordinatesCopiedListener?) : View.OnClickListener {

        override fun onClick(v: View) {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipDataLabel = context.getString(R.string.copied_coordinates_label)
            var clipDataText = ""
            var toastText = ""

            when (v.id) {
                R.id.coordinatesCopyBothTextView -> {
                    clipDataText = String.format(Locale.US, "%f, %f", location.latitude, location.longitude)
                    toastText = context.getString(R.string.copied_coordinates_both_success)
                }
                R.id.coordinatesCopyLatitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.latitude)
                    toastText = context.getString(R.string.copied_coordinates_latitude_success)
                }
                R.id.coordinatesCopyLongitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.longitude)
                    toastText = context.getString(R.string.copied_coordinates_longitude_success)
                }
            }

            clipboardManager.primaryClip = ClipData.newPlainText(clipDataLabel, clipDataText)

            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

            onCoordinatesCopiedListener?.onCopy()
        }
    }

    private interface OnCoordinatesCopiedListener {
        fun onCopy()
    }
}
