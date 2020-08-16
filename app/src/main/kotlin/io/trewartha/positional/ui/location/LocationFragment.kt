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
import androidx.lifecycle.observe
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
        // ViewModel data
        viewModel.locationData.observe(viewLifecycleOwner, ::handleLocationData)
        viewModel.screenLockData.observe(viewLifecycleOwner, ::handleScreenLockData)

        // ViewModel events
        viewModel.coordinatesCopyEvents.observe(viewLifecycleOwner, ::handleCoordinatesCopyEvent)
        viewModel.coordinatesShareEvents.observe(viewLifecycleOwner, ::handleCoordinatesShareEvent)
        viewModel.screenLockEvents.observe(viewLifecycleOwner, ::handleScreenLockEvent)

        // View events
        copyButton.setOnClickListener { viewModel.handleViewEvent(Event.CopyClick) }
        screenLockButton.setOnClickListener { viewModel.handleViewEvent(Event.ScreenLockClick) }
        shareButton.setOnClickListener { viewModel.handleViewEvent(Event.ShareClick) }
    }

    private fun handleLocationData(data: LocationViewModel.LocationData) {
        coordinatesTextView.apply {
            maxLines = data.coordinatesLines
            text = data.coordinates
        }
        bearingValueTextView.text = data.bearing
        bearingAccuracyValueTextView.apply {
            if (data.bearingAccuracy == null) {
                visibility = View.GONE
            } else {
                text = data.bearingAccuracy
            }
        }
        elevationValueTextView.text = data.elevation
        elevationAccuracyValueTextView.apply {
            if (data.elevationAccuracy == null) {
                visibility = View.GONE
            } else {
                text = data.elevationAccuracy
            }
        }
        speedValueTextView.text = data.speed
        speedAccuracyValueTextView.apply {
            if (data.speedAccuracy == null) {
                visibility = View.GONE
            } else {
                text = data.speedAccuracy
            }
        }
        updatedAtTextView.text = data.updatedAt
    }

    private fun handleScreenLockData(data: LocationViewModel.ScreenLockData) {
        screenLockButton.setIconResource(
                if (data.locked) R.drawable.ic_twotone_smartphone_24px
                else R.drawable.ic_twotone_screen_lock_portrait_24px
        )
        activity?.window?.apply {
            if (data.locked) addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun handleCoordinatesCopyEvent(event: LocationViewModel.CoordinatesCopyEvent) {
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

    private fun handleCoordinatesShareEvent(event: LocationViewModel.CoordinatesShareEvent) {
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

    private fun handleScreenLockEvent(event: LocationViewModel.ScreenLockEvent) {
        if (event.handled) return
        coordinatorLayout.showSnackbar(
                if (event.locked) R.string.location_snackbar_screen_locked
                else R.string.location_snackbar_screen_unlocked
        )
        event.handled = true
    }

    sealed class Event {
        object CopyClick : Event()
        object ScreenLockClick : Event()
        object ShareClick : Event()
    }
}
