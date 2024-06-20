package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.data.dto.DTOConverters
import ru.practicum.android.diploma.data.repository.CountryRepositoryImpl
import ru.practicum.android.diploma.domain.api.country.CountryInteractor
import ru.practicum.android.diploma.domain.api.country.CountryRepository
import ru.practicum.android.diploma.domain.impl.country.CountryInteractorImpl
import ru.practicum.android.diploma.ui.country.CountryViewModel

val countryModule = module {
    single { DTOConverters() }
    single<CountryRepository> { CountryRepositoryImpl(get(), get()) }
    factory<CountryInteractor> { CountryInteractorImpl(get()) }
    viewModel { CountryViewModel(get()) }
}
