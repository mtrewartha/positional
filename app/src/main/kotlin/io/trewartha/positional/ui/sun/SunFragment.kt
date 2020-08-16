package io.trewartha.positional.ui.sun

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import io.trewartha.positional.R
import io.trewartha.positional.ui.location.coordinates.SunFragmentPagerAdapter
import kotlinx.android.synthetic.main.sun_fragment.*

class SunFragment : Fragment() {

    private lateinit var viewModel: SunViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(SunViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sun_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            val sunFragmentPagerAdapter = SunFragmentPagerAdapter(childFragmentManager)
            adapter = sunFragmentPagerAdapter
            offscreenPageLimit = sunFragmentPagerAdapter.count
        }

        // ViewModel data
        viewModel.sunData.observe(viewLifecycleOwner, ::handleSunData)
    }

    private fun handleSunData(data: SunViewModel.SunData) {
        updatedAtTextView.text = data.updatedAt
    }
}
