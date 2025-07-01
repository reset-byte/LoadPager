package com.github.pageloadlib.loadmore

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * recyclerView 加载更多事件触发逻辑封装
 */
class LoadMoreDelegate(loadMoreObservable: LoadMoreObservable) {

    private val endlessScrollListener = EndlessScrollListener(loadMoreObservable)

    /**
     * Should be called after recyclerView setup with its layoutManager and adapter
     *
     * @param recyclerView the RecyclerView
     */
    fun attach(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(
            endlessScrollListener
        )
    }

    fun setVisibleThreshold(visibleThreshold: Int) {
        endlessScrollListener.mVisibleThreshold = visibleThreshold
    }

    class EndlessScrollListener(loadMoreObservable: LoadMoreObservable) :
        RecyclerView.OnScrollListener() {
        var mVisibleThreshold = 6
        private val loadMoreObservable: LoadMoreObservable?
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy < 0 || loadMoreObservable == null || loadMoreObservable.isLoading) {
                return
            }
            var lastItem = 0
            val layoutManager = recyclerView.layoutManager
            val totalItemCount = layoutManager!!.itemCount
            if (layoutManager is GridLayoutManager) {
                //Position to find the final item of the current LayoutManager
                lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastItem == -1) {
                    lastItem = layoutManager.findLastVisibleItemPosition()
                }
            } else if (layoutManager is LinearLayoutManager) {
                lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastItem == -1) {
                    lastItem = layoutManager.findLastVisibleItemPosition()
                }
            } else if (layoutManager is StaggeredGridLayoutManager) {
                // since may lead to the final item has more than one StaggeredGridLayoutManager the particularity of the so here that is an array
                // this array into an array of position and then take the maximum value that is the last show the position value
                val lastPositions = layoutManager.findLastCompletelyVisibleItemPositions(null)
                lastItem = findMax(lastPositions)
            }
            val childCount = layoutManager.childCount
            val shouldLoadMore =
                childCount > 1 && lastItem >= totalItemCount - mVisibleThreshold - 1
            if (shouldLoadMore) {
                loadMoreObservable.onLoadMore()
            }
        }

        init {
            this.loadMoreObservable = loadMoreObservable
        }
    }

    interface LoadMoreObservable {
        /**
         * load status
         * @return true or false
         */
        val isLoading: Boolean

        /**
         * 加载回调
         */
        fun onLoadMore()
    }

    companion object {
        private fun findMax(lastPositions: IntArray?): Int {
            if (lastPositions == null || lastPositions.isEmpty()) {
                return 0
            }
            var max = lastPositions[0]
            for (value in lastPositions) {
                max = max.coerceAtLeast(value)
            }
            return max
        }
    }

}