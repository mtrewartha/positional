package io.trewartha.positional.ui.track

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.trewartha.positional.BuildConfig
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.common.getUUIDExtra
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.tracks.TrackPoint
import io.trewartha.positional.ui.BaseActivity
import io.trewartha.positional.ui.common.TrackUiHelper
import kotlinx.android.synthetic.main.track_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*


class TrackInfoActivity : BaseActivity() {

    private val dateFormatter by lazy { DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT) }
    private val defaultName by lazy { getString(R.string.track_default_name) }
    private val timeFormatter by lazy { DateTimeFormatter.ofPattern("h:mm a") }

    private lateinit var trackId: UUID
    private lateinit var trackViewModel: TrackViewModel

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_activity)
        trackId = intent.getUUIDExtra(EXTRA_TRACK_ID)
                ?: throw IllegalArgumentException("The activity wasn't given a track ID")
        trackViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory()
        ).get(TrackViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = null
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.track, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        trackViewModel
                .getTrack(trackId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onTrackLoaded, ::onTrackLoadFailed)
                .attach()

        trackViewModel
                .getTrackPoints(trackId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onTrackPointsLoaded, ::onTrackLoadFailed)
                .attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> onTrackEditClick()
            R.id.delete -> onTrackDeleteClick(item)
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_EDIT -> when (resultCode) {
                TrackEditActivity.RESULT_SAVE_FAILED -> showSaveResultSnackbar(false)
                TrackEditActivity.RESULT_SAVE_SUCCESSFUL -> showSaveResultSnackbar(true)
            }
        }
    }

    private fun deleteTrack(deleteMenuItem: MenuItem, track: Track) {
        deleteMenuItem.isEnabled = false
        deleteMenuItem.title = getString(R.string.deleting)

        doAsync {
            val success = trackViewModel.deleteTrack(track) > 0

            uiThread {
                setResult(if (success) RESULT_DELETE_SUCCESSFUL else RESULT_DELETE_FAILED)
                finish()
            }
        }
    }

    private fun findLatLngBounds(trackPoints: List<TrackPoint>): LatLngBounds {
        if (trackPoints.isEmpty())
            throw IllegalArgumentException("Track doesn't contain any points")

        val firstTrackPoint = trackPoints.first()
        var minLat = firstTrackPoint.latitude
        var maxLat = firstTrackPoint.latitude
        var minLng = firstTrackPoint.longitude
        var maxLng = firstTrackPoint.longitude

        trackPoints.forEach {
            if (it.latitude < minLat) minLat = it.latitude
            if (it.latitude > maxLat) maxLat = it.latitude
            if (it.longitude < minLng) minLng = it.longitude
            if (it.longitude > maxLng) maxLng = it.longitude
        }

        // This might create a funky bounding box for tracks that cross the boundary at 180°
        // longitude... consider improving it.
        return LatLngBounds.from(maxLat, minLng, minLat, maxLng)
    }

    @SuppressLint("CheckResult")
    private fun onTrackLoaded(track: Track) {
        this.track = track
        TrackUiHelper.setImage(this, track, imageView)
        nameTextView.text = if (track.name.isNullOrBlank()) defaultName else track.name

        val currentZoneId = ZoneId.systemDefault()
        track.start?.atZone(currentZoneId)?.let {
            startDateTextView.text = it.format(dateFormatter)
            startTimeTextView.text = it.format(timeFormatter)
        }
        track.end?.atZone(currentZoneId)?.let {
            endDateTextView.text = it.format(dateFormatter)
            endTimeTextView.text = it.format(timeFormatter)
        }

        durationTextView.text = TrackUiHelper.getDuration(this, track)
        distanceTextView.text = TrackUiHelper.setDistance(this, track)
    }

    private fun onTrackLoadFailed(throwable: Throwable) {
        Log.warn(TAG, "Unable to load either the track or points", throwable)
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.track_edit_load_failed_title)
                .setMessage(R.string.track_edit_load_failed_message)
                .setPositiveButton(R.string.ok) { _, _ -> finish() }
                .show()
    }

    private fun onTrackPointsLoaded(trackPoints: List<TrackPoint>) {
        if (trackPoints.isEmpty()) return

        mapView.getMapAsync {
            it.uiSettings.setAllGesturesEnabled(BuildConfig.DEBUG)

            val trackPointLatLngs = trackPoints.map { LatLng(it.latitude, it.longitude) }
            val trackPolyline = PolylineOptions()
                    .addAll(trackPointLatLngs)
                    .alpha(MAP_POLYLINE_ALPHA)
                    .width(MAP_POLYLINE_WIDTH)
            it.addPolyline(trackPolyline)

            // Zoom the map in around the track
            it.cameraPosition = it.getCameraForLatLngBounds(
                    findLatLngBounds(trackPoints),
                    MAP_PADDING
            )
        }
    }

    private fun onTrackDeleteClick(deleteMenuItem: MenuItem) {
        track?.let {
            val trackName = it.name ?: getString(R.string.track_default_name)
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.track_delete_dialog_title, trackName))
                    .setMessage(R.string.track_delete_dialog_message)
                    .setPositiveButton(R.string.delete, { _, _ ->
                        deleteTrack(deleteMenuItem, it)
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    private fun onTrackEditClick() {
        val trackEditIntent = TrackEditActivity.IntentBuilder(this)
                .withTrackId(trackId)
                .build()
        startActivityForResult(trackEditIntent, REQUEST_CODE_EDIT)
    }

    private fun showSaveResultSnackbar(saveSuccessful: Boolean) {
        val snackbarText = getString(
                if (saveSuccessful)
                    R.string.track_save_success_snackbar
                else
                    R.string.track_save_failure_snackbar
        )
        Snackbar.make(trackLayout, snackbarText, Snackbar.LENGTH_LONG).show()
    }

    class IntentBuilder(context: Context) {

        private val intent = Intent(context, TrackInfoActivity::class.java)

        fun withTrackId(trackId: UUID): IntentBuilder {
            intent.putExtra(EXTRA_TRACK_ID, trackId.toString())
            return this
        }

        fun build() = intent
    }

    companion object {
        const val RESULT_DELETE_FAILED = Activity.RESULT_FIRST_USER
        const val RESULT_DELETE_SUCCESSFUL = Activity.RESULT_FIRST_USER + 1

        private const val EXTRA_TRACK_ID = "trackId"
        private val MAP_PADDING = intArrayOf(125, 125, 125, 125)
        private const val MAP_POLYLINE_ALPHA = .50f
        private const val MAP_POLYLINE_WIDTH = 2.0f
        private const val REQUEST_CODE_EDIT = 1
        private const val TAG = "Track"
    }
}