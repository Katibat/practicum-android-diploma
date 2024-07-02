package ru.practicum.android.diploma.ui.location

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentLocationBinding
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.ui.filtration.FiltrationFragment
import ru.practicum.android.diploma.ui.filtration.FiltrationFragment.Companion
import ru.practicum.android.diploma.ui.root.RootActivity

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val toolbar by lazy { (requireActivity() as RootActivity).toolbar }
    private val viewModel by viewModel<LocationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarSetup()
        viewModel.selectedCountry.observe(viewLifecycleOwner) {
            renderCountryField(it)
        }
        viewModel.selectedRegion.observe(viewLifecycleOwner) {
            renderRegionField(it)
        }
        val selectedCountry = arguments?.let { getCountryFromBundle(it) }
        viewModel.setCountry(selectedCountry)
        Log.v("LOCATION", "country $selectedCountry")
        val selectedRegion = arguments?.let { getRegionFromBundle(it) }
        viewModel.setRegion(selectedRegion)
        Log.v("LOCATION", "region $selectedRegion")
        setFragmentResultListener(REGI0N_RESULT_KEY) { requestKey, bundle ->
            val country = getCountryFromBundle(bundle)
            val region = getRegionFromBundle(bundle)
            viewModel.setCountry(country)
            viewModel.setRegion(region)
        }
        setFragmentResultListener(COUNTRY_RESULT_KEY) { requestKey, bundle ->
            val country = getCountryFromBundle(bundle)
            val region = getRegionFromBundle(bundle)
            viewModel.setCountry(country)
            viewModel.setRegion(region)
        }
    }

    private fun getRegionFromBundle(bundle: Bundle): Region? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(SELECTED_REGION_KEY, Region::class.java)
        } else {
            bundle?.getParcelable(SELECTED_REGION_KEY)
        }

    private fun getCountryFromBundle(bundle: Bundle): Country? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(SELECTED_COUNTRY_KEY, Country::class.java)
        } else {
            bundle?.getParcelable(SELECTED_COUNTRY_KEY)
    }

    private fun renderRegionField(region: Region?) {
        if (region == null) {
            binding.apply {
                etRegion.setText("")
                tilRegion.isActivated = false
                tilRegion.setEndIconDrawable(R.drawable.arrow_forward)
                tilRegion.setEndIconOnClickListener(regionOnClickListener)
                etRegion.setOnClickListener(regionOnClickListener)
            }
        } else {
            binding.apply {
                etRegion.setText(region.name)
                tilRegion.isActivated = true
                tilRegion.setEndIconDrawable(R.drawable.clean_icon)
                etRegion.setOnClickListener(regionOnClickListener)
                tilRegion.setEndIconOnClickListener {
                    viewModel.setRegion(null)
                }
            }
        }
        setupSelectButton()
    }

    private fun renderCountryField(country: Country?) {
        if (country == null) {
            binding.apply {
                btnSelectionContainer.isVisible = false
                etCountry.setText("")
                tilCountry.isActivated = false
                tilCountry.setEndIconDrawable(R.drawable.arrow_forward)
                tilCountry.setEndIconOnClickListener {
                    val bundle = Bundle().apply {
                        if (viewModel.selectedCountry.value != null) {
                            putParcelable(SELECTED_COUNTRY_KEY, viewModel.selectedCountry.value)
                        }
                        if (viewModel.selectedRegion.value != null) {
                            putParcelable(SELECTED_REGION_KEY, viewModel.selectedRegion.value)
                        }
                    }
                    findNavController().navigate(R.id.action_locationFragment_to_countryFragment, bundle)
                }
                etCountry.setOnClickListener {
                    val bundle = Bundle().apply {
                        if (viewModel.selectedCountry.value != null) {
                            putParcelable(SELECTED_COUNTRY_KEY, viewModel.selectedCountry.value)
                        }
                        if (viewModel.selectedRegion.value != null) {
                            putParcelable(SELECTED_REGION_KEY, viewModel.selectedRegion.value)
                        }
                    }
                    findNavController().navigate(R.id.action_locationFragment_to_countryFragment, bundle)
                }
            }
        } else {
            binding.apply {
                btnSelectionContainer.isVisible = true
                etCountry.setText(country.name)
                tilCountry.isActivated = true
                tilCountry.setEndIconDrawable(R.drawable.clean_icon)
                etCountry.setOnClickListener {
                    val bundle = Bundle().apply {
                        if (viewModel.selectedCountry.value != null) {
                            putParcelable(SELECTED_COUNTRY_KEY, viewModel.selectedCountry.value)
                        }
                        if (viewModel.selectedRegion.value != null) {
                            putParcelable(SELECTED_REGION_KEY, viewModel.selectedRegion.value)
                        }
                    }
                    findNavController().navigate(R.id.action_locationFragment_to_countryFragment, bundle)
                }
                tilCountry.setEndIconOnClickListener {
                    viewModel.setCountry(null)
                    viewModel.setRegion(null)
                }
            }
        }
        setupSelectButton()
    }


    private fun updateClearButtonVisibility() {
        val isCountryEmpty = binding.etCountry.text.isNullOrEmpty()
        val isRegionEmpty = binding.etRegion.text.isNullOrEmpty()

        binding.tilCountry.setEndIconDrawable(if (isCountryEmpty) R.drawable.arrow_forward else R.drawable.clean_icon)
        binding.tilRegion.setEndIconDrawable(if (isRegionEmpty) R.drawable.arrow_forward else R.drawable.clean_icon)
        binding.btnSelectionContainer.visibility = if (isCountryEmpty) View.GONE else View.VISIBLE

    }

    private fun setupSelectButton() {
        binding.btnSelectionContainer.setOnClickListener {
            val bundle = Bundle().apply {
                if (viewModel.selectedCountry.value != null) {
                    putParcelable(SELECTED_COUNTRY_KEY, viewModel.selectedCountry.value)
                }
                if (viewModel.selectedRegion.value != null) {
                    putParcelable(SELECTED_REGION_KEY, viewModel.selectedRegion.value)
                }
            }
            setFragmentResult(LOCATION_RESULT_KEY, bundle)
            findNavController().navigateUp()
        }
    }

    private val regionOnClickListener = OnClickListener {
        val bundle = Bundle().apply {
            Log.v("LOCATION", "country ${viewModel.selectedCountry.value} region ${viewModel.selectedRegion.value}")
            if (viewModel.selectedCountry.value != null) {
                putParcelable(SELECTED_COUNTRY_KEY, viewModel.selectedCountry.value)
            }
            if (viewModel.selectedRegion.value != null) {
                putParcelable(SELECTED_REGION_KEY, viewModel.selectedRegion.value)
            }
        }
        findNavController().navigate(R.id.action_locationFragment_to_regionFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        updateClearButtonVisibility()
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

        toolbar.title = getString(R.string.pick_job_location)
        toolbar.menu.findItem(R.id.share).isVisible = false
        toolbar.menu.findItem(R.id.favorite).isVisible = false
        toolbar.menu.findItem(R.id.filters).isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SELECTED_COUNTRY_KEY = "selectedCountry"
        private const val SELECTED_REGION_KEY = "selectedRegion"
        private const val LOCATION_RESULT_KEY = "locationResult"
        private const val REGI0N_RESULT_KEY = "regionResult"
        private const val COUNTRY_RESULT_KEY = "countryResult"

    }
}
