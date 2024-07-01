package ru.practicum.android.diploma.ui.filtration


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.api.filtration.FiltrationInteractor
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filtration
import ru.practicum.android.diploma.domain.models.Industry

class FiltrationViewModel(private val filtrationInteractor: FiltrationInteractor) : ViewModel() {
    private val _filtration = MutableLiveData(Filtration(null, null, null, false))
    val filtration: LiveData<Filtration> get() = _filtration
    private val _isChanged = MutableLiveData(false)
    val isChanged: LiveData<Boolean> get() = _isChanged
    private var loadedFiltration: Filtration? = Filtration(null, null, null, false)
    private fun saveStateToPrefs(filtrationToSave: Filtration) {
        filtrationInteractor.saveFiltration(filtrationToSave)
    }

    fun getFiltrationFromPrefs() {
        loadedFiltration = filtrationInteractor.getFiltration() ?: Filtration(null, null, null, false)
        renderFiltration(loadedFiltration)
    }

    fun setCheckbox(onlyWithSalary: Boolean) {
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
        if (renderFiltration != null) {
            _filtration.value = renderFiltration!!
            saveStateToPrefs(renderFiltration)
        } else {
            setEmpty()
        }
        _isChanged.value = isFiltrationChanged()
    }

    fun setEmpty() {
        renderFiltration(Filtration(null, null, null, false))
    }

    fun isFiltrationChanged(): Boolean {
        val currentValue = _filtration.value ?: Filtration(null, null, null, false)
        val currArea = currentValue.area?.id
        val loadArea = loadedFiltration?.area?.id
        val currCountry =
            if (!currentValue.area?.regions.isNullOrEmpty()) currentValue.area?.regions?.get(0)?.id else null
        val loadCountry =
            if (!loadedFiltration?.area?.regions.isNullOrEmpty()) loadedFiltration?.area?.regions?.get(0)?.id else null
        var notChanged = (currentValue.onlyWithSalary == loadedFiltration?.onlyWithSalary)
            && (currentValue.salary == loadedFiltration?.salary)
            && (currentValue.industry?.id == loadedFiltration!!.industry?.id)
            && (currArea == loadArea)
            && (currCountry == loadCountry)
        return !notChanged
    }

    companion object {
        private const val SALARY_DEBOUNCE = 2000L
    }
}
