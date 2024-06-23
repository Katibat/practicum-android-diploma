package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.DTOConverters
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.country.CountryRepository
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.util.SearchResultData

class CountryRepositoryImpl(
    private val networkClient: NetworkClient,
    private val dtoConverters: DTOConverters
) : CountryRepository {
    override suspend fun getCountries(): Flow<SearchResultData<List<Country>>> = flow {
        val response = networkClient.getCountries()
        val data = response?.getOrNull()
        val error = response?.exceptionOrNull()
        when {
            data != null -> {
                emit(SearchResultData.Data(dtoConverters.mapToListCountries(data)))
            }

            error is HttpException -> {
                emit(SearchResultData.Error(R.string.search_server_error))
            }

            else -> {
                emit(SearchResultData.NoConnection(R.string.search_no_connection))
            }
        }
    }
}
