package ru.practicum.android.diploma.domain.api.filtration

import ru.practicum.android.diploma.domain.models.Filtration

interface FiltrationRepository {
    suspend fun saveFiltration(filtration: Filtration?)
    suspend fun getFiltration(): Filtration?
}
