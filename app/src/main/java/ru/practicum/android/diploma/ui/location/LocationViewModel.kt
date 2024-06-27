package ru.practicum.android.diploma.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Region

class LocationViewModel : ViewModel() {
    private val _selectedCountry = MutableLiveData<Country?>(null)
    private val _selectedRegion = MutableLiveData<Region?>(null)
    val selectedCountry: LiveData<Country?> get() = _selectedCountry
    val selectedRegion: LiveData<Region?> get() = _selectedRegion

    fun setCountry(country: Country?) {
        _selectedCountry.postValue(country)
    }

    fun setRegion(region: Region?) {
        _selectedRegion.postValue(region)
    }
}
