package io.fajarca.news.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.fajarca.news.ui.MainActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindMainActivity (): MainActivity
}