package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.ui.location.LocationViewModel

val filtrationModule = module {
    // interactor just factory, repository just single
    viewModel {
        LocationViewModel()
    }
}