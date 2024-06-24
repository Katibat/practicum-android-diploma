package ru.practicum.android.diploma.ui.industry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.api.industry.IndustryInteractor
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.ui.details.VacancyDetailsViewModel.Companion.NO_INTERNET_RESULT_CODE

class IndustryViewModel(private val interactor: IndustryInteractor) : ViewModel() {
    private val industriesList: MutableList<Industry> = mutableListOf()
    private var lastSearchQueryText: String? = null

    private val _stateIndustry = MutableLiveData<IndustryState>()
    val stateIndustry: LiveData<IndustryState> get() = _stateIndustry

    private val _selectIndustry = MutableLiveData<Industry>()
    val selectIndustry: LiveData<Industry> get() = _selectIndustry

    fun fetchIndustries() {
        _stateIndustry.postValue(IndustryState.Loading)
        viewModelScope.launch {
            interactor.getIndustries().collect { result ->
                industriesList.clear()
                result.onSuccess {
                    industriesList.addAll(it)
                    if (industriesList.isNotEmpty()) {
                        industriesList.sortBy { it.name }
                        renderState(IndustryState.Content(industriesList))
                    } else {
                        renderState(IndustryState.NotFound)
                    }
                }
                result.onFailure {
                    val resultCode = it.message
                    when (resultCode) {
                        NO_INTERNET_RESULT_CODE -> {
                            renderState(IndustryState.NoConnection(R.string.search_no_connection))
                        }
                        else -> {
                            renderState(IndustryState.ServerError(R.string.search_server_error))
                        }
                    }
                }
            }
        }
    }

    private fun search(s: String) {
        _stateIndustry.postValue(IndustryState.Loading)
        viewModelScope.launch {
            val filteredIndustry = industriesList.filter { it.name.contains(s, ignoreCase = true) }
            if (filteredIndustry.isEmpty()) {
                _stateIndustry.postValue(IndustryState.NotFound)
            } else {
                _stateIndustry.postValue(IndustryState.Content(filteredIndustry))
            }
        }
    }

    fun saveSelectIndustry(industry: Industry) {
        _selectIndustry.postValue(industry)
    }

    private fun renderState(state: IndustryState) {
        _stateIndustry.value = state
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchQueryText == changedText) return
        this.lastSearchQueryText = changedText
        viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            search(changedText)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}
