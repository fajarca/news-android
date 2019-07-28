package io.fajarca.news.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import io.fajarca.news.R
import io.fajarca.news.databinding.ItemNewsBinding
import io.fajarca.news.db.entity.News


class NewsPagedListAdapter : PagedListAdapter<News, NewsPagedListAdapter.NewsViewHolder>(NewsDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

            val binding = ItemNewsBinding.inflate(inflater, parent, false)
            val viewHolder = NewsViewHolder(binding)
            return viewHolder

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(getItem(position))
    }

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News?) {
            binding.news = news

            val options = RequestOptions
                    .placeholderOf(R.drawable.placeholder)
                    .centerCrop()

            Glide.with(itemView)
                    .load(news?.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(binding.iv)

            binding.executePendingBindings()

        }

    }


    companion object {
        val NewsDiffCallback = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }
}