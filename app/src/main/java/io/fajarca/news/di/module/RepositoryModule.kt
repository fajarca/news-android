package io.fajarca.news.di.module

import io.fajarca.news.repository.NewsLocalDataSource
import dagger.Module
import dagger.Provides
import io.fajarca.news.db.NewsDatabase
import io.fajarca.news.db.dao.NewsDao
import io.fajarca.news.api.NewsService
import io.fajarca.news.repository.NewsRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providesQuestionnaireDao(db: NewsDatabase) = db.newsDao()

    @Provides
    @Singleton
    fun providesNewsLocalDataSource(dao : NewsDao) = NewsLocalDataSource(dao)

    @Provides
    @Singleton
    fun providesNewsRepository(localDataSource : NewsLocalDataSource, newsService: NewsService) = NewsRepository(localDataSource, newsService)
}