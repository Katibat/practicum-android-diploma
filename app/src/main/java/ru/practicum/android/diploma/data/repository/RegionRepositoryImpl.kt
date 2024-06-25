package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.db.converters.CountryDtoConverters
import ru.practicum.android.diploma.data.dto.CountriesRequest
import ru.practicum.android.diploma.data.dto.CountriesResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.region.RegionRepository
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.util.SearchResultData

class RegionRepositoryImpl(
    private val networkClient: NetworkClient
) : RegionRepository {
    private val countryDtoConverters = CountryDtoConverters()

    override suspend fun getRegions(countryId: String): Flow<SearchResultData<List<Region>>> = flow {
        val response = networkClient.doRequest(CountriesRequest())
        when (response.resultCode) {
            CLIENT_SUCCESS_RESULT_CODE -> {
                val countryResponse = response as CountriesResponse
                val regions = countryResponse.list
                if (!regions.isNullOrEmpty()) {
                    emit(SearchResultData.Data(countryDtoConverters.mapToListRegions(regions, countryId)))
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
