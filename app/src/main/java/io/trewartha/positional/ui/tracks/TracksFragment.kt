package io.trewartha.positional.ui.tracks

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.crash.FirebaseCrash
import io.trewartha.positional.R
import io.trewartha.positional.common.Log
import io.trewartha.positional.tracks.Track
import kotlinx.android.synthetic.main.tracks_fragment.*

class TracksFragment : Fragment() {

    companion object {
        private const val TAG = "TracksFragment"
    }

    private val adapter = TracksAdapter(
            { position, track -> onTrackClick(position, track) },
            { position, track -> onTrackEditClick(position, track) },
            { position, track -> onTrackDeleteClick(position, track) }
    ).apply {
        setHasStableIds(true)
    }

    private lateinit var viewModel: FirestoreTracksViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this).get(FirestoreTracksViewModel::class.java)
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
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTracks().observe(this, TracksObserver())
    }

    private fun onTrackClick(position: Int, track: Track) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun onTrackEditClick(position: Int, track: Track) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }

    private fun onTrackDeleteClick(position: Int, track: Track) {
        adapter.tracks.removeAt(position)
        adapter.notifyItemRemoved(position)

        viewModel.deleteTrack(track).addOnCompleteListener(activity) {
            if (it.isSuccessful) {
                showDeleteResultSnackbar(true)
            } else {
                showDeleteResultSnackbar(false)
                adapter.tracks.add(position, track)
                adapter.notifyItemInserted(position)
            }

            it.exception?.let {
                Log.warn(TAG, "Failed to delete track at position #$position", it)
                FirebaseCrash.report(it)
            }
        }
    }

    private fun showDeleteResultSnackbar(deleteSuccessful: Boolean) {
        val snackbarText = if (deleteSuccessful) {
            R.string.track_delete_success_snackbar
        } else {
            R.string.track_delete_failure_snackbar
        }
        Snackbar.make(coordinatorLayout, snackbarText, Snackbar.LENGTH_LONG).show()
    }

    private inner class TracksObserver : Observer<List<Track>> {

        override fun onChanged(tracks: List<Track>?) {
            if (tracks == null) return

            adapter.tracks = tracks.toMutableList()
            adapter.notifyDataSetChanged()

            if (adapter.itemCount == 0) {
                showEmptyView()
            } else {
                hideEmptyView()
            }
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
    }
}