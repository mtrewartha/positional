package io.trewartha.positional.ui.compass

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.trewartha.positional.databinding.CompassFragmentBinding

class CompassFragment : Fragment() {

    private var _viewBinding: CompassFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private lateinit var viewModel: CompassViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(CompassViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _viewBinding = CompassFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            accelerometerAccuracy.observe(viewLifecycleOwner, ::observeAccelerometerAccuracy)
            missingSensorState.observe(viewLifecycleOwner, ::observeMissingSensorState)
            azimuth.observe(viewLifecycleOwner, ::observeAzimuth)
            compassRotation.observe(viewLifecycleOwner, ::observeCompassRotation)
            declination.observe(viewLifecycleOwner, ::observeDeclination)
            magnetometerAccuracy.observe(viewLifecycleOwner, ::observeMagnetometerAccuracy)
            mode.observe(viewLifecycleOwner, ::observeMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun observeAccelerometerAccuracy(accelerometerAccuracy: String) {
        viewBinding.accelerometerAccuracyTextView.text = accelerometerAccuracy
    }

    private fun observeAzimuth(azimuth: String) {
        with(viewBinding) {
            progressIndicator.hide()
            degreesTextView.text = azimuth
        }
    }

    private fun observeCompassRotation(compassRotation: Float) {
        viewBinding.compassBackgroundImageView.azimuth = compassRotation
    }

    private fun observeDeclination(declination: String) {
        viewBinding.declinationTextView.text = declination
    }

    private fun observeMagnetometerAccuracy(magnetometerAccuracy: String) {
        viewBinding.magnetometerAccuracyTextView.text = magnetometerAccuracy
    }

    private fun observeMissingSensorState(
            missingSensorState: CompassViewModel.MissingSensorState
    ) {
        with(viewBinding) {
            missingSensorLayout.visibility = View.VISIBLE
            missingSensorBodyTextView.text = missingSensorState.title
            missingSensorCaptionTextView.text = missingSensorState.body
        }
    }

    private fun observeMode(mode: String) {
        viewBinding.modeTextView.text = mode
    }
}