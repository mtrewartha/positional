package io.trewartha.positional.map.tracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.trewartha.positional.Log
import io.trewartha.positional.MainActivity
import io.trewartha.positional.R
import io.trewartha.positional.position.LocationLiveData

class TrackingService : LifecycleService() {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 1000L
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 10000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "tracking"
        private const val TAG = "TrackingService"
    }

    private val listeners = mutableListOf<TrackingListener>()

    private lateinit var locationLiveData: LocationLiveData
    private lateinit var mainActivityPendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationView: RemoteViews

    private var lastLocation: Location? = null
    private var track: Track? = null
    private var user: FirebaseUser? = null

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        Log.info(TAG, "Bound to the tracking service")
        return TrackingBinder()
    }

    override fun onCreate() {
        super.onCreate()

        user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.warn(TAG, "No one is logged in, shutting down...")
            stopSelf()
            return
        }

        locationLiveData = LocationLiveData(this)
        mainActivityPendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(MainActivity.ACTION_SHOW_TRACK, null, this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationView = RemoteViews(packageName, R.layout.tracking_notification)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val trackingChannel = NotificationChannel(
                    getString(R.string.notification_channel_id_tracking),
                    getString(R.string.notification_channel_name_tracking),
                    NotificationManager.IMPORTANCE_MAX
            )
            notificationManager.createNotificationChannel(trackingChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    fun addListener(listener: TrackingListener) {
        listeners.add(listener)
    }

    fun isTracking(): Boolean {
        return track != null
    }

    fun removeListener(listener: TrackingListener) {
        listeners.remove(listener)
    }

    fun startTracking() {
        updateNotificationWithWaitingStatus()

        if (track == null) {
            track = Track()
        }

        locationLiveData.apply {
            updateInterval = LOCATION_UPDATE_INTERVAL
            updatePriority = LOCATION_UPDATE_PRIORITY
            updateMaxWaitTime = LOCATION_UPDATE_MAX_WAIT_TIME
            observe(this@TrackingService, Observer<Location> { onLocationChanged(it) })
        }
        listeners.forEach { it.onTrackingStarted(track!!) }
        Log.info(TAG, "Tracking started")
    }

    fun stopTracking() {
        locationLiveData.removeObservers(this)
        track = null
        notificationManagerCompat.cancel(NOTIFICATION_ID)
        listeners.forEach { it.onTrackingStopped(track!!) }
        Log.info(TAG, "Tracking stopped")
    }

    private fun onLocationChanged(location: Location?) {
        if (location != null) {
            // If this is the first location or some locations came through as null, make sure we
            // start adding points in a new segment.
            if (lastLocation == null) {
                track!!.startNewSegment()
                Log.info(TAG, "Started a new segment")
            }

            val point = TrackPoint(location)
            track!!.addPoint(point)

            updateNotificationWithPoint(track!!, point)
            listeners.forEach { it.onTrackPointAdded(track!!, point) }

            Log.info(TAG, "Added a new track point at (${point.latitude}, ${point.longitude})")
        }
        lastLocation = location
    }

    private fun updateNotificationWithWaitingStatus() {
        val status = getString(R.string.tracking_notification_status_waiting)
        val coordinates = ""
        val speed = ""
        val altitude = ""
        val distance = ""
        val duration = ""

        notificationView.setTextViewText(R.id.trackingStatusTextView, status)
        notificationView.setTextViewText(R.id.trackingCoordinatesTextView, coordinates)
        notificationView.setTextViewText(R.id.trackingSpeedTextView, speed)
        notificationView.setTextViewText(R.id.trackingAltitudeTextView, altitude)
        notificationView.setTextViewText(R.id.trackingDistanceTextView, distance)
        notificationView.setTextViewText(R.id.trackingDurationTextView, duration)
        updateNotification(notificationView)
    }

    private fun updateNotificationWithPoint(track: Track, point: TrackPoint) {
        val status = getString(R.string.tracking_notification_status_tracking)
        val coordinates = "TODO: ${point.latitude}, ${point.longitude}"
        val speed = "TODO: ${point.speed}"
        val altitude = "TODO: ${point.altitude}"
        val distance = "TODO: ${track.distance}"
        val duration = "TODO"

        notificationView.setTextViewText(R.id.trackingStatusTextView, status)
        notificationView.setTextViewText(R.id.trackingCoordinatesTextView, coordinates)
        notificationView.setTextViewText(R.id.trackingSpeedTextView, speed)
        notificationView.setTextViewText(R.id.trackingAltitudeTextView, altitude)
        notificationView.setTextViewText(R.id.trackingDistanceTextView, distance)
        notificationView.setTextViewText(R.id.trackingDurationTextView, duration)
        updateNotification(notificationView)
    }

    private fun updateNotification(notificationView: RemoteViews) {
        val notification = NotificationCompat.Builder(this, getString(R.string.notification_channel_id_tracking))
                .setSmallIcon(R.drawable.ic_terrain_black_24dp)
                .setContentTitle("Hi!")
                .setContentText("Some text")
//                .setContent(notificationView)
                .setContentIntent(mainActivityPendingIntent)
//                .setStyle(NotificationCompat.BigTextStyle().bigText("Title"))
//                .setCustomBigContentView(notificationView)
                .build()

        notificationManagerCompat.notify(NOTIFICATION_ID, notification)
    }

    inner class TrackingBinder : Binder() {
        val service = this@TrackingService
    }
}