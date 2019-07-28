package io.fajarca.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.fajarca.news.common.UiState
import io.fajarca.news.db.entity.News
import io.fajarca.news.model.HeadlineArticle
import io.fajarca.news.model.NewsHeadlineResult
import io.fajarca.news.model.NewsSearchResult
import io.fajarca.news.repository.NewsRepository
import io.fajarca.news.util.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class NewsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    private val _headlines = MutableLiveData<List<HeadlineArticle>>()
    val headlines: LiveData<List<HeadlineArticle>>
        get() = _headlines


    private val queryLiveData = MutableLiveData<String>()

    //Map keyword string into NewsSearchResult
    val newsResult : LiveData<NewsSearchResult> = Transformations.map(queryLiveData, ::mapData)

    val news : LiveData<PagedList<News>> = Transformations.switchMap(newsResult) {
        it.data
    }

    val networkState : LiveData<UiState<List<News>>> = Transformations.switchMap(newsResult) {
        it.state
    }

    private fun mapData(keyword: String) : NewsSearchResult {
        return repository.search(keyword)
    }

    fun searchNews(keyword : String) {
        queryLiveData.postValue(keyword)
    }

    fun getHeadline(country: String, page: Int, pageSize: Int) {
        mCompositeDisposable += repository.getHeadline(country, page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { news -> onRetrieveHeadlineSuccessful(news) },
                        {}
                )


    }

    private fun onRetrieveHeadlineSuccessful(result: NewsHeadlineResult) {
        setHeadlines(result.articles)
    }


    override fun onCleared() {
        mCompositeDisposable.dispose()
        super.onCleared()
    }

    private fun setHeadlines(headlines: List<HeadlineArticle>) {
        _headlines.value = headlines
    }



}