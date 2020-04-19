package io.trewartha.positional.ui.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import io.trewartha.positional.R
import io.trewartha.positional.ui.MainViewModel
import kotlinx.android.synthetic.main.location_fragment.*

class LocationFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.location_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java).apply {
            locationLiveData.observe(viewLifecycleOwner) { onLocationObserved(it) }
            screenLockLiveData.observe(viewLifecycleOwner) { onScreenLockObserved(it) }
        }

        copyButton.setOnClickListener { onCopyClick() }
        screenLockButton.setOnClickListener { onScreenLockClick() }
        shareButton.setOnClickListener { onShareClick() }
    }

    private fun haveLocationPermissions(): Boolean =
            checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                    && checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    private fun lockScreen(lock: Boolean) {
        activity?.window?.apply {
            if (lock)
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else
                clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onCopyClick() {
        val coordinates = viewModel.locationLiveData.value?.coordinates
        if (coordinates == null) {
            Snackbar.make(
                    coordinatorLayout,
                    R.string.location_copied_coordinates_failure,
                    Snackbar.LENGTH_LONG
            ).show()
            return
        }

        val clipboardManager = requireContext()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboardManager.setPrimaryClip(
                ClipData.newPlainText(
                        requireContext().getString(R.string.location_copied_coordinates_label),
                        coordinates
                )
        )

        Snackbar.make(
                coordinatorLayout,
                requireContext().getString(R.string.location_copied_coordinates_both_success),
                Snackbar.LENGTH_LONG
        ).show()
    }

    private fun onLocationObserved(locationViewData: MainViewModel.LocationViewData) {
        coordinatesTextView.apply {
            maxLines = locationViewData.coordinatesLines
            text = locationViewData.coordinates
        }
        accuracyValueTextView.text = locationViewData.accuracy
        bearingValueTextView.text = locationViewData.bearing
        elevationValueTextView.text = locationViewData.elevation
        speedValueTextView.text = locationViewData.speed
        updatedAtTextView.text = locationViewData.updatedAt
    }

    private fun onScreenLockClick() {
        viewModel.toggleScreenLock()
    }

    private fun onScreenLockObserved(screenLock: Boolean?) {
        lockScreen(screenLock ?: return)
        screenLockButton.setIconResource(
                if (screenLock) R.drawable.ic_twotone_smartphone_24px
                else R.drawable.ic_twotone_screen_lock_portrait_24px
        )
    }

    private fun onShareClick() {
        val coordinates = viewModel.locationLiveData.value?.coordinates
        if (coordinates == null) {
            Snackbar.make(
                    coordinatorLayout,
                    R.string.location_share_snackbar_failure,
                    Snackbar.LENGTH_LONG
            ).show()
            return
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, coordinates)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }
}
