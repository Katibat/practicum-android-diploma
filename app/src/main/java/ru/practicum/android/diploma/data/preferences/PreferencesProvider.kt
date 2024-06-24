package ru.practicum.android.diploma.data.preferences

import ru.practicum.android.diploma.domain.models.Filtration

interface PreferencesProvider {
    suspend fun saveFiltration(filtration: Filtration?)
    suspend fun getFiltration(): Filtration?
}
