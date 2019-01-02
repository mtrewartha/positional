package io.trewartha.positional.ui.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.trewartha.positional.R
import io.trewartha.positional.location.CoordinatesFormat
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.ui.MainViewModel
import io.trewartha.positional.ui.location.coordinates.CoordinatesFragment
import io.trewartha.positional.ui.location.coordinates.CoordinatesFragmentPagerAdapter
import kotlinx.android.synthetic.main.location_fragment.*
import timber.log.Timber
import java.util.*

class LocationFragment : Fragment() {

    private lateinit var coordinatesFormat: CoordinatesFormat
    private lateinit var locationFormatter: LocationFormatter
    private lateinit var preferenceChangeListener: PreferenceChangeListener
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesUnitsKey: String
    private lateinit var sharedPreferencesUnitsImperial: String
    private lateinit var sharedPreferencesUnitsMetric: String
    private lateinit var viewModel: MainViewModel

    private var coordinatesFragments: MutableList<CoordinatesFragment> = LinkedList()
    private var location: Location? = null
    private var screenLock: Boolean = false
    private var useMetricUnits: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        locationFormatter = LocationFormatter(context)
        preferenceChangeListener = PreferenceChangeListener()
        sharedPreferences = context.getSharedPreferences(
            getString(R.string.settings_filename),
            Context.MODE_PRIVATE
        )
        sharedPreferencesUnitsKey = context.getString(R.string.settings_units_key)
        sharedPreferencesUnitsImperial = context.getString(R.string.settings_units_imperial_value)
        sharedPreferencesUnitsMetric = context.getString(R.string.settings_units_metric_value)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java).apply {
            location.apply {
                updatePriority = LOCATION_UPDATE_PRIORITY
                updateInterval = LOCATION_UPDATE_INTERVAL
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.location_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        copyButton.setOnClickListener(::onCopyClick)
        screenLockButton.setOnClickListener(::onScreenLockClick)
        shareButton.setOnClickListener(::onShareClick)

        useMetricUnits = try {
            sharedPreferences.getString(
                sharedPreferencesUnitsKey,
                sharedPreferencesUnitsMetric
            ) == sharedPreferencesUnitsMetric
        } catch (e: ClassCastException) {
            sharedPreferences.getBoolean(sharedPreferencesUnitsKey, false)
        }
        coordinatesFormat = CoordinatesFormat.valueOf(
            sharedPreferences.getString(
                getString(R.string.settings_coordinates_format_key),
                CoordinatesFormat.DMS.name
            ) ?: CoordinatesFormat.DMS.name
        )
        screenLock =
                sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false)
        screenLockButton.isSelected = screenLock
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

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is CoordinatesFragment) {
            coordinatesFragments.add(childFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        if (haveLocationPermissions()) {
            viewModel.location.observe(this, Observer<Location> { onLocationChanged(it) })
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.location.removeObservers(this)
    }

    override fun onDestroyView() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onDestroyView()
    }

    private fun getCoordinatesFragmentIndex(coordinatesFormat: CoordinatesFormat): Int =
        when (coordinatesFormat) {
            CoordinatesFormat.DECIMAL -> 0
            CoordinatesFormat.DDM -> 1
            CoordinatesFormat.DMS -> 2
            CoordinatesFormat.UTM -> 3
            CoordinatesFormat.MGRS -> 4
        }

    private fun haveLocationPermissions(): Boolean =
        checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    private fun lockScreen(lock: Boolean) {
        activity?.window?.apply {
            if (lock)
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else
                clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onCopyClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val context = context ?: return
        val location = location

        if (location == null) {
            Snackbar.make(
                coordinatorLayout,
                R.string.location_copied_coordinates_failure,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        val bottomSheetDialog = BottomSheetDialog(context).apply {
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            setTitle(R.string.location_copy_coordinates_title)
            setContentView(R.layout.coordinates_copy_fragment)
        }

        val bothTextView = bottomSheetDialog.findViewById<View>(R.id.coordinatesCopyBothTextView)
        val latitudeTextView = bottomSheetDialog
            .findViewById<View>(R.id.coordinatesCopyLatitudeTextView)
        val longitudeTextView = bottomSheetDialog
            .findViewById<View>(R.id.coordinatesCopyLongitudeTextView)

        if (bothTextView == null || latitudeTextView == null || longitudeTextView == null) {
            Snackbar.make(
                coordinatorLayout,
                R.string.location_copied_coordinates_failure,
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        CoordinatesCopier(location) { bottomSheetDialog.dismiss() }.let {
            bothTextView.setOnClickListener(it)
            latitudeTextView.setOnClickListener(it)
            longitudeTextView.setOnClickListener(it)
        }

        bottomSheetDialog.show()
    }

    private fun onLocationChanged(location: Location?) {
        this.location = location
        if (location != null) updateLocationViews(location)
    }

    private fun onScreenLockClick(@Suppress("UNUSED_PARAMETER") view: View) {
        screenLock = !screenLock
        screenLockButton.setIconResource(
            if (screenLock)
                R.drawable.ic_twotone_smartphone_24px
            else
                R.drawable.ic_twotone_screen_lock_portrait_24px
        )
        lockScreen(screenLock)
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock)
        val textRes = if (screenLock)
            R.string.location_screen_lock_snackbar_on
        else
            R.string.location_screen_lock_snackbar_off
        Snackbar.make(coordinatorLayout, textRes, Snackbar.LENGTH_LONG).show()
    }

    private fun onShareClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val safeLocation = location
        if (safeLocation == null) {
            Snackbar.make(
                coordinatorLayout,
                R.string.location_share_snackbar_failure,
                Snackbar.LENGTH_LONG
            ).show()
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
        bearingValueTextView.text = locationFormatter.getBearing(location)
        elevationValueTextView.text = locationFormatter.getElevation(location, useMetricUnits)
        speedValueTextView.text = locationFormatter.getSpeed(location, useMetricUnits)
        timestampValueTextView.text = locationFormatter.getTimestamp(location)
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

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            // Don't do anything here
        }

        override fun onPageSelected(position: Int) {
            coordinatesFormat = when (position) {
                0 -> CoordinatesFormat.DECIMAL
                1 -> CoordinatesFormat.DDM
                2 -> CoordinatesFormat.DMS
                3 -> CoordinatesFormat.UTM
                4 -> CoordinatesFormat.MGRS
                else -> CoordinatesFormat.DECIMAL
            }
            setStringPreference(
                getString(R.string.settings_coordinates_format_key),
                coordinatesFormat.name
            )
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
            val clipboardManager = requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipDataLabel = requireContext()
                .getString(R.string.location_copied_coordinates_label)
            var clipDataText = ""
            var snackbarText = ""

            when (v.id) {
                R.id.coordinatesCopyBothTextView -> {
                    clipDataText = String.format(
                        Locale.US,
                        "%f, %f",
                        location.latitude,
                        location.longitude
                    )
                    snackbarText = requireContext()
                        .getString(R.string.location_copied_coordinates_both_success)
                }
                R.id.coordinatesCopyLatitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.latitude)
                    snackbarText = requireContext()
                        .getString(R.string.location_copied_coordinates_latitude_success)
                }
                R.id.coordinatesCopyLongitudeTextView -> {
                    clipDataText = String.format(Locale.US, "%f", location.longitude)
                    snackbarText = requireContext()
                        .getString(R.string.location_copied_coordinates_longitude_success)
                }
            }

            clipboardManager.primaryClip = ClipData.newPlainText(clipDataLabel, clipDataText)

            Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG).show()

            onCoordinatesCopied()
        }
    }

    private inner class PreferenceChangeListener :
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                sharedPreferencesUnitsKey -> {
                    useMetricUnits = sharedPreferences.getString(
                        sharedPreferencesUnitsKey,
                        sharedPreferencesUnitsMetric
                    ) == sharedPreferencesUnitsMetric
                    updateLocationViews(location)
                }
            }
        }
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 3_000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}
