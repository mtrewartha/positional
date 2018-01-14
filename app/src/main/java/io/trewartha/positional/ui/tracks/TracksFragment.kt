package io.trewartha.positional.ui.tracks

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.trewartha.positional.R
import io.trewartha.positional.storage.ViewModelFactory
import io.trewartha.positional.tracks.Track
import io.trewartha.positional.ui.track.TrackEditActivity
import kotlinx.android.synthetic.main.tracks_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TracksFragment : Fragment() {

    companion object {
        private const val REQUEST_CODE_EDIT_TRACK = 1
        private const val TAG = "TracksFragment"
    }

    private val adapter = TracksAdapter(
            { _, track -> onTrackClick(track) },
            { _, track -> onTrackEditClick(track) },
            { _, track -> onTrackDeleteClick(track) }
    )

    private lateinit var tracksViewModel: TracksViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        tracksViewModel = ViewModelProviders.of(
                this,
                ViewModelFactory()).get(TracksViewModel::class.java
        )
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return layoutInflater.inflate(R.layout.tracks_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tracksViewModel.tracks.observe(this, Observer {
            adapter.setList(it)
            if (it?.size ?: 0 <= 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun deleteTrack(track: Track) {
        doAsync {
            val deleted = tracksViewModel.deleteTrack(track) >= 1
            uiThread {
                showDeleteResultSnackbar(track, deleted)
            }
        }
    }

    private fun hideEmptyView() {
        emptyView.clearAnimation()
        recyclerView.clearAnimation()

        val emptyViewAnimator = emptyView.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animation.removeListener(this)
                    }
                })

        val recyclerViewAnimator = recyclerView.animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        recyclerView.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        animation.removeListener(this)
                    }
                })

        emptyViewAnimator.start()
        recyclerViewAnimator.start()
    }

    private fun onTrackClick(track: Track) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun onTrackEditClick(track: Track) {
        val intent = Intent(context, TrackEditActivity::class.java).apply {
            putExtra(TrackEditActivity.EXTRA_TRACK_ID, track.start.toString())
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT_TRACK)
    }

    private fun onTrackDeleteClick(track: Track) {
        val trackName = track.name ?: getString(R.string.track_default_name)
        AlertDialog.Builder(context)
                .setTitle(getString(R.string.tracks_delete_dialog_title, trackName))
                .setMessage(R.string.tracks_delete_dialog_message)
                .setPositiveButton(R.string.delete, { _, _ ->
                    deleteTrack(track)
                })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun showDeleteResultSnackbar(track: Track, deleteSuccessful: Boolean) {
        val trackName = track.name ?: getString(R.string.track_default_name)
        val snackbarText = if (deleteSuccessful) {
            getString(R.string.track_delete_success_snackbar, trackName)
        } else {
            getString(R.string.track_delete_failure_snackbar, trackName)
        }
        Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG).show()
    }

    private fun showEmptyView() {
        emptyView.clearAnimation()
        recyclerView.clearAnimation()

        val emptyViewAnimator = emptyView.animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animation.removeListener(this)
                    }
                })
        val recyclerViewAnimator = recyclerView.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animation.removeListener(this)
                        recyclerView.visibility = View.GONE
                    }
                })

        emptyViewAnimator.start()
        recyclerViewAnimator.start()
    }
}