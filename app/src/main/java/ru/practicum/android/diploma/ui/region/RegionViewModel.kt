package ru.practicum.android.diploma.ui.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.api.country.CountryInteractor
import ru.practicum.android.diploma.domain.api.region.RegionInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.util.SearchResultData

class RegionViewModel(
    private val regionInteractor: RegionInteractor,
    private val countryInteractor: CountryInteractor
) : ViewModel() {

    private val _regionState = MutableLiveData<RegionState>()
    val regionState: LiveData<RegionState> = _regionState

    fun fetchRegions(countryId: String) {
        _regionState.value = RegionState.Loading
        viewModelScope.launch {
            var countryList: MutableList<Country> = mutableListOf()
            countryInteractor.getCountries().collect {
                when (it) {
                    is SearchResultData.Data -> {
                        countryList.addAll(it.value)
                    }

                    is SearchResultData.Error -> {
                        _regionState.value = RegionState.ServerError(R.string.search_server_error)
                    }

                    is SearchResultData.NoConnection -> {
                        _regionState.value = RegionState.NoConnection(R.string.cant_find_list)
                    }

                    is SearchResultData.NotFound -> {
                        _regionState.value = RegionState.NotFound
                    }
                }
            }
            regionInteractor.getRegions(countryId).collect { result: SearchResultData<List<Region>> ->
                when (result) {
                    is SearchResultData.Data -> {
                        _regionState.value = RegionState.Content(result.value, countryList)
                    }

                    is SearchResultData.Error -> {
                        _regionState.value = RegionState.ServerError(R.string.search_server_error)
                    }

                    is SearchResultData.NoConnection -> {
                        _regionState.value = RegionState.NoConnection(R.string.cant_find_list)
                    }

                    is SearchResultData.NotFound -> {
                        _regionState.value = RegionState.NotFound
                    }
                }
            }
        }
    }
}
