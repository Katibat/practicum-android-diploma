package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.db.converters.IndustryConverter
import ru.practicum.android.diploma.data.dto.IndustryRequest
import ru.practicum.android.diploma.data.dto.IndustryResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.industry.IndustryRepository
import ru.practicum.android.diploma.domain.models.Industry

class IndustryRepositoryImpl(
    private val client: NetworkClient,
    private val converter: IndustryConverter
) : IndustryRepository {

    override suspend fun getIndustries(industryId: String): Flow<Result<List<Industry>>> = flow {
        val response = client.doRequest(IndustryRequest(industryId))
        when (response.resultCode) {
            CLIENT_SUCCESS_RESULT_CODE -> {
                val list = (response as IndustryResponse).items
                if (list.isEmpty()) {
                    emit(Result.failure(Throwable(EMPTY_BODY_CODE.toString())))
                } else {
                    val industryList = converter.mapToList(list)
                    emit(Result.success(industryList))
                }
            }

            else -> {
                emit(Result.failure(Throwable(response.resultCode.toString())))
            }
        }
    }

    companion object {
        const val CLIENT_SUCCESS_RESULT_CODE = 200
        const val EMPTY_BODY_CODE = -1
    }
}
