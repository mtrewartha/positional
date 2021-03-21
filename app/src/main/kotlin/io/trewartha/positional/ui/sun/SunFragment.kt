package io.trewartha.positional.ui.sun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import io.trewartha.positional.R
import io.trewartha.positional.databinding.SunFragmentBinding
import io.trewartha.positional.ui.location.coordinates.SunFragmentPagerAdapter

@AndroidEntryPoint
class SunFragment : Fragment() {

    private var _viewBinding: SunFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: SunViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = SunFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewPager.apply {
            val sunFragmentPagerAdapter = SunFragmentPagerAdapter(childFragmentManager)
            adapter = sunFragmentPagerAdapter
            offscreenPageLimit = sunFragmentPagerAdapter.count
        }
        viewModel.sunState.observe(viewLifecycleOwner, ::observeSunState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun observeSunState(sunState: SunViewModel.SunState) {
        viewBinding.updatedAtTextView.text = sunState.updatedAt
    }
}
