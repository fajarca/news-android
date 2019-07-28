package io.fajarca.news.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.fajarca.news.di.ViewModelFactory
import io.fajarca.news.di.ViewModelKey
import io.fajarca.news.viewmodel.NewsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory : ViewModelFactory) : ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: NewsViewModel): ViewModel


}