package io.trewartha.positional.ui.track

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.trewartha.positional.R
import io.trewartha.positional.common.GlideApp
import io.trewartha.positional.common.Log
import io.trewartha.positional.common.getUUIDExtra
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.BaseActivity
import io.trewartha.positional.ui.DayNightThemeUtils
import kotlinx.android.synthetic.main.track_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class TrackActivity : BaseActivity() {

    companion object {
        const val RESULT_DELETE_FAILED = Activity.RESULT_FIRST_USER
        const val RESULT_DELETE_SUCCESSFUL = Activity.RESULT_FIRST_USER + 1

        private const val EXTRA_TRACK_ID = "trackId"
        private const val REQUEST_CODE_EDIT = 1
        private const val TAG = "Track"
    }

    private val defaultName by lazy { getString(R.string.track_default_name) }

    private lateinit var trackId: UUID
    private lateinit var trackViewModel: TrackViewModel

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onTrackLoaded, ::onTrackLoadFailed)
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

    @SuppressLint("CheckResult")
    private fun onTrackLoaded(track: Track) {
        this.track = track

        val inDayMode = DayNightThemeUtils(this).inDayMode()
        val errorDrawable = getDrawable(R.drawable.ic_terrain_black_24dp).apply {
            val tintColor = if (inDayMode)
                R.color.trackImageForegroundDay
            else
                R.color.trackImageForegroundNight
            setTint(ContextCompat.getColor(this@TrackActivity, tintColor))
        }
        val imageBackgroundColor = if (inDayMode)
            R.color.trackImageBackgroundDay
        else
            R.color.trackImageBackgroundNight
        imageView.setBackgroundResource(imageBackgroundColor)

        GlideApp.with(this)
                .load(track.imageLocal ?: track.imageRemote)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(errorDrawable)
                .centerCrop()
                .into(imageView)

        nameTextView.text = if (track.name.isNullOrBlank()) defaultName else track.name
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

        private val intent = Intent(context, TrackActivity::class.java)

        fun withTrackId(trackId: UUID): IntentBuilder {
            intent.putExtra(EXTRA_TRACK_ID, trackId.toString())
            return this
        }

        fun build() = intent
    }
}