package io.trewartha.positional.ui.sun

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.trewartha.positional.databinding.SunFragmentBinding
import io.trewartha.positional.ui.location.coordinates.SunFragmentPagerAdapter

class SunFragment : Fragment() {

    private var _viewBinding: SunFragmentBinding? = null
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
