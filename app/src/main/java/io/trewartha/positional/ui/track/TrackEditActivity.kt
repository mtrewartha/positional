package io.trewartha.positional.ui.track

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.common.getUUIDExtra
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.BaseActivity
import io.trewartha.positional.ui.common.TrackUiHelper
import kotlinx.android.synthetic.main.track_edit_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*


class TrackEditActivity : BaseActivity() {

    companion object {
        const val RESULT_SAVE_FAILED = Activity.RESULT_FIRST_USER + 2
        const val RESULT_SAVE_SUCCESSFUL = Activity.RESULT_FIRST_USER + 3

        private const val EXTRA_TRACK_ID = "trackId"
        private const val REQUEST_CODE_GET_IMAGE = 1
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
            val closeDrawable = getDrawable(R.drawable.ic_close_black_24dp).apply {
                setTint(Color.WHITE)
            }
            setHomeAsUpIndicator(closeDrawable)
            setDisplayHomeAsUpEnabled(true)
        }

        imageFab.setOnClickListener { onImageFabClick() }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_GET_IMAGE -> when (resultCode) {
                Activity.RESULT_OK -> onImageChosen(data?.data ?: return)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> onTrackSaveClick(item)
            android.R.id.home -> finish()
        }
        return true
    }

    private fun onImageChosen(image: Uri) {
        doAsync {
            val filename = "image-$trackId"

            // If there's an old image file for this track already saved, delete it. We only have
            // one file per track.
            if (deleteFile(filename)) Log.info(TAG, "Deleted old image from $image")

            val importImage = Uri.fromFile(File(filesDir, filename))
            val imageInputStream = contentResolver.openInputStream(image)
            val importOutputStream = contentResolver.openOutputStream(importImage)
            val copiedByteCount = imageInputStream.copyTo(importOutputStream)
            imageInputStream.close()
            importOutputStream.close()
            Log.info(TAG, "Copied $copiedByteCount bytes from $image to $importImage")

            trackViewModel.updateTrack(
                    track?.apply { imageLocal = importImage } ?: return@doAsync
            )
            Log.info(TAG, "Updated track ${track?.id} with new image: $importImage")
        }
    }

    private fun onImageFabClick() {
        // TODO: Let the user take a picture instead of choosing an image
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE)
    }

    @SuppressLint("CheckResult")
    private fun onTrackLoaded(track: Track) {
        this.track = track
        TrackUiHelper.setImage(this, track, imageView)
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