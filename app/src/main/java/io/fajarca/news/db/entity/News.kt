package io.fajarca.news.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class News(
        @PrimaryKey
        @ColumnInfo(name = "title")
        var title : String,
        @ColumnInfo(name = "image_url")
        var imageUrl : String?,
        @ColumnInfo(name = "description")
        var description : String?,
        @ColumnInfo(name = "published_at")
        var publishedAt : String?
)