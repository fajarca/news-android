package io.fajarca.news.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.fajarca.news.db.entity.News
import io.reactivex.*


@Dao
interface NewsDao {

    @Query("SELECT * FROM NEWS WHERE (TITLE LIKE :keyword) OR (DESCRIPTION LIKE :keyword) ORDER BY PUBLISHED_AT DESC, TITLE ASC")
    fun findAll(keyword : String) : DataSource.Factory<Int, News>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(news : List<News>) : Completable
}