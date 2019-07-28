package io.fajarca.news.repository

import android.util.Log
import androidx.paging.LivePagedListBuilder
import io.fajarca.news.api.NewsService
import io.fajarca.news.model.NewsBoundaryCallback
import io.fajarca.news.model.NewsSearchResult
import javax.inject.Inject

class NewsRepository @Inject constructor(private val localDataSource: NewsLocalDataSource, private val newsService: NewsService) {


    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

    /**
     * Always get the news from the DB. DB act as single source of truth
     * Get data from API only occured when database returned 0 items (onZeroItemsLoaded)
     * or
     * when all items in the database were loaded, we need to query the backend for more items (onItemAtEndLoaded)
     */
    fun search(keyword: String): NewsSearchResult {
        Log.v("Repository ", "New query : $keyword")


        // Create data source factory from the local db
        val dataSourceFactory = localDataSource.findNewsByKeyword(keyword)

        // Every new query creates a new BoundaryCallback
        // The BoundaryCallback will observe when the user reaches to the edges of
        // the list and update the database with extra data
        val boundaryCallback = NewsBoundaryCallback(keyword, newsService, localDataSource)

        val networkState = boundaryCallback.networkState

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()


        return NewsSearchResult(data, networkState)
    }

    fun getHeadline(country: String, page: Int, pageSize: Int) = newsService.getNewsHeadline(country, page, pageSize)

}