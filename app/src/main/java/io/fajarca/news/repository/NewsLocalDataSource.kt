package io.fajarca.news.repository

import android.util.Log
import androidx.paging.DataSource
import io.fajarca.news.db.dao.NewsDao
import io.fajarca.news.db.entity.News
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NewsLocalDataSource @Inject constructor(val dao : NewsDao) {

    fun insert(news: List<News>, insertFinished: () -> Unit) {
        Log.d("Local Data Source", "Inserting ${news.size} size to db")
        insertAllNewsToDb(news)
        insertFinished()
    }


    fun findNewsByKeyword(keyword : String): DataSource.Factory<Int, News> {
        //appending '%' so we can allow other characters to be before and after the query string
        val query = "%${keyword.replace(' ', '%')}%"
        return dao.findAll(query)
                .map { transformData(it) }
    }

    private fun insertAllNewsToDb(news: List<News>) {
        dao.insertAll(news)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe()
    }

    private fun transformData(news : News): News {
        val localTimeZone = toLocalTimeZone(news.publishedAt)
        val localDate = toLocalDate(localTimeZone)
        return News(news.title, news.imageUrl, news.description, localDate)
    }

    private fun toLocalTimeZone(originalDate: String?): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        var date: Date? = null

        try {
            date = dateFormat.parse(originalDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        dateFormat.timeZone = TimeZone.getDefault()

        return dateFormat.format(date)
    }

    private fun toLocalDate(time: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val date: Date

        try {
            date = dateFormat.parse(time)
            val calendar = Calendar.getInstance()
            calendar.time = date

            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val year = calendar.get(Calendar.YEAR)

            return "$dayOfMonth $month $year"

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        return time
    }

}