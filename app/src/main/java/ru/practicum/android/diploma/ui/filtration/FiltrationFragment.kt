package ru.practicum.android.diploma.ui.filtration

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
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
        setFragmentResultListener(INDUSTRY_RESULT_KEY) { requestKey, bundle ->
            var industry = getIndustry(bundle)
            if (industry != null) viewModel.setIndustry(industry)
            Log.v("FILTRATION", "fragment view created after set industry from bundle $industry")
        }
        setFragmentResultListener(REGI0N_RESULT_KEY) { requestKey, bundle ->
            val country = getCountry(bundle)
            val region = getRegion(bundle)
            if (country != null) {
                val resultCountry = if (region != null) {
                    Country(country.id, country.name, listOf(region))
                } else {
                    Country(country.id, country.name, listOf())
                }
                viewModel.setArea(resultCountry)
            }
            Log.v("FILTRATION", "fragment view created after set area from bundle $country")
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
            args.putBoolean(IS_APPLY_KEY, true)
            setFragmentResult(FRAGMENT_RESULT_KEY, args)
            findNavController().navigateUp()
        }
        binding.etSalary.addTextChangedListener(textWatcher)
        binding.ilSalary.setEndIconOnClickListener {
            viewModel.setSalary(null)
        }
        binding.ilSalary.isEndIconVisible = false
        binding.etSalary.doOnTextChanged { text, start, before, count ->
            binding.ilSalary.isEndIconVisible = !text.isNullOrEmpty()
        }
        binding.etAreaOfWork.setOnClickListener { onAreaClick.invoke() }
        binding.etIndustry.setOnClickListener { onIndustryClick.invoke() }
    }

    private fun render(filtration: Filtration) {
        binding.apply {
            showArea(filtration.area)
            showIndustry(filtration.industry)
            binding.etSalary.removeTextChangedListener(textWatcher)
            renderSalary(filtration.salary)
            binding.etSalary.addTextChangedListener(textWatcher)
            checkBoxSalary.isChecked = filtration.onlyWithSalary
            buttonRemove.isVisible = true
            val checkEmpty =
                filtration.salary.isNullOrEmpty() && filtration.industry == null && filtration.area == null
            buttonRemove.isVisible = !(checkEmpty && !filtration.onlyWithSalary)
        }
    }

    private fun renderSalary(salary: String?) {
        val currentSelection = binding.etSalary.selectionEnd
        binding.etSalary.setText(salary)
        if (binding.etSalary.hasFocus()) {
            if (salary.isNullOrEmpty()) {
                binding.etSalary.setSelection(0)
            } else {
                if (currentSelection > 0 && currentSelection < salary.length) {
                    binding.etSalary.setSelection(currentSelection)
                } else {
                    binding.etSalary.setSelection(salary.length)
                }
            }
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
            ilIndustry.clearOnEndIconChangedListeners()
            ilIndustry.setEndIconDrawable(R.drawable.arrow_forward)
            etIndustry.setOnClickListener { onIndustryClick.invoke() }
            ilIndustry.setEndIconOnClickListener { onIndustryClick.invoke() }
        }
    }

    private fun areaEndIconListener() {
        binding.apply {
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

    private fun getRegion(bundle: Bundle): Region? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getParcelable(SELECTED_REGION_KEY, Region::class.java)
    } else {
        bundle.getParcelable(SELECTED_REGION_KEY)
    }

    private fun getCountry(bundle: Bundle): Country? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getParcelable(SELECTED_COUNTRY_KEY, Country::class.java)
    } else {
        bundle.getParcelable(SELECTED_COUNTRY_KEY)
    }

    private fun getIndustry(bundle: Bundle): Industry? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        bundle.getParcelable(SELECTED_INDUSTRY_KEY, Industry::class.java)
    } else {
        bundle.getParcelable(SELECTED_INDUSTRY_KEY)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Empty
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!s.isNullOrEmpty()) {
                viewModel.setSalary(s.toString())
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            // Empty
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
        private const val REGI0N_RESULT_KEY = "regionResult"
        private const val INDUSTRY_RESULT_KEY = "industryResult"
        private const val FRAGMENT_RESULT_KEY = "fragmentResult"
        private const val IS_APPLY_KEY = "isApply"
        private const val SELECTED_COUNTRY_KEY = "selectedCountry"
        private const val SELECTED_REGION_KEY = "selectedRegion"
        private const val SELECTED_INDUSTRY_KEY = "selectedIndustry"
    }
}
