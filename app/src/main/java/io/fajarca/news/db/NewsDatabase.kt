package io.fajarca.news.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.fajarca.news.db.dao.NewsDao
import io.fajarca.news.db.entity.News

@Database(entities = [News::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao() : NewsDao
}