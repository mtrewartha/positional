package io.trewartha.positional.map

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.LocationRequest
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnCameraMoveStartedListener.REASON_API_GESTURE
import io.trewartha.positional.Log
import io.trewartha.positional.R
import io.trewartha.positional.common.LocationAwareFragment
import io.trewartha.positional.map.tracking.Track
import io.trewartha.positional.map.tracking.TrackPoint
import io.trewartha.positional.map.tracking.TrackingService
import kotlinx.android.synthetic.main.map_fragment.*

class MapFragment : LocationAwareFragment() {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 1000L
        private const val MAP_ANIMATION_DURATION_MS = 2000
        private const val MAP_ZOOM_LEVEL_DEFAULT = 17.0
        private const val TAG = "MapFragment"
    }

    private val trackingListener = TrackingListener()

    private var followingUserLocation = true
    private var map: MapboxMap? = null
    private var trackingService: TrackingService? = null
    private var trackingServiceConnection: TrackingServiceConnection? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Mapbox.getInstance(context, context.getString(R.string.mapbox_key))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return layoutInflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync gotMap@ {
            map = it
            it.setOnCameraMoveStartedListener(OnCameraMoveStartedListener())
            zoomToLocation(lastLocation ?: return@gotMap)
        }

        myLocationButton.setOnClickListener { onMyLocationClick() }
        recordTrackButton.setOnClickListener { onRecordTrackClick() }
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
        mapView.onSaveInstanceState(outState)
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
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unbindService(trackingServiceConnection)
    }

    override fun onLocationChanged(location: Location?) {
        if (location == null) return

        if (followingUserLocation) {
            zoomToLocation(location)
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
        when {
            trackingServiceConnection == null -> startTracking()
            trackingServiceConnection?.connected == true -> stopTracking()
            else -> Log.info(TAG, "Already connecting to the tracking service, hold tight...")
        }

        // TODO: Animate the record button to reflect its new status
    }

    private fun startTracking() {
        trackingServiceConnection = TrackingServiceConnection()

        val intent = Intent(context, TrackingService::class.java)
        context.startService(intent)
        context.bindService(intent, trackingServiceConnection, 0)

        Log.info(TAG, "Tracking started")
    }

    private fun stopTracking() {
        trackingService?.stopTracking()
        trackingService?.stopSelf()
        trackingService = null
        trackingServiceConnection = null

        Log.info(TAG, "Tracking stopped")
    }

    private fun zoomToLocation(location: Location, zoomLevel: Double = MAP_ZOOM_LEVEL_DEFAULT) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(zoomLevel).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map?.animateCamera(cameraUpdate, MAP_ANIMATION_DURATION_MS)
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

    private inner class TrackingListener : io.trewartha.positional.map.tracking.TrackingListener {

        override fun onTrackingUnavailable() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTrackingStarted(track: Track) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTrackPointAdded(track: Track, point: TrackPoint) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTrackingTemporarilyUnavailable() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTrackingResumed() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTrackingStopped(track: Track) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLocationDisabled() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private inner class TrackingServiceConnection : ServiceConnection {

        var connected = false

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.info(TAG, "Connected to tracking service")
            connected = true
            val trackingService = (service as TrackingService.TrackingBinder).service
            this@MapFragment.trackingService = trackingService

            trackingService.startTracking()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.info(TAG, "Disconnected from tracking service")
            connected = false
            trackingService?.apply {
                removeListener(this@MapFragment.trackingListener)
                stopTracking()
            }
        }
    }
}