package ru.practicum.android.diploma.domain.impl.region

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.region.RegionInteractor
import ru.practicum.android.diploma.domain.api.region.RegionRepository
import ru.practicum.android.diploma.domain.models.Region
import ru.practicum.android.diploma.util.SearchResultData

class RegionInteractorImpl(private val repository: RegionRepository) : RegionInteractor {
    override suspend fun getRegions(countryId: String): Flow<SearchResultData<List<Region>>> {
        return repository.getRegions(countryId)
    }
}
