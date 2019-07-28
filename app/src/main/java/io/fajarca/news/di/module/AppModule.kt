package io.fajarca.news.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import io.fajarca.news.util.DATABASE_NAME
import dagger.Module
import dagger.Provides
import io.fajarca.news.AppController
import io.fajarca.news.db.NewsDatabase
import io.fajarca.news.api.NewsService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AppModule {


    @Provides
    @Singleton
    fun provideContext(app: AppController) : Context = app

    @Provides
    @Singleton
    fun provideApplications(app : AppController) : Application = app

    @Provides
    @Singleton
    fun provideDatabase(context: Context) = Room.databaseBuilder(context, NewsDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(NewsService::class.java)

}