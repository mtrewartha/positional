package io.trewartha.positional.ui.location.help

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import io.noties.markwon.Markwon
import io.trewartha.positional.databinding.LocationHelpFragmentBinding

class LocationHelpFragment : Fragment() {

    private var _viewBinding: LocationHelpFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private lateinit var viewModel: LocationHelpViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(LocationHelpViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _viewBinding = LocationHelpFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        Markwon.create(requireContext()).setMarkdown(viewBinding.textView, viewModel.helpContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}