package io.fajarca.news.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.fajarca.news.common.UiState
import io.fajarca.news.db.entity.News

data class NewsSearchResult (
        val data: LiveData<PagedList<io.fajarca.news.db.entity.News>>,
        val state: LiveData<UiState<List<News>>>
)