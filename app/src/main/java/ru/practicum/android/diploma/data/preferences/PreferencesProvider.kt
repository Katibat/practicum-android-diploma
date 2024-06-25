package ru.practicum.android.diploma.data.preferences

import ru.practicum.android.diploma.domain.models.Filtration

interface PreferencesProvider {
    fun saveFiltration(filtration: Filtration?)
    fun getFiltration(): Filtration?
}
