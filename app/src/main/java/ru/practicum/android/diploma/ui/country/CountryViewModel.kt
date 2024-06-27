package ru.practicum.android.diploma.ui.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.country.CountryInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.util.SearchResultData

class CountryViewModel(private val countryInteractor: CountryInteractor) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>> = _countries

    private val _countryState = MutableLiveData<CountryState>()
    val countryState: LiveData<CountryState> = _countryState

    fun fetchCountries() {
        _countryState.value = CountryState.Loading
        viewModelScope.launch {
            countryInteractor.getCountries().collect { result: SearchResultData<List<Country>> ->
                when (result) {
                    is SearchResultData.Data -> {
                        _countries.value = result.value
                        _countryState.value = CountryState.Content(result.value)
                    }

                    is SearchResultData.Error -> {
                        _countryState.value = CountryState.ServerError(result.message)
                    }

                    is SearchResultData.NoConnection -> {
                        _countryState.value = CountryState.NoConnection(result.message)
                    }

                    is SearchResultData.NotFound -> {
                        _countryState.value = CountryState.NotFound
                    }
                }
            }
        }
    }
}
