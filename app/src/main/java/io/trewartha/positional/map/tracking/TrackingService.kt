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
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.trewartha.positional.Log
import io.trewartha.positional.MainActivity
import io.trewartha.positional.R
import io.trewartha.positional.position.LocationLiveData
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.util.*

class TrackingService : LifecycleService() {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 5000L
        private const val LOCATION_UPDATE_MAX_WAIT_TIME = 5000L
        private const val LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val NOTIFICATION_ID = 1
        private const val TAG = "TrackingService"
    }

    private val listeners = mutableSetOf<TrackingListener>()

    private lateinit var locationLiveData: LocationLiveData
    private lateinit var mainActivityPendingIntent: PendingIntent
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationManagerCompat: NotificationManagerCompat

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
        mainActivityPendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(MainActivity.ACTION_SHOW_TRACK, null, this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerCompat = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.notification_channel_id_tracking)
            val channelName = getString(R.string.notification_channel_name_tracking)
            val trackingChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)

            notificationManager.deleteNotificationChannel(channelId)
            notificationManager.createNotificationChannel(trackingChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    fun addListener(listener: TrackingListener) {
        listeners.add(listener)
        if (isTracking()) {
            listener.onTrackingResumed(track!!)
        }
    }

    fun isTracking(): Boolean {
        return track != null
    }

    fun removeListener(listener: TrackingListener) {
        listeners.remove(listener)
    }

    fun startTracking() {
        val channelId = getString(R.string.notification_channel_id_tracking)
        val title = getString(R.string.tracking_notification_title_waiting)
        val text = getString(R.string.tracking_notification_text_waiting)
        val intent = Intent(this, MainActivity::class.java)
        val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_terrain_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .build()
        startForeground(NOTIFICATION_ID, notification)

        if (track == null) {
            track = Track(Instant.now())
        }

        val notificationUpdateTask = object : TimerTask() {
            override fun run() {
                updateNotification()
            }
        }
        timer = Timer(true).apply {
            scheduleAtFixedRate(notificationUpdateTask, 0, 1000L)
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
        timer?.cancel()
        locationLiveData.removeObservers(this)
        listeners.forEach { it.onTrackingStopped(track!!) }
        notificationManagerCompat.cancel(NOTIFICATION_ID)
        track = null

        stopForeground(true)

        Log.info(TAG, "Tracking stopped")
    }

    private fun getElapsedTimeString(start: Instant): String {
        val durationSeconds = Duration.between(start, Instant.now()).seconds
        val durationMinutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        val minutes = (durationMinutes) % 60
        val hours = durationMinutes / 60

        return getString(R.string.tracking_notification_text_in_progress, hours, minutes, seconds)
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

            listeners.forEach { it.onTrackPointAdded(track!!, point) }

            Log.info(TAG, "Added a new track point at (${point.latitude}, ${point.longitude})")
        }
        lastLocation = location
    }

    private fun updateNotification() {
        val channelId = getString(R.string.notification_channel_id_tracking)
        val title = getString(R.string.tracking_notification_title_in_progress)
        val text = getElapsedTimeString(track!!.start)
        val notification = NotificationCompat.Builder(this@TrackingService, channelId)
                .setSmallIcon(R.drawable.ic_terrain_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .build()
        notificationManagerCompat.notify(NOTIFICATION_ID, notification)
    }

    inner class TrackingBinder : Binder() {
        val service = this@TrackingService
    }
}