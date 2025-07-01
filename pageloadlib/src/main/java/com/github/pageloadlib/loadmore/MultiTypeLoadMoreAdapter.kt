package com.github.pageloadlib.loadmore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.github.pageloadlib.util.UiThreadUtils
import java.util.*

/**
 * 分页加载Adapter
 */
class MultiTypeLoadMoreAdapter : MultiTypeAdapter(), LoadMoreDelegate.LoadMoreObservable {
    private var loadMoreItemViewBinder: AbstractItemViewBinder<*>
    private val loadMoreItem = LoadMoreItem()
    private val loadMoreDelegate = LoadMoreDelegate(this)
    private var loadMoreObservable: LoadMoreDelegate.LoadMoreObservable? = null

    // 针对GridLayoutManager布局配置
    private var mSpanSizeLookup: SpanSizeLookup? = null

    override var items: List<Any> = emptyList()
        set(newItems) {
            if (newItems.isEmpty()) {
                field = newItems
                return
            }
            val mutableList: MutableList<Any> = ArrayList()
            mutableList.addAll(newItems)
            if (newItems.last() != loadMoreItem) {
                mutableList.add(loadMoreItem)
            }
            field = mutableList
        }

    fun appendItemsAndNotify(newItems: List<Any>) {
        if (newItems.isNullOrEmpty()) {
            return
        }
        val size = items.size
        val mutableList = items.toMutableList()
        if (mutableList.lastOrNull() == loadMoreItem) {
            val index = size - 1
            mutableList.addAll(index, newItems)
            items = mutableList
            notifyItemRangeChanged(index, newItems.size)
        } else {
            // 最后一项不是loadMoreItem的场景
            mutableList.addAll(newItems)
            mutableList.add(loadMoreItem)
            items = mutableList
            notifyItemRangeChanged(size, newItems.size + 1)
        }
    }

    fun setLoadMoreState(@LoadMoreItem.ItemState state: Int) {
        if (loadMoreItem.state == LoadMoreItem.STATE_NO_MORE_DATA && state == LoadMoreItem.STATE_FAILED) {
            // 如果已经提示没有更多数据了，此时新状态为加载失败则过滤。
            return
        }
        loadMoreItem.state = state
        UiThreadUtils.post(
            Runnable {
                // 延迟100ms刷新UI，避免新数据加入列表之前刷新loadMore
                if (itemCount > 0) {
                    notifyItemChanged(itemCount - 1)
                }
            }, 100
        )
    }

    /**
     * @param listener retry event callback
     */
    fun setLoadMoreRetryListener(listener: ILoadMoreRetryListener) {
        loadMoreItemViewBinder.setLoadMoreRetryListener(listener)
    }

    fun setGridLayoutSpanSizeLookup(spanSizeLookup: SpanSizeLookup) {
        mSpanSizeLookup = spanSizeLookup
    }

    fun setLoadMoreViewBinder(viewBinder: AbstractItemViewBinder<*>) {
        viewBinder.setLoadMoreRetryListener(loadMoreItemViewBinder.mListener)
        loadMoreItemViewBinder = viewBinder
        register(LoadMoreItem::class.java, viewBinder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        loadMoreDelegate.attach(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager == null) {
            Log.e(
                TAG,
                "Cannot setSpanSizeLookup on a null LayoutManager Object. " +
                        "Call setLayoutManager with a non-null argument."
            )
            return
        }
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = mSpanSizeLookup ?: object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val obj = items[position]
                    return if (obj is LoadMoreItem) layoutManager.spanCount else 1
                }
            }
        }
    }

    fun setAutoLoadMoreTrigger(visibleThreshold: Int) {
        loadMoreDelegate.setVisibleThreshold(visibleThreshold)
    }

    fun setLoadMoreObservable(loadMoreObservable: LoadMoreDelegate.LoadMoreObservable) {
        this.loadMoreObservable = loadMoreObservable
    }

    override val isLoading: Boolean
        get() = loadMoreObservable == null || loadMoreObservable!!.isLoading

    override fun onLoadMore() {
        // Loading 和 failed 状态均可触发加载下一页
        if (loadMoreObservable != null &&
            (loadMoreItem.state == LoadMoreItem.STATE_LOADING
                    || loadMoreItem.state == LoadMoreItem.STATE_FAILED)
        ) {
            loadMoreObservable?.also { delegate ->
                delegate.onLoadMore()
            }
        }
    }

    /**
     * dataEntity for LoadMoreViewBinder
     */
    class LoadMoreItem {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(
            STATE_NO_MORE_DATA,
            STATE_FAILED,
            STATE_LOADING,
            STATE_GONE
        )
        annotation class ItemState

        @get:ItemState
        var state = STATE_GONE

        companion object {
            const val STATE_LOADING = 0

            /**
             * finish load
             */
            const val STATE_NO_MORE_DATA = 1
            const val STATE_FAILED = 2

            /**
             * not visible
             */
            const val STATE_GONE = 3
        }
    }

    /**
     * loadMoreViewBinder基类，限制子类实现各种状态。
     */
    abstract class AbstractItemViewBinder<VH : ViewHolder> : ItemViewBinder<LoadMoreItem, VH>() {
        var mListener: ILoadMoreRetryListener? = null
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
            val viewHolder = createViewHolder(inflater, parent)
            viewHolder.mListener = mListener
            return viewHolder
        }

        override fun onBindViewHolder(holder: VH, item: LoadMoreItem) {
            holder.data = item
            holder.setState(item.state)
        }

        protected abstract fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH
        fun setLoadMoreRetryListener(listener: ILoadMoreRetryListener?) {
            mListener = listener
        }
    }

    /**
     * loadMore ViewHolder 基类
     */
    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: LoadMoreItem? = null
        var mListener: ILoadMoreRetryListener? = null
        abstract fun setState(@LoadMoreItem.ItemState state: Int)

        init {
            itemView.setOnClickListener {
                data?.let {
                    if (it.state == LoadMoreItem.STATE_FAILED) {
                        mListener?.let { listener ->
                            listener.retry()
                            it.state = LoadMoreItem.STATE_LOADING
                            setState(it.state)
                        }
                    }
                }
            }
        }
    }

    interface ILoadMoreRetryListener {
        fun retry()
    }

    companion object {
        private val TAG = MultiTypeLoadMoreAdapter::class.java.simpleName
    }

    init {
        loadMoreItemViewBinder = DefaultLoadMoreItemViewBinder()
        register(LoadMoreItem::class.java, loadMoreItemViewBinder)
    }
}