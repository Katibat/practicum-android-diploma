package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.data.dto.DTOConverters
import ru.practicum.android.diploma.data.repository.RegionRepositoryImpl
import ru.practicum.android.diploma.domain.api.region.RegionInteractor
import ru.practicum.android.diploma.domain.api.region.RegionRepository
import ru.practicum.android.diploma.domain.impl.region.RegionInteractorImpl
import ru.practicum.android.diploma.ui.region.RegionViewModel

val regionModule = module {
    single { DTOConverters() }
    single<RegionRepository> { RegionRepositoryImpl(get()) }
    factory<RegionInteractor> { RegionInteractorImpl(get()) }
    viewModel { RegionViewModel(get(), get()) }
}
