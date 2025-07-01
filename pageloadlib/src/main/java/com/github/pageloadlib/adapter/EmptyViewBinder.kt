package com.github.pageloadlib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.github.pageloadlib.R
import com.github.pageloadlib.config.PlaceholderViewStyleConfig

class EmptyViewBinder(val config: PlaceholderViewStyleConfig?) :
    ItemViewBinder<EmptyViewBinder.EmptyItem, EmptyViewHolder>() {
    override fun onBindViewHolder(holder: EmptyViewHolder, item: EmptyItem) {
        holder.onBind()
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): EmptyViewHolder {
        return EmptyViewHolder(
            inflater.inflate(R.layout.common_ui_layout_empty, parent, false),
            config
        )
    }

    class EmptyItem {

    }
}