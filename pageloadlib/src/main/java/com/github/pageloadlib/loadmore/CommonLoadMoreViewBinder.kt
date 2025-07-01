package com.github.pageloadlib.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.github.pageloadlib.R
import com.github.pageloadlib.config.LayoutManagerType

class CommonLoadMoreViewBinder(
    @RawRes val loadingRes: Int? = null,
    private val loadingText: String? = null,
    private val failedText: String? = null,
    private val loadFinishText: String? = null,
    private val layoutType: LayoutManagerType = LayoutManagerType.LINEAR // 默认线性布局
) : MultiTypeLoadMoreAdapter.AbstractItemViewBinder<MultiTypeLoadMoreAdapter.ViewHolder>() {

    override fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ViewHolder {
        val footer =
            inflater.inflate(R.layout.common_loadmore_item, parent, false)
        // 根据布局类型设置不同的 LayoutParams
        when (layoutType) {
            LayoutManagerType.STAGGERED_GRID -> {
                (footer.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.apply {
                    isFullSpan = true
                } ?: run {
                    footer.layoutParams = StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        isFullSpan = true
                    }
                }
            }
            LayoutManagerType.LINEAR -> {
                footer.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
        return ViewHolder(footer, loadingRes, loadingText, failedText, loadFinishText, layoutType)
    }

    inner class ViewHolder(
        itemView: View,
        @RawRes val loadingRes: Int? = null,
        private val loadingText: String? = null,
        private val failedText: String? = null,
        private val loadFinishText: String? = null,
        private val layoutType: LayoutManagerType
    ) : MultiTypeLoadMoreAdapter.ViewHolder(itemView) {
        private var tips: TextView = itemView.findViewById(R.id.tv_tips)
        private var lottieView: LottieAnimationView = itemView.findViewById(R.id.lottie_load_more)
        private var progressDrawable: ProgressBar = itemView.findViewById(R.id.progressbar)

        init {
            // 根据布局类型设置参数
            if (layoutType == LayoutManagerType.STAGGERED_GRID) {
                (itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
            }
            loadingRes?.let {
                lottieView.setAnimation(it)
            }
            lottieView.visibility = View.GONE
            progressDrawable.visibility = View.GONE
            tips.visibility = View.GONE
        }

        override fun setState(state: Int) {
            // 确保瀑布流布局时 fullSpan 设置正确
            if (layoutType == LayoutManagerType.STAGGERED_GRID) {
                (itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.isFullSpan = true
            }
            when (state) {
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_LOADING -> {
                    loadingRes?.let {
                        lottieView.visibility = View.VISIBLE
                        lottieView.playAnimation()
                    }
                    progressDrawable.visibility = View.VISIBLE
                    tips.visibility = View.VISIBLE
                    tips.text = loadingText
                }
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_FAILED -> {
                    loadingRes?.let {
                        lottieView.visibility = View.GONE
                        lottieView.pauseAnimation()
                    }
                    progressDrawable.visibility = View.GONE
                    tips.visibility = View.VISIBLE
                    tips.text = failedText
                }
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_NO_MORE_DATA -> {
                    lottieView.visibility = View.GONE
                    progressDrawable.visibility = View.GONE
                    lottieView.pauseAnimation()
                    tips.visibility = View.VISIBLE
                    tips.text = loadFinishText
                }
                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_GONE -> {
                    tips.visibility = View.GONE
                    lottieView.visibility = View.GONE
                    lottieView.pauseAnimation()
                    progressDrawable.visibility = View.GONE
                }
            }
        }
    }
}