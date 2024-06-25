package ru.practicum.android.diploma.domain.api.filtration

import ru.practicum.android.diploma.domain.models.Filtration

interface FiltrationRepository {
    fun saveFiltration(filtration: Filtration?)
    fun getFiltration(): Filtration?
}
