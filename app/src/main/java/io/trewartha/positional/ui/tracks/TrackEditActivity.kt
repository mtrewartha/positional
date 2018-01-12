package io.trewartha.positional.ui.tracks

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.tracks.Track
import kotlinx.android.synthetic.main.track_edit_activity.*
import java.io.File


class TrackEditActivity : FragmentActivity() {

    companion object {
        const val EXTRA_TRACK_ID = "trackId"

        private const val TAG = "TrackEditActivity"
    }

    private lateinit var trackId: String
    private lateinit var viewModel: FirestoreTrackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_edit_activity)
        trackId = intent.getStringExtra(EXTRA_TRACK_ID)
        viewModel = ViewModelProviders.of(this).get(FirestoreTrackViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveTrack(trackId).observe(this, TrackObserver())
    }

    private inner class TrackObserver : Observer<Track> {
        override fun onChanged(track: Track?) {
            if (track == null) return

            val localSnapshotFile = File(track.snapshotLocal?.path)
            val glide = GlideApp.with(this@TrackEditActivity)
            val glideRequest = if (localSnapshotFile.exists()) {
                glide.load(track.snapshotLocal)
            } else {
                glide.load(track.snapshotRemote)
            }
            glideRequest.centerCrop().into(mapSnapshotImageView)

            nameTextInputEditText.setText(track.name)
        }
    }
}