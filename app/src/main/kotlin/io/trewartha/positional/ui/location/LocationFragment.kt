package io.trewartha.positional.ui.location

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.trewartha.positional.R
import io.trewartha.positional.ui.utils.showSnackbar
import kotlinx.android.synthetic.main.location_fragment.*

class LocationFragment : Fragment() {

    private lateinit var viewModel: LocationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.location_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.coordinates.observe(viewLifecycleOwner, ::observeCoordinates)
        viewModel.bearing.observe(viewLifecycleOwner, ::observeBearing)
        viewModel.bearingAccuracy.observe(viewLifecycleOwner, ::observeBearingAccuracy)
        viewModel.elevation.observe(viewLifecycleOwner, ::observeElevation)
        viewModel.elevationAccuracy.observe(viewLifecycleOwner, ::observeElevationAccuracy)
        viewModel.speed.observe(viewLifecycleOwner, ::observeSpeed)
        viewModel.speedAccuracy.observe(viewLifecycleOwner, ::observeSpeedAccuracy)
        viewModel.updatedAt.observe(viewLifecycleOwner, ::observeUpdatedAt)
        viewModel.screenLocked.observe(viewLifecycleOwner, ::observeScreenLocked)

        viewModel.coordinatesCopyEvents.observe(viewLifecycleOwner, ::observeCoordinatesCopyEvent)
        viewModel.coordinatesShareEvents.observe(viewLifecycleOwner, ::observeCoordinatesShareEvent)
        viewModel.screenLockEvents.observe(viewLifecycleOwner, ::observeScreenLockEvent)

        copyButton.setOnClickListener { viewModel.handleViewEvent(Event.CopyClick) }
        screenLockButton.setOnClickListener { viewModel.handleViewEvent(Event.ScreenLockClick) }
        shareButton.setOnClickListener { viewModel.handleViewEvent(Event.ShareClick) }
    }

    private fun observeBearing(bearing: String) {
        bearingValueTextView.text = bearing
    }

    private fun observeBearingAccuracy(bearingAccuracy: String) {
        bearingAccuracyValueTextView.text = bearingAccuracy
    }

    private fun observeCoordinates(coordinates: LocationViewModel.Coordinates) {
        progressIndicator.hide()
        coordinatesTextView.apply {
            maxLines = coordinates.maxLines
            text = coordinates.text
        }
    }

    private fun observeCoordinatesCopyEvent(event: LocationViewModel.CoordinatesCopyEvent) {
        if (event.handled) return
        when (event) {
            is LocationViewModel.CoordinatesCopyEvent.Error -> {
                coordinatorLayout.showSnackbar(R.string.location_copied_coordinates_failure)
            }
            is LocationViewModel.CoordinatesCopyEvent.Success -> {
                coordinatorLayout.showSnackbar(R.string.location_copied_coordinates_both_success)
            }
        }
        event.handled = true
    }

    private fun observeCoordinatesShareEvent(event: LocationViewModel.CoordinatesShareEvent) {
        if (event.handled) return
        when (event) {
            is LocationViewModel.CoordinatesShareEvent.Error -> {
                coordinatorLayout.showSnackbar(R.string.location_share_snackbar_failure)
            }
            is LocationViewModel.CoordinatesShareEvent.Success -> {
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, event.coordinates)
                    type = "text/plain"
                })
            }
        }
        event.handled = true
    }

    private fun observeElevation(elevation: String) {
        elevationValueTextView.text = elevation
    }

    private fun observeElevationAccuracy(elevationAccuracy: String) {
        elevationAccuracyValueTextView.text = elevationAccuracy
    }

    private fun observeScreenLocked(screenLocked: Boolean) {
        screenLockButton.setIconResource(
                if (screenLocked) R.drawable.ic_twotone_smartphone_24px
                else R.drawable.ic_twotone_screen_lock_portrait_24px
        )
        activity?.window?.apply {
            if (screenLocked) addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun observeScreenLockEvent(event: LocationViewModel.ScreenLockEvent) {
        if (event.handled) return
        coordinatorLayout.showSnackbar(
                if (event.locked) R.string.location_snackbar_screen_locked
                else R.string.location_snackbar_screen_unlocked
        )
        event.handled = true
    }

    private fun observeSpeed(speed: String) {
        speedValueTextView.text = speed
    }

    private fun observeSpeedAccuracy(speedAccuracy: String) {
        speedAccuracyValueTextView.text = speedAccuracy
    }

    private fun observeUpdatedAt(updatedAt: String) {
        updatedAtTextView.text = updatedAt
    }

    sealed class Event {
        object CopyClick : Event()
        object ScreenLockClick : Event()
        object ShareClick : Event()
    }
}
