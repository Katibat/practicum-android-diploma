package ru.practicum.android.diploma.ui.country

import androidx.annotation.StringRes
import ru.practicum.android.diploma.domain.models.Country

sealed interface CountryState {
    object Loading : CountryState
    object NotFound : CountryState
    data class Content(val countries: List<Country>) : CountryState
    data class ServerError(@StringRes val message: Int) : CountryState
    data class NoConnection(@StringRes val message: Int) : CountryState
}
