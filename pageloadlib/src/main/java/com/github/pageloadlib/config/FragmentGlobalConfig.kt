package com.github.pageloadlib.config

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.github.pageloadlib.R
import com.github.pageloadlib.util.IDividerItemDecoration
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshHeader

/**
 * 用于配置Fragment相关的全局变量
 */

class FragmentGlobalConfig constructor(context: Context?) {

    /**
     * debug模式
     */
    var debug = false

    /**
     * 是否支持下拉刷新
     */
    var supportPullToRefresh = true

    /**
     * 从第几页开始加载
     */
    var firstPageStartFrom = 1

    /**
     * 分页大小
     */
    var pageSize = 20

    /**
     * 页面超过多少条数据才展示"到底了"，默认为3
     */
    var minCountToShowLoadFinishView = 3

    /**
     * 设置下拉刷新头部，建议继承自InternalAbstract并实现RefreshHeader接口
     */
    var refreshHeader: RefreshHeader = ClassicsHeader(context)

    /**
     * 设置加载动画，要求传入放在raw文件夹下的json格式文件
     */
    @get:RawRes
    var loadingJsonRes = R.raw.common_ui_loading_progress

    /**
     * 设置上拉加载动画，要求传入放在raw文件夹下的json格式文件
     */
    @get:RawRes
    var loadMoreJsonRes: Int? = null

    /**
     * 设置上拉加载 加载中文案
     */
    var loadMoreLoadingText: String? = "加载中"

    /**
     * 设置上拉加载失败文案
     */
    var loadMoreFailedText: String? = "加载失败，请点击重试"

    /**
     * 设置上拉加载结束，没有数据的文案
     */
    var loadMoreFinishText: String? = "—— 没有更多了 ——"

    /**
     * 设置触发自动加载下一页的时机：<= totalItemCount - lastVisibleCount
     */
    var loadMoreWhenLeftItemCount = 4

    /**
     * 设置兜底页面的样式，包括错误样式和空样式
     */
    var placeholderViewStyleConfig: PlaceholderViewStyleConfig? = null

    /**
     * 设置返回顶部图标显示时机：可见item数量
     */
    var backToTopWhenShowItemCount = 20

    /**
     * 设置返回顶部图标, 0表示不设置
     */
    @get:DrawableRes
    var backToTopDrawableRes = 0

    /**
     * 设置返回顶部图标的margin
     */
    var backToTopDrawableMarginRightPx = -1

    /**
     * 设置返回顶部图标的margin
     */
    var backToTopDrawableMarginBottomPx = -1

    /**
     * emptyView是否以item形式展示在Recyclerview中
     */
    var emptyViewShowInRecyclerView = false

    /**
     * 在OnActivityCreated回调触发时是否强制请求接口
     */
    var forceRequestOnActivityCreated = false

    /**
     * skeleton图类型 0:默认 1:列表 2:网格 3:瀑布流
     */
    var skeletonType: Int? = 0

    /**
     * layoutManagerType类型 LayoutManagerType.LINEAR  LayoutManagerType.STAGGERED_GRID
     */
    var layoutManagerType: LayoutManagerType = LayoutManagerType.LINEAR

    /**
     * 是否展示骨架屏, 默认不展示
     */
    var isShowSkeleton = false

    /**
     * 用于动态设置recyclerview item背景
     */
    val iDividerItemDecoration: IDividerItemDecoration? = null
}