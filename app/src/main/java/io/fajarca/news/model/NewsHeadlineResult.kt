package io.fajarca.news.model


import com.google.gson.annotations.SerializedName

data class NewsHeadlineResult(
        @SerializedName("articles")
        val articles: List<HeadlineArticle>,
        @SerializedName("status")
        val status: String,
        @SerializedName("totalResults")
        val totalResults: Int
)


data class HeadlineArticle(
        @SerializedName("author")
        val author: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("publishedAt")
        val publishedAt: String,
        @SerializedName("source")
        val source: HeadlineSource,
        @SerializedName("title")
        val title: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("urlToImage")
        val urlToImage: String
)

data class HeadlineSource(
        @SerializedName("id")
        val id: Any,
        @SerializedName("name")
        val name: String
)