package ru.practicum.android.diploma.domain.impl.country

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.country.CountryInteractor
import ru.practicum.android.diploma.domain.api.country.CountryRepository
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.util.SearchResultData

class CountryInteractorImpl(private val repository: CountryRepository) : CountryInteractor {
    override suspend fun getCountries(): Flow<SearchResultData<List<Country>>> {
        return repository.getCountries()
    }
}
