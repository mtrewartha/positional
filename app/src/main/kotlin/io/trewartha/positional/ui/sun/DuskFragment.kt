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
import kotlinx.android.synthetic.main.dusk_fragment.*

class DuskFragment : Fragment() {

    private lateinit var viewModel: SunViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(SunViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dusk_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sunData.observe(viewLifecycleOwner, ::handleSunData)
    }

    private fun handleSunData(data: SunViewModel.SunData) {
        dateTextView.text = data.date
        astronomicalValueTextView.text = data.astronomicalDusk
        nauticalValueTextView.text = data.nauticalDusk
        civilValueTextView.text = data.civilDusk
        sunsetValueTextView.text = data.sunset
    }
}
