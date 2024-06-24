package ru.practicum.android.diploma.ui.filtration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.filtration.FiltrationInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filtration
import ru.practicum.android.diploma.domain.models.Industry

class FiltrationViewModel(private val filtrationInteractor: FiltrationInteractor) : ViewModel() {
    private val _filtration = MutableLiveData<Filtration>()
    val filtration: LiveData<Filtration> get() = _filtration
    private val _isChanged = MutableLiveData<Boolean>(false)
    val isChanged: LiveData<Boolean> get() = _isChanged

    init {
        getFiltrationFromPrefs()
    }

    private fun saveStateToPrefs() {
        viewModelScope.launch {
            filtrationInteractor.saveFiltration(filtration.value)
        }
    }

    fun getFiltrationFromPrefs() {
        viewModelScope.launch {
            renderFiltration(filtrationInteractor.getFiltration())
        }
    }

    fun setCheckbox(onlyWithSalary: Boolean) {
        _isChanged.value = true
        renderFiltration(
            Filtration(
                area = filtration.value?.area,
                industry = filtration.value?.industry,
                salary = filtration.value?.salary,
                onlyWithSalary = onlyWithSalary
            )
        )
    }


    fun setSalary(salary: String) {
        _isChanged.value = true
        renderFiltration(
            Filtration(
                area = filtration.value?.area,
                industry = filtration.value?.industry,
                salary = salary,
                onlyWithSalary = filtration.value?.onlyWithSalary ?: false
            )
        )
    }

    fun setArea(area: Country?) {
        _isChanged.value = true
        renderFiltration(
            Filtration(
                area = area,
                industry = filtration.value?.industry,
                salary = filtration.value?.salary,
                onlyWithSalary = filtration.value?.onlyWithSalary ?: false
            )
        )
    }

    fun setIndustry(industry: Industry?) {
        _isChanged.value = true
        renderFiltration(
            Filtration(
                area = filtration.value?.area,
                industry = industry,
                salary = filtration.value?.salary,
                onlyWithSalary = filtration.value?.onlyWithSalary ?: false
            )
        )
    }

    private fun renderFiltration(filtration: Filtration?) {
        if (filtration != null) {
            _filtration.postValue(filtration!!)
        } else {
            setEmpty()
        }
    }

    private fun isEmpty(filtration: Filtration): Boolean {
        val checkFiltrationNull = filtration.industry == null && filtration.area == null
        return checkFiltrationNull && filtration.salary.isNullOrEmpty() && !filtration.onlyWithSalary
    }

    fun setEmpty() {
        renderFiltration(Filtration(null, null, null, false))
    }


    companion object {
        private const val SALARY_DEBOUNCE = 2000L
    }
}
