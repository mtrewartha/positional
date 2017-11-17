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
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.location.DistanceUtils
import io.trewartha.positional.location.DistanceUtils.distanceInKilometers
import io.trewartha.positional.location.DistanceUtils.distanceInMiles
import io.trewartha.positional.location.LocationLiveData
import io.trewartha.positional.storage.FileStorage
import io.trewartha.positional.storage.FirebaseFileStorage
import io.trewartha.positional.storage.FirebaseTrackStorage
import io.trewartha.positional.storage.TrackStorage
import io.trewartha.positional.time.Duration
import io.trewartha.positional.ui.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.threeten.bp.Instant
import java.io.File
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

    private val fileStorage: FileStorage = FirebaseFileStorage()
    private val listeners = mutableSetOf<TrackingListener>()
    private val trackStorage: TrackStorage = FirebaseTrackStorage()

    private lateinit var locationLiveData: LocationLiveData
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var sharedPreferences: SharedPreferences

    private var lastLocation: Location? = null
    private var timer: Timer? = null
    private var track: Track? = null
    private var trackPoints: MutableList<TrackPoint>? = null
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
        if (listeners.contains(listener)) return

        listeners.add(listener)
        if (isTracking()) {
            track?.let { listener.onTrackingStarted(it) }
        }
    }

    fun isTracking() = track != null

    fun removeListener(listener: TrackingListener) {
        listeners.remove(listener)
    }

    fun addTrackSnapshot(snapshot: File) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: throw IllegalStateException("No one is signed in")
        val filename = "track_snapshot_${userId}_${Instant.now()}"
        val path = getString(R.string.user_track_snapshot, userId, filename)
        track?.snapshot = fileStorage.upload(snapshot, path)
    }

    fun startTracking() {
        val title = getString(R.string.tracking_notification_title_waiting)
        val text = getString(R.string.tracking_notification_text_waiting)
        val notification = buildNotification(title, text)
        startForeground(NOTIFICATION_ID, notification)

        if (track == null) {
            track = Track().let {
                it.start()
                doAsync { trackStorage.createTrack(it) }
                it
            }
        }

        timer = Timer(true).apply { scheduleAtFixedRate(TrackTimerTask(), 0, 1000L) }

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
            doAsync { trackStorage.saveTrack(stoppedTrack) }
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
        val trackPoint = if (location == null) TrackPoint() else TrackPoint(location)
        val lastTrackPoint = if (trackPoints?.isEmpty() == true) null else trackPoints?.last()
        if (lastTrackPoint != null) {
            track?.distance = (track?.distance ?: 0.0f) + DistanceUtils.distanceBetween(
                    lastTrackPoint,
                    trackPoint
            )
        }
        trackPoints?.add(trackPoint)

        doAsync { trackStorage.saveTrackPoint(currentTrack, trackPoint) }
        listeners.forEach { it.onTrackPointAdded(currentTrack, trackPoint) }
        Log.info(TAG, "Added a new track point at (${trackPoint.latitude}, ${trackPoint.longitude})")
        lastLocation = location
    }

    inner class TrackingBinder : Binder() {
        val service = this@TrackingService
    }

    private inner class TrackTimerTask : TimerTask() {

        override fun run() {
            val currentTrack = track ?: return

            // Update the notification first
            val title = getString(R.string.tracking_notification_title_in_progress)
            val useMetricUnits = sharedPreferences.getBoolean(
                    getString(R.string.settings_metric_units_key),
                    false
            )
            val duration = Duration.between(currentTrack.start ?: return, Instant.now())
            val distance = (currentTrack.distance).let {
                if (useMetricUnits) distanceInKilometers(it) else distanceInMiles(it)
            }
            val format = if (useMetricUnits)
                R.string.tracking_notification_text_in_progress_km
            else
                R.string.tracking_notification_text_in_progress_mi
            val text = getString(format, duration.hours, duration.minutes, duration.seconds, distance)
            val notification = buildNotification(title, text)
            notificationManagerCompat.notify(NOTIFICATION_ID, notification)

            // Then update all of the listeners
            runOnUiThread {
                listeners.forEach { it.onTrackDurationChanged(currentTrack, duration) }
            }
        }
    }
}