package io.trewartha.positional.ui.track

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.common.Log
import io.trewartha.positional.common.getUUIDExtra
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.BaseActivity
import kotlinx.android.synthetic.main.track_edit_activity.*
import java.io.File
import java.util.*


class TrackEditActivity : BaseActivity() {

    companion object {
        private const val EXTRA_TRACK_ID = "trackId"
        private const val TAG = "TrackEdit"
    }

    private lateinit var trackId: UUID
    private lateinit var trackViewModel: TrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_edit_activity)
        trackId = intent.getUUIDExtra(EXTRA_TRACK_ID)
                ?: throw IllegalArgumentException("The activity wasn't given a track ID")
        trackViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory()
        ).get(TrackViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        trackViewModel
                .getTrack(trackId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onTrackLoaded, ::onTrackLoadFailed)
                .attach()
    }

    @SuppressLint("CheckResult")
    private fun onTrackLoaded(track: Track) {
        val localSnapshotFile = File(track.snapshotLocal?.path)
        val glide = GlideApp.with(this)
        val glideRequest = if (localSnapshotFile.exists()) {
            glide.load(track.snapshotLocal)
        } else {
            glide.load(track.snapshotRemote)
        }
        glideRequest.centerCrop().into(mapSnapshotImageView)

        nameTextInputEditText.setText(track.name)
    }

    private fun onTrackLoadFailed(throwable: Throwable) {
        Log.warn(TAG, "Unable to load track", throwable)
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.track_edit_load_failed_title)
                .setMessage(R.string.track_edit_load_failed_message)
                .setPositiveButton(R.string.ok) { _, _ -> finish() }
                .show()
    }

    class IntentBuilder(context: Context) {

        private val intent = Intent(context, TrackEditActivity::class.java)

        fun withTrackId(trackId: UUID): IntentBuilder {
            intent.putExtra(EXTRA_TRACK_ID, trackId.toString())
            return this
        }

        fun build() = intent
    }
}