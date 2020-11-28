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
        viewModel.accelerometerAccuracy.observe(viewLifecycleOwner) {
            viewBinding.accelerometerAccuracyTextView.text = it
        }
        viewModel.missingSensorState.observe(viewLifecycleOwner) {
            viewBinding.missingSensorLayout.visibility = View.VISIBLE
            viewBinding.missingSensorBodyTextView.text = it.title
            viewBinding.missingSensorCaptionTextView.text = it.body
        }
        viewModel.azimuth.observe(viewLifecycleOwner) {
            if (it != null) {
                viewBinding.progressIndicator.hide()
                viewBinding.degreesTextView.text = it
            }
        }
        viewModel.compassRotation.observe(viewLifecycleOwner) {
            viewBinding.compassBackgroundImageView.rotation = it
        }
        viewModel.declination.observe(viewLifecycleOwner) {
            viewBinding.declinationTextView.text = it
        }
        viewModel.magnetometerAccuracy.observe(viewLifecycleOwner) {
            viewBinding.magnetometerAccuracyTextView.text = it
        }
        viewModel.mode.observe(viewLifecycleOwner) {
            viewBinding.modeTextView.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}