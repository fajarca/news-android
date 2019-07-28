package io.fajarca.news.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NewsResult(
        @SerializedName("status")
        @Expose
        val status: String,
        @SerializedName("totalResults")
        @Expose
        val totalResults: Int,
        @SerializedName("articles")
        @Expose
        val articles: List<ApiNews>? = null)

data class ApiNews (
        @SerializedName("title")
        @Expose
        val title: String? = null,

        @SerializedName("description")
        @Expose
        val description: String? = null,

        @SerializedName("urlToImage")
        @Expose
        val urlToImage: String? = null,

        @SerializedName("publishedAt")
        @Expose
        val publishedAt: String? = null
)