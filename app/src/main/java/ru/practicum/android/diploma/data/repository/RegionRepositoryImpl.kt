package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.DTOConverters
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.region.RegionRepository
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.util.SearchResultData

class RegionRepositoryImpl(private val networkClient: NetworkClient) : RegionRepository {
    private val dtoConverters = DTOConverters()

    override suspend fun getRegions(countryId: String): Flow<SearchResultData<List<Region>>> = flow {
        val response = networkClient.getCountries()
        val data = response?.getOrNull()
        val error = response?.exceptionOrNull()
        when {
            data != null -> {
                emit(SearchResultData.Data(dtoConverters.mapToListRegions(data, countryId)))
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
