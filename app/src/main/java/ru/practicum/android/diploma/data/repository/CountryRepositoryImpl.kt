package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.db.converters.CountryDtoConverters
import ru.practicum.android.diploma.data.dto.CountriesRequest
import ru.practicum.android.diploma.data.dto.CountriesResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.country.CountryRepository
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.util.SearchResultData

class CountryRepositoryImpl(
    private val networkClient: NetworkClient,
    private val converter: CountryDtoConverters
) : CountryRepository {
    override suspend fun getCountries(): Flow<SearchResultData<List<Country>>> = flow {
        val response = networkClient.doRequest(CountriesRequest())
        when (response.resultCode) {
            CLIENT_SUCCESS_RESULT_CODE -> {
                val countryResponse = response as CountriesResponse
                val countries = countryResponse.list
                if (!countries.isNullOrEmpty()) {
                    emit(SearchResultData.Data(converter.mapToListCountries(countries)))
                } else {
                    emit(SearchResultData.Data(emptyList()))
                }
            }

            NO_INTERNET_RESULT_CODE -> {
                emit(SearchResultData.NoConnection(R.string.search_no_connection))
            }

            else -> {
                emit(SearchResultData.Error(R.string.search_server_error))
            }
        }
    }

    companion object {
        const val CLIENT_SUCCESS_RESULT_CODE = 200
        const val NO_INTERNET_RESULT_CODE = -1
    }
}
