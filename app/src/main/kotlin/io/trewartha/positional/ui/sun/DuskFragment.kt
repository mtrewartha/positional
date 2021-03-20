package io.trewartha.positional.ui.sun

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.trewartha.positional.databinding.DuskFragmentBinding

class DuskFragment : Fragment() {

    private var _viewBinding: DuskFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private lateinit var viewModel: SunViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(SunViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = DuskFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sunState.observe(viewLifecycleOwner, ::observeSunState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun observeSunState(sunState: SunViewModel.SunState) {
        with(viewBinding) {
            progressIndicator.animate()
                .alpha(0f)
                .withEndAction { progressIndicator.visibility = View.GONE }
            dateTextView.text = sunState.date
            astronomicalValueTextView.text = sunState.astronomicalDusk
            nauticalValueTextView.text = sunState.nauticalDusk
            civilValueTextView.text = sunState.civilDusk
            sunsetValueTextView.text = sunState.sunset
        }
    }
}
