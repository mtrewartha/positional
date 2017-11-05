package io.trewartha.positional.tracks

import android.app.*
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.trewartha.positional.Log
import io.trewartha.positional.R
import io.trewartha.positional.location.DistanceUtils.distanceInKilometers
import io.trewartha.positional.location.DistanceUtils.distanceInMiles
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.ui.MainActivity
import java.util.*

class TrackingService : LifecycleService() {

    companion object {
        private const val ACTION_STOP_TRACKING = "stopTracking"
        private const val LOCATION_UPDATE_INTERVAL = 5000L
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 5000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val NOTIFICATION_ID = 100
        private const val TAG = "TrackingService"
    }

    private val listeners = mutableSetOf<TrackingListener>()

    private lateinit var locationLiveData: LocationLiveData
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var sharedPreferences: SharedPreferences

    private var lastLocation: Location? = null
    private var timer: Timer? = null
    private var track: Track? = null
    private var user: FirebaseUser? = null

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
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

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerCompat = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.notification_channel_id_tracking)
            val channelName = getString(R.string.notification_channel_name_tracking)
            val trackingChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)

            notificationManager.deleteNotificationChannel(channelId)
            notificationManager.createNotificationChannel(trackingChannel)
        }

        sharedPreferences = getSharedPreferences(
                getString(R.string.settings_filename),
                Context.MODE_PRIVATE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.action == ACTION_STOP_TRACKING) stopTracking()
        return Service.START_STICKY
    }

    fun addListener(listener: TrackingListener) {
        listeners.add(listener)
        if (isTracking()) {
            track?.let { listener.onTrackingStarted(it) }
        }
    }

    fun isTracking(): Boolean {
        return track != null
    }

    fun removeListener(listener: TrackingListener) {
        listeners.remove(listener)
    }

    fun startTracking() {
        val title = getString(R.string.tracking_notification_title_waiting)
        val text = getString(R.string.tracking_notification_text_waiting)
        val notification = buildNotification(title, text)
        startForeground(NOTIFICATION_ID, notification)

        if (track == null) {
            val defaultName = getString(R.string.track_default_name)
            track = Track(defaultName).apply { start() }
        }

        val trackTimerTask = object : TimerTask() {
            override fun run() {
                updateNotification()
            }
        }
        timer = Timer(true).apply {
            scheduleAtFixedRate(trackTimerTask, 0, 1000L)
        }

        locationLiveData.apply {
            updateInterval = LOCATION_UPDATE_INTERVAL
            updatePriority = LOCATION_UPDATE_PRIORITY
            updateMaxWaitTime = LOCATION_UPDATE_MAX_WAIT_TIME
            observe(this@TrackingService, Observer<Location> { onLocationChanged(it) })
        }

        track?.let { startedTrack -> listeners.forEach { it.onTrackingStarted(startedTrack) } }
        Log.info(TAG, "Tracking started")
    }

    fun stopTracking() {
        timer?.cancel()
        locationLiveData.removeObservers(this)
        track?.let { stoppedTrack ->
            stoppedTrack.stop()
            listeners.forEach { it.onTrackingStopped(stoppedTrack) }
        }
        notificationManagerCompat.cancel(NOTIFICATION_ID)
        track = null

        stopForeground(true)

        Log.info(TAG, "Tracking stopped")
    }

    private fun buildNotification(title: String, text: String): Notification {
        val channelId = getString(R.string.notification_channel_id_tracking)
        val contentIntent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
        val contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val stopIntent = Intent(ACTION_STOP_TRACKING, null, this, TrackingService::class.java)
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        return NotificationCompat.Builder(this@TrackingService, channelId)
                .setChannelId(channelId)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setShowWhen(false)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .setSmallIcon(R.drawable.ic_terrain_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_stop_black_24dp, "Stop", stopPendingIntent)
                .build()
    }

    private fun onLocationChanged(location: Location?) {
        val currentTrack = track ?: return

        if (location != null) {
            val point = TrackPoint(location)
            currentTrack.addPoint(point)

            listeners.forEach { it.onTrackPointAdded(currentTrack, point) }

            Log.info(TAG, "Added a new track point at (${point.latitude}, ${point.longitude})")
        }
        lastLocation = location
    }

    private fun updateNotification() {
        val duration = track?.duration ?: return
        val distanceInMeters = track?.distance ?: return
        val title = getString(R.string.tracking_notification_title_in_progress)
        val useMetricUnits = sharedPreferences.getBoolean(
                getString(R.string.settings_metric_units_key),
                false
        )

        val distance = if (useMetricUnits)
            distanceInKilometers(distanceInMeters)
        else
            distanceInMiles(distanceInMeters)

        val format = if (useMetricUnits)
            R.string.tracking_notification_text_in_progress_km
        else
            R.string.tracking_notification_text_in_progress_mi

        val text = getString(format, duration.hours, duration.minutes, duration.seconds, distance)
        val notification = buildNotification(title, text)
        notificationManagerCompat.notify(NOTIFICATION_ID, notification)
    }

    inner class TrackingBinder : Binder() {
        val service = this@TrackingService
    }
}