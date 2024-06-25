package ru.practicum.android.diploma.ui.filtration

import android.util.Log
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

    private fun saveStateToPrefs(filtrationToSave: Filtration) {
            filtrationInteractor.saveFiltration(filtrationToSave)
    }

    fun getFiltrationFromPrefs() {
            renderFiltration(filtrationInteractor.getFiltration())
        Log.v("FILTRATION", "after get filtr from prefs ${filtration.value} _filtr ${_filtration.value}")
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

    fun setSalary(salary: String?) {
        Log.v("FILTRATION", "salary $salary")
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
        Log.v("FILTRATION", "set industry ${filtration.value} _filtr ${_filtration.value}")
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
        Log.v("FILTRATION", "set industry ${filtration.value} _filtr ${_filtration.value}")
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

    private fun renderFiltration(renderFiltration: Filtration?) {
        Log.v("FILTRATION", "renderfiltration ${renderFiltration} _filtr ${_filtration.value}")
        if (renderFiltration != null) {
            _filtration.value = renderFiltration!!
            saveStateToPrefs(renderFiltration)
        } else {
            setEmpty()
        }
        Log.v("FILTRATION", "after renderfiltration ${renderFiltration} _filtr ${_filtration.value}")
    }

    fun setEmpty() {
        renderFiltration(Filtration(null, null, null, false))
    }

    companion object {
        private const val SALARY_DEBOUNCE = 2000L
    }
}
