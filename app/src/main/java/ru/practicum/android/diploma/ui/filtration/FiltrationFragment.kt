package ru.practicum.android.diploma.ui.filtration

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFiltrationBinding
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filtration
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.ui.root.RootActivity

class FiltrationFragment : Fragment() {
    private var _binding: FragmentFiltrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FiltrationViewModel>()

    private val toolbar by lazy { (requireActivity() as RootActivity).toolbar }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFiltrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarSetup()
        viewModel.filtration.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.getFiltrationFromPrefs()
        var industry = getIndustry()
        if (industry != null) viewModel.setIndustry(industry)
        val country = getCountry()
        val region = getRegion()
        if (country != null) {
            val resultCountry = if (region != null) {
                Country(country.id, country.name, listOf(region))
            } else {
                Country(country.id, country.name, listOf())
            }
            viewModel.setArea(resultCountry)
        }
        binding.checkBoxSalary.setOnClickListener {
            viewModel.setCheckbox(binding.checkBoxSalary.isChecked)
        }
        viewModel.isChanged.observe(viewLifecycleOwner) {
            showSaveButton(it)
        }
        binding.buttonRemove.setOnClickListener {
            viewModel.setEmpty()
        }
        binding.buttonSave.setOnClickListener {
            val args = Bundle()
            args.putBoolean(IS_APPLY_KEY, false)
            setFragmentResult(FRAGMENT_RESULT_KEY, args)
            findNavController().navigateUp()
        }
        binding.etSalary.setOnKeyListener(onKeyListener())
        binding.etAreaOfWork.setOnClickListener { onAreaClick.invoke() }
        binding.etIndustry.setOnClickListener { onIndustryClick.invoke() }

    }

    private fun render(filtration: Filtration) {
        binding.apply {
            showArea(filtration.area)
            showIndustry(filtration.industry)
            if (!filtration.salary.isNullOrEmpty()) {
                etSalary.setText(filtration.salary)
            }
            checkBoxSalary.isChecked = filtration.onlyWithSalary
            buttonRemove.isVisible = true
        }
    }

    private fun showArea(area: Country?) {
        with(binding) {
            if (area != null) {
                var text = if (area.regions != null && area.regions.isNotEmpty()) {
                    area.name + ", " + area.regions[0].name
                } else {
                    area.name
                }
                etAreaOfWork.setText(text)
                ilAreaOfWork.setEndIconDrawable(R.drawable.clean_icon)
                ilAreaOfWork.setEndIconOnClickListener {
                    viewModel.setArea(null)
                    areaEndIconListener()
                }
            } else {
                etAreaOfWork.setText("")
                areaEndIconListener()
            }
        }
    }

    private fun showIndustry(industry: Industry?) {
        with(binding) {
            if (industry != null) {
                etIndustry.setText(industry.name)
                ilIndustry.setEndIconDrawable(R.drawable.clean_icon)
                ilIndustry.setEndIconOnClickListener {
                    viewModel.setIndustry(null)
                    industryEndIconListener()
                }
            } else {
                etIndustry.setText("")
                industryEndIconListener()
            }
        }
    }

    private fun showSaveButton(show: Boolean) {
        binding.buttonSave.isVisible = show
    }

    private fun industryEndIconListener() {
        binding.apply {
            etIndustry.setText("")
            ilIndustry.clearOnEndIconChangedListeners()
            ilIndustry.setEndIconDrawable(R.drawable.arrow_forward)
            etIndustry.setOnClickListener { onIndustryClick.invoke() }
            ilIndustry.setEndIconOnClickListener { onIndustryClick.invoke() }
        }
    }

    private fun areaEndIconListener() {
        binding.apply {
            etAreaOfWork.setText("")
            ilAreaOfWork.clearOnEndIconChangedListeners()
            ilAreaOfWork.setEndIconDrawable(R.drawable.arrow_forward)
            etAreaOfWork.setOnClickListener { onAreaClick.invoke() }
            ilAreaOfWork.setEndIconOnClickListener { onAreaClick.invoke() }
        }
    }

    private val onAreaClick: () -> Unit = {
        val country = viewModel.filtration.value?.area
        val args = Bundle()
        args.apply {
            if (country != null) {
                args.putParcelable(SELECTED_COUNTRY_KEY, country)
                if (country.regions.isNotEmpty()) {
                    args.putParcelable(SELECTED_REGION_KEY, country.regions[0])
                }
            }
        }
        findNavController().navigate(R.id.action_filtrationFragment_to_locationFragment, args)
    }

    private val onIndustryClick: () -> Unit = {
        val bundle = Bundle().apply {
            val selectedIndustry = viewModel.filtration.value?.industry
            putParcelable(SELECTED_INDUSTRY_KEY, selectedIndustry)
        }
        findNavController().navigate(R.id.action_filtrationFragment_to_industryFragment, bundle)
    }

    private fun getRegion(): Region? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(SELECTED_REGION_KEY, Region::class.java)
    } else {
        arguments?.getParcelable(SELECTED_REGION_KEY)
    }

    private fun getCountry(): Country? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(SELECTED_COUNTRY_KEY, Country::class.java)
    } else {
        arguments?.getParcelable(SELECTED_COUNTRY_KEY)
    }

    private fun getIndustry(): Industry? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arguments?.getParcelable(SELECTED_INDUSTRY_KEY, Industry::class.java)
    } else {
        arguments?.getParcelable(SELECTED_INDUSTRY_KEY)
    }

    private fun onKeyListener(): View.OnKeyListener? {
        return View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(binding.etSalary.windowToken, 0)
                viewModel.setSalary(binding.etSalary.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun toolbarSetup() {
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        toolbar.title = getString(R.string.title_filtration)
        toolbar.menu.findItem(R.id.share).isVisible = false
        toolbar.menu.findItem(R.id.favorite).isVisible = false
        toolbar.menu.findItem(R.id.filters).isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val FRAGMENT_RESULT_KEY = "fragmentResult"
        private const val IS_APPLY_KEY = "isApply"
        private const val SELECTED_COUNTRY_KEY = "selectedCountry"
        private const val SELECTED_REGION_KEY = "selectedRegion"
        private const val SELECTED_INDUSTRY_KEY = "selectedIndustry"
    }
}
