package ru.practicum.android.diploma.domain.impl.filtration

import ru.practicum.android.diploma.domain.api.filtration.FiltrationInteractor
import ru.practicum.android.diploma.domain.api.filtration.FiltrationRepository
import ru.practicum.android.diploma.domain.models.Filtration

class FiltrationInteractorImpl(private val filtrationRepository: FiltrationRepository) : FiltrationInteractor {
    override suspend fun saveFiltration(filtration: Filtration?) {
        filtrationRepository.saveFiltration(filtration)
    }

    override suspend fun getFiltration(): Filtration? {
        return filtrationRepository.getFiltration()
    }
}
