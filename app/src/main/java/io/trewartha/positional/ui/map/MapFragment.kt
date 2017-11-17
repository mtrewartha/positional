package io.trewartha.positional.ui.map

import android.content.*
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.rubensousa.floatingtoolbar.FloatingToolbar
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnCameraMoveStartedListener.REASON_API_GESTURE
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.location.DistanceUtils.distanceInKilometers
import io.trewartha.positional.location.DistanceUtils.distanceInMiles
import io.trewartha.positional.time.Duration
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint
import io.trewartha.positional.tracks.TrackingListener
import io.trewartha.positional.tracks.TrackingService
import io.trewartha.positional.ui.LocationAwareFragment
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.track_toolbar.*
import org.jetbrains.anko.doAsync
import org.threeten.bp.Instant
import java.io.File
import java.io.FileOutputStream

class MapFragment : LocationAwareFragment() {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 1000L
        private const val MAP_ANIMATION_DURATION_MS = 2000
        private const val MAP_SNAPSHOT_JPEG_QUALITY = 80
        private const val MAP_ZOOM_LEVEL_DEFAULT = 17.0
        private const val TAG = "MapFragment"
    }

    private val trackingListener = MapTrackingListener()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener

    private var followingUserLocation = true
    private var map: MapboxMap? = null
    private var trackingService: TrackingService? = null
    private var trackingServiceConnection: TrackingServiceConnection? = null
    private var useMetricUnits = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Mapbox.getInstance(context, context.getString(R.string.mapbox_key))
        attachToSharedPreferences()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackingServiceConnection = TrackingServiceConnection()

        val intent = Intent(context, TrackingService::class.java)
        context.startService(intent)
        context.bindService(intent, trackingServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return layoutInflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync gotMap@ {
            map = it.apply { addOnCameraMoveStartedListener(OnCameraMoveStartedListener()) }
            zoomToLocation(lastLocation ?: return@gotMap)
        }

        updateTrackDistance(null)
        updateTrackDuration(0, 0, 0)
        trackToolbar.attachFab(recordTrackButton)

        syncTrackToolbarState()
        trackingService?.addListener(trackingListener)

        myLocationButton.setOnClickListener { onMyLocationClick() }
        recordTrackButton.setOnClickListener { onRecordTrackClick() }
        stopTrackButton.setOnClickListener { onStopTrackClick() }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        map = null
        mapView.onDestroy()
        trackToolbar.hide()
        trackingService?.removeListener(trackingListener)

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackingServiceConnection?.let {
            activity.unbindService(it)
        }
        trackingServiceConnection = null
        trackingService = null
    }

    override fun onDetach() {
        super.onDetach()
        detachFromSharedPreferences()
    }

    override fun onLocationChanged(location: Location?) {
        if (location == null) return

        if (followingUserLocation) {
            zoomToLocation(location)
        }
    }

    override fun getLocationUpdateInterval() = LOCATION_UPDATE_INTERVAL

    override fun getLocationUpdateMaxWaitTime() = LOCATION_UPDATE_MAX_WAIT_TIME

    override fun getLocationUpdatePriority() = LOCATION_UPDATE_PRIORITY

    private fun attachToSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(
                getString(R.string.settings_filename),
                Context.MODE_PRIVATE
        )
        onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener(context)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)

        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false)
    }

    private fun detachFromSharedPreferences() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    private fun onMyLocationClick() {
        // Start following the user location again.
        followingUserLocation = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myLocationButton.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.blue)
            )
        }
        Log.info(TAG, "Following the user's location")

        val location = lastLocation
        if (location == null) {
            Toast.makeText(context, R.string.my_location_failure, Toast.LENGTH_SHORT).show()
        } else {
            zoomToLocation(location)
        }
    }

    private fun onRecordTrackClick() {
        if (trackingService?.isTracking() == false) {
            startTracking()
        } else {
            Snackbar.make(
                    coordinatorLayout,
                    R.string.tracking_service_connecting,
                    Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun onStopTrackClick() {
        stopTracking()
    }

    private fun startTracking() {
        map?.snapshot { doAsync { addTrackSnapshot(it) } }
        trackingService?.startTracking()
    }

    private fun addTrackSnapshot(trackSnapshotBitmap: Bitmap) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val snapshotFile = File(context.cacheDir, "track-snapshot-$userId-${Instant.now()}.jpg")
        trackSnapshotBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                MAP_SNAPSHOT_JPEG_QUALITY,
                FileOutputStream(snapshotFile)
        )
        trackingService?.addTrackSnapshot(snapshotFile)
    }

    private fun stopTracking() {
        trackingService?.stopTracking()
    }

    private fun syncTrackToolbarState() {
        trackingService?.let {
            if (it.isTracking() && !trackToolbar.isShowing) {
                trackToolbar.show()
            } else if (!it.isTracking() && trackToolbar.isShowing) {
                trackToolbar.hide()
            }
        }
    }

    private fun updateTrackDistance(track: Track?) {
        val distance = (track?.distance ?: 0f).let {
            if (useMetricUnits) distanceInKilometers(it) else distanceInMiles(it)
        }

        val format = if (useMetricUnits)
            R.string.tracking_toolbar_distance_km
        else
            R.string.tracking_toolbar_distance_mi

        distanceTextView.text = getString(format, distance)
    }

    private fun updateTrackDuration(hours: Int, minutes: Int, seconds: Int) {
        durationTextView?.text = getString(
                R.string.tracking_toolbar_duration,
                hours,
                minutes,
                seconds
        )
    }

    private fun zoomToLocation(location: Location, zoomLevel: Double = MAP_ZOOM_LEVEL_DEFAULT) {
        map?.apply {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder().target(latLng).zoom(zoomLevel).build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
            animateCamera(cameraUpdate, MAP_ANIMATION_DURATION_MS)
        }
    }

    private inner class OnCameraMoveStartedListener : MapboxMap.OnCameraMoveStartedListener {

        override fun onCameraMoveStarted(reason: Int) {
            if (followingUserLocation && reason == REASON_API_GESTURE) {
                // The user probably doesn't want the map snapping to their location every time it
                // gets updated now, so hold off on following it until they tap the "my location"
                // button again.
                followingUserLocation = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    myLocationButton.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.gray2)
                    )
                }
                Log.info(TAG, "Stopped following the user's location")
            }
        }
    }

    private inner class OnSharedPreferenceChangeListener(
            context: Context
    ) : SharedPreferences.OnSharedPreferenceChangeListener {

        val keyMetricUnits: String = context.getString(R.string.settings_metric_units_key)

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                keyMetricUnits -> {
                    useMetricUnits = sharedPreferences.getBoolean(keyMetricUnits, false)
                    updateTrackDistance(trackingListener.track ?: return)
                }
            }
        }
    }

    private inner class MapTrackingListener : TrackingListener {

        var track: Track? = null
            private set

        override fun onTrackingStarted(track: Track) {
            Log.info(TAG, "Tracking started")
            this.track = track
            syncTrackToolbarState()
            updateTrackDistance(track)
        }

        override fun onTrackPointAdded(track: Track, point: TrackPoint) {
            Log.info(TAG, "Track point added at ${point.latitude}, ${point.longitude}")
            updateTrackDistance(track)
        }

        override fun onTrackDurationChanged(track: Track, duration: Duration) {
            updateTrackDuration(duration.hours, duration.minutes, duration.seconds)
        }

        override fun onTrackingStopped(track: Track) {
            Log.info(TAG, "Tracking stopped")
            if (trackToolbar.isShowing) {
                trackToolbar.addMorphListener(TrackToolbarMorphListener())
                trackToolbar.hide()
            }
        }
    }

    private inner class TrackToolbarMorphListener : FloatingToolbar.MorphListener {
        override fun onMorphStart() {
        }

        override fun onMorphEnd() {
        }

        override fun onUnmorphStart() {
        }

        override fun onUnmorphEnd() {
            trackToolbar?.removeMorphListener(this)
            Snackbar.make(
                    coordinatorLayout,
                    R.string.tracking_stopped_and_saved,
                    Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private inner class TrackingServiceConnection : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.info(TAG, "Connected to tracking service")
            trackingService = (service as TrackingService.TrackingBinder).service.apply {
                addListener(this@MapFragment.trackingListener)
            }
            syncTrackToolbarState()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.info(TAG, "Disconnected from tracking service")
            trackingService = null
            syncTrackToolbarState()
        }
    }
}