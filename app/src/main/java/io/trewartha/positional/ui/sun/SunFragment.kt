package io.trewartha.positional.ui.sun

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.trewartha.positional.R
import io.trewartha.positional.location.LocationFormatter
import io.trewartha.positional.ui.MainViewModel
import io.trewartha.positional.ui.location.coordinates.SunFragmentPagerAdapter
import kotlinx.android.synthetic.main.sun_fragment.*

class SunFragment : Fragment() {

    private lateinit var locationFormatter: LocationFormatter
    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationFormatter = LocationFormatter(context)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
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
        viewModel.sunViewData.observe(this, Observer {
            updatedAtTextView.text = getString(
                R.string.location_updated_at,
                locationFormatter.getTimestamp(it.location)
            )
        })
    }
}
