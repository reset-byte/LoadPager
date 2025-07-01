package com.github.loadpager.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.github.loadpager.R

/**
 * 文章数据项的ViewBinder
 * 用于在RecyclerView中显示Article数据
 */
class SampleItemViewBinder : ItemViewBinder<Article, SampleItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_sample, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, article: Article) {
        holder.bind(article)
    }

    /**
     * 文章数据项的ViewHolder
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)
        private val idTextView: TextView = itemView.findViewById(R.id.tvId)

        /**
         * 绑定数据到视图
         * 
         * @param article 文章数据项
         */
        fun bind(article: Article) {
            titleTextView.text = article.title
            
            // 显示作者信息，优先显示author，如果为空则显示shareUser
            val authorText = when {
                article.author.isNotBlank() -> "作者: ${article.author}"
                article.shareUser.isNotBlank() -> "分享: ${article.shareUser}"
                else -> "匿名"
            }
            
            // 组合描述信息：章节 + 时间 + 作者
            val descriptionText = buildString {
                append("${article.superChapterName} > ${article.chapterName}")
                append("\n")
                append("发布时间: ${article.niceDate}")
                append("\n")
                append(authorText)
            }
            
            descriptionTextView.text = descriptionText
            idTextView.text = "ID: ${article.id}"
            
            // 设置点击事件，可以在这里添加跳转到文章详情的逻辑
            itemView.setOnClickListener {
                // 这里可以添加点击跳转逻辑
                // 例如：跳转到WebView显示article.link
            }
        }
    }
} 