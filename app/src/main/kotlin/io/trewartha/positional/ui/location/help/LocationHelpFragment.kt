package io.trewartha.positional.ui.location.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.trewartha.positional.R
import io.trewartha.positional.databinding.LocationHelpFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class LocationHelpFragment : Fragment() {

    @Inject
    lateinit var markwon: Markwon

    private var _viewBinding: LocationHelpFragmentBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: LocationHelpViewModel by hiltNavGraphViewModels(R.id.nav_graph)

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

        markwon.setMarkdown(viewBinding.textView, viewModel.helpContent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}