package io.trewartha.positional.ui.compass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.trewartha.positional.R
import io.trewartha.positional.compass.CompassAccuracy
import io.trewartha.positional.compass.CompassMode
import kotlinx.android.synthetic.main.compass_fragment.*

class CompassFragment : Fragment() {

    private lateinit var viewModel: CompassViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(CompassViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.compass_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.readings.observe(viewLifecycleOwner, ::onDataReadings)
        viewModel.missingSensors.observe(viewLifecycleOwner, ::onDataMissingSensor)
    }

    private fun getAccuracyText(compassAccuracy: CompassAccuracy): String = getString(
            when (compassAccuracy) {
                CompassAccuracy.UNUSABLE -> R.string.compass_accuracy_no_contact
                CompassAccuracy.UNRELIABLE -> R.string.compass_accuracy_unreliable
                CompassAccuracy.LOW -> R.string.compass_accuracy_low
                CompassAccuracy.MEDIUM -> R.string.compass_accuracy_medium
                CompassAccuracy.HIGH -> R.string.compass_accuracy_high
            }
    )

    private fun onDataReadings(data: CompassViewModel.Data.Readings) {
        if (missingSensorLayout.visibility != View.GONE) missingSensorLayout.visibility = View.GONE
        compassBackgroundImageView.rotation = data.compassViewRotation
        degreesTextView.text = getString(R.string.compass_degrees, data.azimuth)
        accelerometerAccuracyTextView.text = getAccuracyText(data.accelerometerAccuracy)
        magnetometerAccuracyTextView.text = getAccuracyText(data.magnetometerAccuracy)
        modeTextView.text = getString(
                when (data.mode) {
                    CompassMode.MAGNETIC_NORTH -> R.string.compass_mode_magnetic_north
                    CompassMode.TRUE_NORTH -> R.string.compass_mode_true_north
                }
        )
        declinationTextView.text = getString(R.string.compass_declination, data.declination)
    }

    private fun onDataMissingSensor(data: CompassViewModel.Data.MissingSensor) {
        missingSensorLayout.visibility = View.VISIBLE
        missingSensorBodyTextView.text = data.bodyText
        missingSensorCaptionTextView.text = data.captionText
    }
}