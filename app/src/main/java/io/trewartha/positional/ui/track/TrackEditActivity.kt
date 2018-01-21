package io.trewartha.positional.ui.track

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.common.Log
import io.trewartha.positional.common.getUUIDExtra
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.BaseActivity
import kotlinx.android.synthetic.main.track_edit_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class TrackEditActivity : BaseActivity() {

    companion object {
        const val RESULT_DELETE_FAILED = Activity.RESULT_FIRST_USER
        const val RESULT_DELETE_SUCCESSFUL = Activity.RESULT_FIRST_USER + 1
        const val RESULT_SAVE_FAILED = Activity.RESULT_FIRST_USER + 2
        const val RESULT_SAVE_SUCCESSFUL = Activity.RESULT_FIRST_USER + 3

        private const val EXTRA_TRACK_ID = "trackId"
        private const val TAG = "TrackEdit"
    }

    private lateinit var trackId: UUID
    private lateinit var trackViewModel: TrackViewModel

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_edit_activity)
        trackId = intent.getUUIDExtra(EXTRA_TRACK_ID)
                ?: throw IllegalArgumentException("The activity wasn't given a track ID")
        trackViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory()
        ).get(TrackViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.track_edit_title)
            val closeDrawable = getDrawable(R.drawable.ic_close_black_24dp).apply {
                val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightMode == Configuration.UI_MODE_NIGHT_YES) setTint(Color.WHITE)
            }
            setHomeAsUpIndicator(closeDrawable)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.track_edit, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        trackViewModel
                .getTrack(trackId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onTrackLoaded, ::onTrackLoadFailed)
                .attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> onTrackSaveClick(item)
            R.id.delete -> onTrackDeleteClick(item)
            android.R.id.home -> finish()
        }
        return true
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

    private fun onTrackDeleteClick(deleteMenuItem: MenuItem) {
        val trackName = track?.name ?: getString(R.string.track_default_name)
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.track_delete_dialog_title, trackName))
                .setMessage(R.string.track_delete_dialog_message)
                .setPositiveButton(R.string.delete, { _, _ ->
                    deleteTrack(deleteMenuItem, track!!)
                })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    @SuppressLint("CheckResult")
    private fun onTrackLoaded(track: Track) {
        this.track = track
        val snapshotUri = track.imageLocal ?: track.imageRemote
        val errorDrawable = getDrawable(R.drawable.ic_terrain_black_24dp).apply {
            setTint(ContextCompat.getColor(this@TrackEditActivity, R.color.gray4))
        }
        GlideApp.with(this)
                .load(snapshotUri)
                .error(errorDrawable)
                .centerCrop()
                .into(imageView)

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

    private fun onTrackSaveClick(saveMenuItem: MenuItem) {
        saveMenuItem.isEnabled = false
        saveMenuItem.title = getString(R.string.saving)

        doAsync {
            val success = track?.let {
                it.name = nameTextInputEditText.text.toString()
                trackViewModel.updateTrack(it) > 0
            } ?: false

            uiThread {
                setResult(if (success) RESULT_SAVE_SUCCESSFUL else RESULT_SAVE_FAILED)
                finish()
            }
        }
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