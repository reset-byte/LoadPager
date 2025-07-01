package com.github.pageloadlib.loadmore

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class DefaultLoadMoreItemViewBinder :
    MultiTypeLoadMoreAdapter.AbstractItemViewBinder<DefaultLoadMoreItemViewBinder.DefaultViewHolder>() {
    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): DefaultViewHolder {
        val view = TextView(inflater.context)
        val params = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = params
        view.gravity = Gravity.CENTER
        return DefaultViewHolder(view)
    }

    internal class DefaultViewHolder(itemView: View) :
        MultiTypeLoadMoreAdapter.ViewHolder(itemView) {
        override fun setState(state: Int) {
            var tips = "state_none"
            when (state) {
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_LOADING -> tips = "state loading"
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_FAILED -> tips = "state failed"
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_NO_MORE_DATA -> tips = "state no more data"
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_GONE -> itemView.visibility = View.GONE
                else -> {
                }
            }
            (itemView as TextView).text = tips
        }
    }
}