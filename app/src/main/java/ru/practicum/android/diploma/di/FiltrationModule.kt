package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.data.repository.FiltrationRepositoryImpl
import ru.practicum.android.diploma.domain.api.filtration.FiltrationInteractor
import ru.practicum.android.diploma.domain.api.filtration.FiltrationRepository
import ru.practicum.android.diploma.domain.impl.filtration.FiltrationInteractorImpl
import ru.practicum.android.diploma.ui.filtration.FiltrationViewModel
import ru.practicum.android.diploma.ui.location.LocationViewModel

val filtrationModule = module {
    // interactor just factory, repository just single
    viewModel {
        LocationViewModel()
    }
    viewModel {
        FiltrationViewModel(get())
    }
    single<FiltrationRepository> { FiltrationRepositoryImpl(get()) }
    factory<FiltrationInteractor> { FiltrationInteractorImpl(get()) }
}
