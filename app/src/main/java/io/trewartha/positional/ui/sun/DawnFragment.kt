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
import kotlinx.android.synthetic.main.dawn_fragment.*

class DawnFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var dateTimeFormatter: DateTimeFormatter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        dateTimeFormatter = DateTimeFormatter(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dawn_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.sunViewData.observe(this, Observer {
            dateTextView.text = dateTimeFormatter.getFormattedDate(it.astronomicalDawn)
            astronomicalValueTextView.text = dateTimeFormatter.getFormattedTime(it.astronomicalDawn)
            nauticalValueTextView.text = dateTimeFormatter.getFormattedTime(it.nauticalDawn)
            civilValueTextView.text = dateTimeFormatter.getFormattedTime(it.civilDawn)
            sunriseValueTextView.text = dateTimeFormatter.getFormattedTime(it.sunrise)
        })
    }
}
