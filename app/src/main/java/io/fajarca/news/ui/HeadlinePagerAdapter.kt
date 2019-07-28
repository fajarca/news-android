package io.fajarca.news.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.fajarca.news.R
import io.fajarca.news.model.HeadlineArticle

class HeadlinePagerAdapter(var headlines : List<HeadlineArticle>, val context : Context) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj : Any): Boolean {
        return view == obj
    }

    override fun getCount() = headlines.size

    override fun destroyItem(container: ViewGroup, position: Int, obj : Any) {
        val view = obj as View
        container.removeView(view)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_headline_news, container, false)

        val imageView =     view.findViewById<ImageView>(R.id.ivHeadline)
        val tvTitle = view.findViewById<TextView>(R.id.tvHeadlineTitle)

        val headline = headlines[position].urlToImage

        val options = RequestOptions
                .fitCenterTransform()


        Glide.with(context)
                .load(headline)
                .apply(options)
                .thumbnail(0.1f)
                .into(imageView)

        tvTitle.text = headlines[position].title


        container.addView(view)

        return view
    }

    fun refreshHeadlines(headlines : List<HeadlineArticle>) {
        this.headlines = headlines
    }
}