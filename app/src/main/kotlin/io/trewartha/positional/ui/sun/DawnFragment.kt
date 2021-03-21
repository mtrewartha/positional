package io.trewartha.positional.ui.sun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import io.trewartha.positional.R
import io.trewartha.positional.databinding.DawnFragmentBinding

@AndroidEntryPoint
class DawnFragment : Fragment() {

    private var _viewBinding: DawnFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: SunViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = DawnFragmentBinding.inflate(inflater, container, false)
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
            astronomicalValueTextView.text = sunState.astronomicalDawn
            nauticalValueTextView.text = sunState.nauticalDawn
            civilValueTextView.text = sunState.civilDawn
            sunriseValueTextView.text = sunState.sunrise
        }
    }
}
