package ru.practicum.android.diploma.domain.api.industry

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Industry

interface IndustryRepository {
    suspend fun getIndustries(): Flow<Result<List<Industry>>>
}
