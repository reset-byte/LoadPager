package com.github.pageloadlib.adapter

import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.pageloadlib.R
import com.github.pageloadlib.config.PlaceholderViewStyleConfig
import com.github.pageloadlib.util.ErrorViewAdapter
import com.github.pageloadlib.util.JLoading

class EmptyViewHolder(
    itemView: View,
    placeholderConfig: PlaceholderViewStyleConfig?
): RecyclerView.ViewHolder(itemView) {

    private val emptyViewRoot = itemView.findViewById<FrameLayout>(R.id.emptyViewRoot)
    private val holder = JLoading.getDefault().wrap(emptyViewRoot)

    init {
        JLoading.getDefault().adapter ?: JLoading.getDefault().setAdapter(ErrorViewAdapter())
        if (placeholderConfig != null) {
            holder.errorViewConfig = placeholderConfig
        }
    }

    fun onBind() {
        holder.showEmpty()
    }
}