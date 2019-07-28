package io.fajarca.news

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fajarca.news.di.component.DaggerAppComponent
import javax.inject.Inject

class AppController : Application(), HasActivityInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)


    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
