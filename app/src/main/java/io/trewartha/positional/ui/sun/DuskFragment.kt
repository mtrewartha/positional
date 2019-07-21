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
import io.trewartha.positional.ui.MainViewModel
import io.trewartha.positional.ui.utils.DateTimeFormatter
import kotlinx.android.synthetic.main.dusk_fragment.*

class DuskFragment : Fragment() {

    private lateinit var dateTimeFormatter: DateTimeFormatter
    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        dateTimeFormatter = DateTimeFormatter(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dusk_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sunViewData.observe(this, Observer {
            dateTextView.text = dateTimeFormatter.getFormattedDate(it.astronomicalDusk)
            astronomicalValueTextView.text = dateTimeFormatter.getFormattedTime(it.astronomicalDusk)
            nauticalValueTextView.text = dateTimeFormatter.getFormattedTime(it.nauticalDusk)
            civilValueTextView.text = dateTimeFormatter.getFormattedTime(it.civilDusk)
            sunsetValueTextView.text = dateTimeFormatter.getFormattedTime(it.sunset)
        })
    }
}
