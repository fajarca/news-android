package io.fajarca.news.common

sealed class UiState<out T> {
    class Loading<out T> : UiState<T>()
    class Success<out T> : UiState<T>()
    class NoData<out T> : UiState<T>()
    class NoInternetConnection<out T> : UiState<T>()
    data class Error<out T>(val errorMessage : String) : UiState<T>()
}
