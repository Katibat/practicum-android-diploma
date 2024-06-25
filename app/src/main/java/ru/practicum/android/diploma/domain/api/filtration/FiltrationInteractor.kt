package ru.practicum.android.diploma.domain.api.filtration

import ru.practicum.android.diploma.domain.models.Filtration

interface FiltrationInteractor {
    fun saveFiltration(filtration: Filtration?)
    fun getFiltration(): Filtration?
}
