package io.fajarca.news.model

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.fajarca.news.common.UiState
import io.fajarca.news.api.NewsService
import io.fajarca.news.db.entity.News
import io.fajarca.news.repository.NewsLocalDataSource
import io.reactivex.schedulers.Schedulers
import java.io.IOException


/**
 * This boundary callback gets notified when user reaches to the edges of the list for example when
 * the database cannot provide any more data.
 **/
class NewsBoundaryCallback(
    private val query: String,
    private val service: NewsService,
    private val localDataSource: NewsLocalDataSource
) : PagedList.BoundaryCallback<News>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
        private const val SORT_BY_PUBLISHED_AT = "publishedAt"
    }


    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1


    private val _networkState = MutableLiveData<UiState<List<News>>>()


    //Livedata of network state
    val networkState: LiveData<UiState<List<News>>>
        get() = _networkState

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false


    /**
     * Database returned 0 items.  Query the backend for more items.
     */
    override fun onZeroItemsLoaded() {
        Log.v("RepoBoundaryCallback", "onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    /**
     * When all items in the database were loaded, we need to query the backend for more items.
     */
    override fun onItemAtEndLoaded(itemAtEnd: News) {
        Log.v("RepoBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData(query)
    }


    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        searchNews(
                query,
                lastRequestedPage,
                NETWORK_PAGE_SIZE,
                { news -> onRetrieveNewsSuccess(news) },
                { error -> onRetrieveNewsFailed(error) }
        )
    }

    @SuppressLint("CheckResult")
    fun searchNews(query: String,
                   page: Int,
                   pageSize: Int,
                   onSuccess: (news: List<News>) -> Unit,
                   onError: (throwable : Throwable) -> Unit) {

        setNetworkState(UiState.Loading())

        service.getEverything(query, SORT_BY_PUBLISHED_AT, page, pageSize)
                .map { mapData(it.articles) }
                .subscribeOn((Schedulers.io()))
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            Log.v("NewsBoundaryCallback", "Success get ${it.size} news from API")
                            onSuccess(it)
                        },
                        {
                            Log.v("NewsBoundaryCallback", "Failed to get data from API $it")
                            onError(it)
                        }
                )

    }


    private fun mapData(result: List<ApiNews>?): List<News> {
        if (result != null) {
            val newsList = mutableListOf<News>()

            for (i in result) {
                newsList.add(News(i.title!!, i.urlToImage, i.description, i.publishedAt))
            }

            return newsList
        }

        return listOf()
    }

    /**
     * Store the data to db when api return new data
     */
    private fun onRetrieveNewsSuccess(news: List<News>) {
        localDataSource.insert(news) {
            //On insert to db success
            lastRequestedPage++
            isRequestInProgress = false
        }
        isRequestInProgress = false

        if (news.isEmpty()) setNetworkState(UiState.NoData()) else setNetworkState(UiState.Success())

    }

    private fun onRetrieveNewsFailed(throwable: Throwable) {

        if (throwable is IOException) {
            setNetworkState(UiState.NoInternetConnection())
        } else {
            setNetworkState(UiState.Error(throwable.message ?: "Unknown error. Please try again"))
        }

        isRequestInProgress = false
    }


    private fun setNetworkState(networkState: UiState<List<News>>) {
        _networkState.postValue(networkState)
    }
}