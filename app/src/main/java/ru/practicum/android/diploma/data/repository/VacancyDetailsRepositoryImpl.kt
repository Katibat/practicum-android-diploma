package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.db.converters.VacancyDtoConverter
import ru.practicum.android.diploma.data.dto.VacancyDetailsRequest
import ru.practicum.android.diploma.data.dto.VacancyDetailsResponse
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.details.VacancyDetailsRepository
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyDetailsRepositoryImpl(
    private val client: NetworkClient,
    private val converter: VacancyDtoConverter
) : VacancyDetailsRepository {

    override fun getVacancyDetails(vacancyId: String): Flow<Result<Vacancy>> = flow {
        val response = client.doRequest(VacancyDetailsRequest(vacancyId))
        when (response.resultCode) {
            SearchRepositoryImpl.CLIENT_SUCCESS_RESULT_CODE -> {
                val detailsResponse = response as VacancyDetailsResponse
                if (detailsResponse.vacancy == null) {
                    emit(Result.failure(Throwable(EMPTY_BODY_CODE.toString())))
                } else {
                    val vacancyDetails = converter.map(detailsResponse.vacancy)
                    emit(
                        Result.success(vacancyDetails)
                    )
                }
            }

            else -> {
                emit(Result.failure(Throwable(response.resultCode.toString())))
            }
        }
    }

    companion object {
        const val EMPTY_BODY_CODE = -1
    }
}
