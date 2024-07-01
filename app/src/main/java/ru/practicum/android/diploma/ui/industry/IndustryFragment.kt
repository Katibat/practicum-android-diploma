package ru.practicum.android.diploma.ui.industry

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.ui.root.RootActivity

class IndustryFragment : Fragment() {
    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<IndustryViewModel>()
    private var selectedIndustry: Industry? = null
    private val adapter = IndustryAdapter { industry ->
        selectedIndustry = industry
        viewModel.saveSelectIndustry(industry)
        hideKeyboard()
    }

    private val toolbar by lazy { (requireActivity() as RootActivity).toolbar }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarSetup()
        viewModel.stateIndustry.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.selectIndustry.observe(viewLifecycleOwner) {
            renderSelected()
        }

        binding.rvIndustry.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIndustry.adapter = adapter

        viewModel.fetchIndustries()

        binding.etSelectIndustry.addTextChangedListener(textWatcherListener())

        binding.buttonSelectIndustry.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable(SELECTED_INDUSTRY_KEY, selectedIndustry)
            }
            Log.v("FILTRATION", "industry back $bundle")
            setFragmentResult(INDUSTRY_RESULT_KEY, bundle)
            findNavController().navigateUp()
        }

        binding.ivClear.setOnClickListener {
            with(binding) {
                etSelectIndustry.setText("")
                ivClear.setImageResource(R.drawable.search_icon)
            }
            adapter.industries.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun render(state: IndustryState) {
        when (state) {
            is IndustryState.Loading -> renderLoading()
            is IndustryState.NotFound -> renderNotFound()
            is IndustryState.ServerError -> renderServerError()
            is IndustryState.NoConnection -> renderNoConnection()
            is IndustryState.Content -> {
                renderContent()
                updateAdapterData(state.industries)
                selectIndustry(getIndustry())
            }
        }
    }

    private fun updateAdapterData(industries: List<Industry>) {
        adapter.industries.clear()
        adapter.industries = industries.toMutableList()
        adapter.notifyDataSetChanged()
    }

    private fun getIndustry(): Industry? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(SELECTED_INDUSTRY_KEY, Industry::class.java)
    } else {
        arguments?.getParcelable(SELECTED_INDUSTRY_KEY)
    }

    private fun selectIndustry(industry: Industry?) {
        if (industry != null) {
            renderSelected()
        } else {
            binding.buttonSelectIndustry.isVisible = false
        }
    }

    private fun renderSelected() {
        with(binding) {
            buttonSelectIndustry.isVisible = true
            progressBar.isVisible = false
        }
    }

    private fun renderLoading() {
        with(binding) {
            progressBar.isVisible = true
            buttonSelectIndustry.isVisible = false
        }
    }

    private fun renderNotFound() {
        with(binding) {
            rvIndustry.isVisible = false
            progressBar.isVisible = false
            buttonSelectIndustry.isVisible = false
            ivPlaceholder.isVisible = true
            ivPlaceholder.setImageResource(R.drawable.nothing_found_cat)
            tvPlaceholder.isVisible = true
            tvPlaceholder.setText(R.string.industry_not_found)
        }
    }

    private fun renderContent() {
        adapter.industries.clear()
        val list = if (viewModel.stateIndustry.value is IndustryState.Content) {
            (viewModel.stateIndustry.value as IndustryState.Content).industries
        } else {
            listOf()
        }
        adapter.industries.addAll(list)
        adapter.notifyDataSetChanged()
        with(binding) {
            rvIndustry.isVisible = true
            progressBar.isVisible = false
            ivPlaceholder.isVisible = false
            tvPlaceholder.isVisible = false
        }
    }

    private fun renderServerError() {
        with(binding) {
            rvIndustry.isVisible = false
            progressBar.isVisible = false
            ivPlaceholder.isVisible = true
            ivPlaceholder.setImageResource(R.drawable.server_error_cat)
            tvPlaceholder.isVisible = true
            tvPlaceholder.setText(R.string.search_server_error)
            buttonSelectIndustry.isVisible = false
        }
    }

    private fun renderNoConnection() {
        with(binding) {
            rvIndustry.isVisible = false
            progressBar.isVisible = false
            ivPlaceholder.isVisible = true
            ivPlaceholder.setImageResource(R.drawable.no_internet_scull)
            tvPlaceholder.isVisible = true
            tvPlaceholder.setText(R.string.search_no_connection)
            buttonSelectIndustry.isVisible = false
        }
    }

    private fun textWatcherListener() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.etSelectIndustry.text.toString().isNotEmpty()) {
                binding.ivClear.setImageResource(R.drawable.clean_icon_black)
                viewModel.searchDebounce(s.toString())
            } else {
                binding.ivClear.setImageResource(R.drawable.search_icon)
                hideKeyboard()
                viewModel.fetchIndustries()
            }
        }

        override fun afterTextChanged(p0: Editable?) = Unit
    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null) {
            inputMethodManager?.hideSoftInputFromWindow(binding.etSelectIndustry.windowToken, 0)
            binding.etSelectIndustry.isEnabled = true
        }
        view?.clearFocus()
    }

    override fun onStop() {
        super.onStop()
        toolbar.menu.findItem(R.id.share).isVisible = false
        toolbar.menu.findItem(R.id.favorite).isVisible = false
        toolbar.menu.findItem(R.id.filters).isVisible = false
    }

    private fun toolbarSetup() {
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        toolbar.title = getString(R.string.title_industry)
        toolbar.menu.findItem(R.id.share).isVisible = false
        toolbar.menu.findItem(R.id.favorite).isVisible = false
        toolbar.menu.findItem(R.id.filters).isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val INDUSTRY_RESULT_KEY = "industryResult"
        const val SELECTED_INDUSTRY_KEY = "selectedIndustry"
    }
}
