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
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import io.trewartha.positional.R
import io.trewartha.positional.databinding.LocationFragmentBinding
import io.trewartha.positional.ui.utils.showSnackbar

class LocationFragment : Fragment() {

    private var _viewBinding: LocationFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private lateinit var viewModel: LocationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _viewBinding = LocationFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            accuracyVisibility.observe(viewLifecycleOwner, ::observeAccuracyVisibility)
            coordinates.observe(viewLifecycleOwner, ::observeCoordinates)
            coordinatesAccuracy.observe(viewLifecycleOwner, ::observeCoordinatesAccuracy)
            bearing.observe(viewLifecycleOwner, ::observeBearing)
            bearingAccuracy.observe(viewLifecycleOwner, ::observeBearingAccuracy)
            elevation.observe(viewLifecycleOwner, ::observeElevation)
            elevationAccuracy.observe(viewLifecycleOwner, ::observeElevationAccuracy)
            speed.observe(viewLifecycleOwner, ::observeSpeed)
            speedAccuracy.observe(viewLifecycleOwner, ::observeSpeedAccuracy)
            updatedAt.observe(viewLifecycleOwner, ::observeUpdatedAt)
            screenLockState.observe(viewLifecycleOwner, ::observeScreenLockState)
            events.observe(viewLifecycleOwner, ::observeEvent)
        }

        with(viewBinding) {
            copyButton.setOnClickListener { viewModel.handleViewEvent(Event.CopyClick) }
            screenLockButton.setOnClickListener { viewModel.handleViewEvent(Event.ScreenLockClick) }
            shareButton.setOnClickListener { viewModel.handleViewEvent(Event.ShareClick) }
            helpButton.setOnClickListener { viewModel.handleViewEvent(Event.HelpClick) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun observeAccuracyVisibility(visibility: Int) {
        with(viewBinding) {
            coordinatesAccuracyPlaceholderTextView.visibility = visibility
            bearingAccuracyValueTextView.visibility = visibility
            elevationAccuracyValueTextView.visibility = visibility
            speedAccuracyValueTextView.visibility = visibility
        }
    }

    private fun observeBearing(bearing: String) {
        viewBinding.bearingValueTextView.text = bearing
    }

    private fun observeBearingAccuracy(bearingAccuracy: String?) {
        viewBinding.bearingAccuracyValueTextView.text = bearingAccuracy
    }

    private fun observeCoordinates(coordinates: LocationViewModel.Coordinates) {
        with(viewBinding) {
            progressIndicator.hide()
            coordinatesTextView.apply {
                maxLines = coordinates.maxLines
                text = coordinates.text
            }
        }
    }

    private fun observeCoordinatesAccuracy(coordinatesAccuracy: String) {
        viewBinding.coordinatesAccuracyValueTextView.text = coordinatesAccuracy
    }

    private fun observeCoordinatesCopyEvent(event: LocationViewModel.Event.CoordinatesCopy) {
        when (event) {
            is LocationViewModel.Event.CoordinatesCopy.Error -> {
                viewBinding.coordinatorLayout.showSnackbar(R.string.location_copied_coordinates_failure)
            }
            is LocationViewModel.Event.CoordinatesCopy.Success -> {
                viewBinding.coordinatorLayout.showSnackbar(R.string.location_copied_coordinates_both_success)
            }
        }
    }

    private fun observeCoordinatesShareEvent(event: LocationViewModel.Event.CoordinatesShare) {
        when (event) {
            is LocationViewModel.Event.CoordinatesShare.Error -> {
                viewBinding.coordinatorLayout.showSnackbar(R.string.location_share_snackbar_failure)
            }
            is LocationViewModel.Event.CoordinatesShare.Success -> {
                startActivity(Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, event.coordinates)
                    type = "text/plain"
                })
            }
        }
    }

    private fun observeElevation(elevation: String) {
        viewBinding.elevationValueTextView.text = elevation
    }

    private fun observeElevationAccuracy(elevationAccuracy: String?) {
        viewBinding.elevationAccuracyValueTextView.text = elevationAccuracy
    }

    private fun observeEvent(event: LocationViewModel.Event) {
        if (event.handled) return
        event.handled = true
        when (event) {
            is LocationViewModel.Event.CoordinatesCopy ->
                observeCoordinatesCopyEvent(event)
            is LocationViewModel.Event.CoordinatesShare ->
                observeCoordinatesShareEvent(event)
            is LocationViewModel.Event.NavigateToLocationHelp ->
                observeNavigateToLocationHelp()
            is LocationViewModel.Event.ScreenLock ->
                observeScreenLockEvent(event)
        }
    }

    private fun observeNavigateToLocationHelp() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        findNavController().navigate(R.id.help)
    }

    private fun observeScreenLockState(screenLockState: LocationViewModel.ScreenLockState) {
        with(viewBinding.screenLockButton) {
            icon = screenLockState.icon
            contentDescription = screenLockState.contentDescription
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                tooltipText = screenLockState.tooltip
            }
        }
        activity?.window?.apply {
            if (screenLockState.locked) addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun observeScreenLockEvent(event: LocationViewModel.Event.ScreenLock) {
        viewBinding.coordinatorLayout.showSnackbar(
                if (event.locked) R.string.location_snackbar_screen_locked
                else R.string.location_snackbar_screen_unlocked
        )
    }

    private fun observeSpeed(speed: String) {
        viewBinding.speedValueTextView.text = speed
    }

    private fun observeSpeedAccuracy(speedAccuracy: String?) {
        viewBinding.speedAccuracyValueTextView.text = speedAccuracy
    }

    private fun observeUpdatedAt(updatedAt: String) {
        viewBinding.updatedAtTextView.text = updatedAt
    }

    sealed class Event {
        object CopyClick : Event()
        object HelpClick : Event()
        object ScreenLockClick : Event()
        object ShareClick : Event()
    }
}
