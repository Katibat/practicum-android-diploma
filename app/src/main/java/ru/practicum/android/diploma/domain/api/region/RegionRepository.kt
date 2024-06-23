package ru.practicum.android.diploma.domain.api.region

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.util.SearchResultData

interface RegionRepository {
    suspend fun getRegions(countryId: String): Flow<SearchResultData<List<Region>>>
}
