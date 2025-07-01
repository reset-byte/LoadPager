package com.github.pageloadlib.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.github.pageloadlib.R
import com.github.pageloadlib.net.ApiResponse
import kotlinx.coroutines.flow.Flow
import com.github.pageloadlib.adapter.EmptyViewBinder
import com.github.pageloadlib.config.FragmentGlobalConfig
import com.github.pageloadlib.config.LayoutManagerType
import com.github.pageloadlib.config.PlaceholderViewStyleConfig
import com.github.pageloadlib.event.EntityUIEvent
import com.github.pageloadlib.loadmore.CommonLoadMoreViewBinder
import com.github.pageloadlib.loadmore.LoadMoreDelegate
import com.github.pageloadlib.loadmore.MultiTypeLoadMoreAdapter
import com.github.pageloadlib.responselistener.OnProcessResponseListener
import com.github.pageloadlib.skeletonlayout.RecyclerViewSkeletonScreen
import com.github.pageloadlib.skeletonlayout.Skeleton
import com.github.pageloadlib.util.DensityUtils
import com.github.pageloadlib.util.IDividerItemDecoration
import com.github.pageloadlib.viewmodel.LoadMoreListFragmentViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * 基础刷新加载列表Fragment抽象类
 * 
 * 提供了完整的下拉刷新、上拉加载更多、骨架屏显示、空视图显示等功能
 * 使用SmartRefreshLayout + RecyclerView + MultiTypeAdapter实现
 * 
 * 主要功能：
 * - 支持下拉刷新和上拉加载更多
 * - 支持骨架屏显示提升用户体验
 * - 支持多种布局管理器（线性布局、瀑布流布局）
 * - 支持自定义空视图和错误视图
 * - 支持返回顶部功能
 * - 支持自定义加载更多视图
 * - 基于MVVM架构，使用ViewModel管理数据
 * 
 * 使用方式：
 * 1. 继承此类并实现所有抽象方法
 * 2. 在obtainGlobalConfig()中配置Fragment行为
 * 3. 在registerViewBinder()中注册各种数据类型的ViewBinder
 * 4. 在requestData()中实现具体的网络请求
 * 5. 实现数据处理相关的抽象方法
 * 
 * @param T 接口响应数据的类型
 */
abstract class BaseRefreshLoadListFragment<T> : CommonFragment(), View.OnClickListener,
    OnProcessResponseListener<T> {
    private var contentView: View? = null
    private var isViewCreated = false
    private var customViewArea: FrameLayout? = null
    private var brlSmartRefreshLayout: SmartRefreshLayout? = null
    private var brlRecyclerview: RecyclerView? = null
    private var brlBackToTop: ImageButton? = null
    private var customBackTopDrawable: Drawable? = null

    private val adapter = MultiTypeLoadMoreAdapter()
    private val viewModel: LoadMoreListFragmentViewModel<T> by viewModels()
    private var placeholderViewStyleConfig: PlaceholderViewStyleConfig? = null
    private lateinit var fragmentGlobalConfig: FragmentGlobalConfig
    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen

    // 是否调用过接口
    private var hasLoadOnce: Boolean = false

    // 是否成功调用过接口，用于判断是否显示骨架屏
    private var hasRequestSuccessOnce: Boolean = false

    // 接口请求参数
    private var requestParams: MutableMap<String, Any> = mutableMapOf()

    /**
     * 创建Fragment视图
     * 
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的实例状态
     * @return 创建的视图
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (contentView == null) {
            contentView = inflater.inflate(
                R.layout.common_ui_fragment_base_refresh_load_list,
                container,
                false
            )
            contentView?.let {
                initView(it)
            }
        }
        isViewCreated = true
        val parent = contentView?.parent
        if (parent is ViewGroup) {
            parent.removeView(contentView)
        }
        return contentView
    }

    /**
     * 初始化视图组件
     * 设置RecyclerView、SmartRefreshLayout、返回顶部按钮等组件的基本配置
     * 
     * @param view Fragment根视图
     */
    private fun initView(view: View) {
        customViewArea = view.findViewById(R.id.customViewArea)
        brlSmartRefreshLayout = view.findViewById(R.id.brlSmartRefreshLayout)
        brlRecyclerview = view.findViewById(R.id.brlRecyclerview)
        brlBackToTop = view.findViewById(R.id.brlBackToTop)

        brlBackToTop?.setOnClickListener(this)
        initGlobalConfig()
        initAdapter()
        brlRecyclerview?.layoutManager = when (fragmentGlobalConfig.layoutManagerType) {
            LayoutManagerType.LINEAR -> {
                LinearLayoutManager(view.context)
            }

            LayoutManagerType.STAGGERED_GRID -> {
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
        brlRecyclerview?.adapter = adapter
        brlRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                takeControlBackTopView()
            }
        })
        if (fragmentGlobalConfig.layoutManagerType == LayoutManagerType.LINEAR) {
            IDividerItemDecoration(view.context).apply {
                setVerticalDividerHeight(DensityUtils.dp2px(view.context, 12f))
                setDividerColor(view.context.getColor(R.color.white))
                setOffsetMode(IDividerItemDecoration.OFFSET_MODE_TOP)
                ignoreLastItem = true
                brlRecyclerview?.addItemDecoration(this)
            }
        } else {
            IDividerItemDecoration(view.context).apply {
                setVerticalDividerHeight(DensityUtils.dp2px(view.context, 48f))
                setDividerColor(view.context.getColor(R.color.transparent))
                brlRecyclerview?.addItemDecoration(this)
            }
        }
        brlSmartRefreshLayout?.setEnableOverScrollBounce(false)
        brlSmartRefreshLayout?.setOnRefreshListener {
            onPullToRefresh()
            requestDatas(true, showProgress = false)
        }
    }

    /**
     * Activity创建完成回调
     * 初始化ViewModel、设置错误视图配置、开始数据请求
     * 
     * @param savedInstanceState 保存的实例状态
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.debug = fragmentGlobalConfig.debug
        viewModel.currentPageIndex = fragmentGlobalConfig.firstPageStartFrom
        setErrorViewConfig(placeholderViewStyleConfig)
        initObserver()
        // fragment可见或者在OnActivityCreated回调触发时强制请求接口
        if (userVisibleHint || fragmentGlobalConfig.forceRequestOnActivityCreated) {
            requestDatas(true)
        }
    }

    /**
     * 初始化全局配置
     * 根据子类提供的配置初始化Fragment的行为参数，包括布局管理器、刷新头部、返回顶部按钮等
     */
    private fun initGlobalConfig() {
        fragmentGlobalConfig = obtainGlobalConfig()
        if (fragmentGlobalConfig.backToTopDrawableRes > 0) {
            customBackTopDrawable =
                ResourcesCompat.getDrawable(
                    resources,
                    fragmentGlobalConfig.backToTopDrawableRes,
                    null
                )
        }
        placeholderViewStyleConfig = fragmentGlobalConfig.placeholderViewStyleConfig
        adapter.setAutoLoadMoreTrigger(fragmentGlobalConfig.loadMoreWhenLeftItemCount)
        brlSmartRefreshLayout?.setEnableRefresh(fragmentGlobalConfig.supportPullToRefresh)
        brlSmartRefreshLayout?.setRefreshHeader(fragmentGlobalConfig.refreshHeader)

        // 设置回到顶部按钮距离右边底部的margin
        val lp = (brlBackToTop?.layoutParams as? ViewGroup.MarginLayoutParams)
        if (fragmentGlobalConfig.backToTopDrawableMarginBottomPx != -1) {
            lp?.bottomMargin = fragmentGlobalConfig.backToTopDrawableMarginBottomPx
        }
        if (fragmentGlobalConfig.backToTopDrawableMarginRightPx != -1) {
            lp?.rightMargin = fragmentGlobalConfig.backToTopDrawableMarginRightPx
        }
    }

    /**
     * 显示骨架屏
     * 在数据加载过程中显示骨架屏效果，提升用户体验
     */
    private fun showSkeletonScreen() {
        skeletonScreen = Skeleton.bind(brlRecyclerview)
            .adapter(adapter)
            .load(R.layout.common_ui_item_list_skeleton)
//            .angle(30)
//            .duration(500)
            .shimmer(false)
            .show()
    }

    /**
     * 初始化数据观察者
     * 监听ViewModel中的列表数据和UI事件，并根据变化更新UI状态
     */
    private fun initObserver() {
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer<List<Any>?> { listItems ->
            hasLoadOnce = true
            hideLoadingView()
            // 在没有请求成功数据且是刷新状态且设置了展示骨架屏
            if (!hasRequestSuccessOnce && viewModel.isRefresh && fragmentGlobalConfig.isShowSkeleton) {
                skeletonScreen.hide()
            }
            listItems?.also {
                brlSmartRefreshLayout?.visibility = View.VISIBLE
                adapter.items = it
                adapter.notifyDataSetChanged()
                if (it.isEmpty()) {
                    showErrorView(true)
                }
                hasRequestSuccessOnce = true
            } ?: showErrorView(false)
            brlSmartRefreshLayout?.finishRefresh()
        })

        viewModel.eventLiveData.observe(viewLifecycleOwner) { event ->
            event?.let {
                when (it.eventType) {
                    EntityUIEvent.EventType.LOADING ->
                        adapter.setLoadMoreState(MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_LOADING)

                    EntityUIEvent.EventType.LOAD_MORE_FAILED ->
                        adapter.setLoadMoreState(MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_FAILED)

                    EntityUIEvent.EventType.LOAD_FINISH -> {
                        var realShowCount = 0
                        getCurrentItems()?.let { items ->
                            realShowCount = items.size
                        }
                        adapter.setLoadMoreState(
                            if (realShowCount < fragmentGlobalConfig.minCountToShowLoadFinishView)
                                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_GONE
                            else
                                MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_NO_MORE_DATA
                        )
                    }
                }
            }
        }
    }

    /**
     * 初始化RecyclerView适配器
     * 设置多类型适配器，注册空视图绑定器和加载更多视图绑定器，配置加载更多逻辑
     */
    private fun initAdapter() {
        adapter.register(
            EmptyViewBinder.EmptyItem::class.java,
            EmptyViewBinder(
                placeholderViewStyleConfig
            )
        )
        registerViewBinder(adapter)
        adapter.setLoadMoreViewBinder(
            CommonLoadMoreViewBinder(
                fragmentGlobalConfig.loadMoreJsonRes,
                fragmentGlobalConfig.loadMoreLoadingText,
                fragmentGlobalConfig.loadMoreFailedText,
                fragmentGlobalConfig.loadMoreFinishText,
                fragmentGlobalConfig.layoutManagerType
            )
        )
        adapter.setLoadMoreObservable(object : LoadMoreDelegate.LoadMoreObservable {
            override val isLoading: Boolean
                get() = viewModel.isRequesting()

            override fun onLoadMore() {
                requestDatas(false)
            }
        })
        adapter.setLoadMoreRetryListener(object : MultiTypeLoadMoreAdapter.ILoadMoreRetryListener {
            override fun retry() {
                requestDatas(false)
            }
        })
    }

    /**
     * 设置自定义的加载更多视图绑定器
     * 
     * @param viewBinder 自定义的加载更多视图绑定器
     */
    protected fun setLoadMoreViewBinder(viewBinder: MultiTypeLoadMoreAdapter.AbstractItemViewBinder<*>) {
        adapter.setLoadMoreViewBinder(viewBinder)
    }

    /**
     * 设置Fragment对用户的可见性
     * 当Fragment对用户可见且视图已创建时，如果还未加载过数据则触发数据请求
     * 
     * @param isVisibleToUser Fragment是否对用户可见
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isViewCreated) {
            if (!hasLoadOnce) {
                requestDatas(true)
            }
        }
    }

    /**
     * 销毁视图时的清理工作
     * 取消网络请求，重置视图创建标志位
     */
    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        viewModel.cancelRequest()
    }

    /**
     * 显示错误视图
     * 根据是否有响应数据决定显示空视图还是加载失败视图
     * 
     * @param hasResponse 是否有响应数据，true显示空视图，false显示加载失败视图
     */
    open fun showErrorView(hasResponse: Boolean) {
        if (hasResponse) {
            showEmptyView()
        } else {
            brlSmartRefreshLayout?.visibility = View.GONE
            showLoadFailed()
        }
    }

    /**
     * 显示空视图
     * 根据配置决定在RecyclerView中显示空视图还是隐藏整个刷新布局显示全局空视图
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun showEmptyView() {
        if (fragmentGlobalConfig.emptyViewShowInRecyclerView) {
            adapter.items = arrayListOf(EmptyViewBinder.EmptyItem())
            adapter.notifyDataSetChanged()
            adapter.setLoadMoreState(MultiTypeLoadMoreAdapter.LoadMoreItem.STATE_GONE)
        } else {
            showEmpty()
            brlSmartRefreshLayout?.visibility = View.GONE
        }
    }

    /**
     * 控制返回顶部按钮的显示和隐藏
     * 根据RecyclerView的滚动位置决定是否显示返回顶部按钮，并添加平滑动画效果
     */
    private fun takeControlBackTopView() {
        if (brlRecyclerview?.layoutManager is LinearLayoutManager) {
            val visibleItemPosition =
                (brlRecyclerview?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val shouldShow = visibleItemPosition > fragmentGlobalConfig.backToTopWhenShowItemCount
            
            brlBackToTop?.let { backButton ->
                if (shouldShow && backButton.visibility != View.VISIBLE) {
                    // 显示按钮并添加渐入动画
                    backButton.visibility = View.VISIBLE
                    backButton.alpha = 0f
                    backButton.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                } else if (!shouldShow && backButton.visibility == View.VISIBLE) {
                    // 隐藏按钮并添加渐出动画
                    backButton.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            backButton.visibility = View.GONE
                        }
                        .start()
                }
                
                // 设置自定义图标
                customBackTopDrawable?.let { drawable ->
                    backButton.setImageDrawable(drawable)
                }
            }
        }
    }

    /**
     * 数据刷新回调
     * 子类可重写此方法以在刷新数据时执行自定义操作
     */
    open fun onRefreshDatas() {

    }

    /**
     * 下拉刷新回调
     * 子类可重写此方法以在下拉刷新时执行自定义操作
     */
    open fun onPullToRefresh() {

    }

    /**
     * 点击事件处理
     * 处理返回顶部按钮的点击事件，添加平滑滚动动画
     * 
     * @param v 被点击的视图
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.brlBackToTop -> {
                // 平滑滚动到顶部
                brlRecyclerview?.smoothScrollToPosition(0)
                
                // 添加按钮隐藏动画
                brlBackToTop?.let { backButton ->
                    backButton.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            backButton.visibility = View.GONE
                            backButton.alpha = 1f // 重置alpha为下次显示做准备
                        }
                        .start()
                }
            }
        }
    }

    /**
     * 获取加载包装视图
     * 
     * @return SmartRefreshLayout作为加载包装视图
     */
    override fun getLoadingWrapperView(): View? {
        return brlSmartRefreshLayout
    }

    /**
     * 获取RecyclerView组件
     * 
     * @return RecyclerView实例
     */
    fun getRecyclerView(): RecyclerView? {
        return brlRecyclerview
    }

    /**
     * 获取SmartRefreshLayout组件
     * 
     * @return SmartRefreshLayout实例
     */
    fun getSmartRefreshLayout(): SmartRefreshLayout? {
        return brlSmartRefreshLayout
    }

    /**
     * 获取刷新布局上方的自定义视图区域
     * 
     * @return 自定义视图区域的FrameLayout
     */
    fun getCustomViewAboveRefreshLayout(): FrameLayout? {
        return customViewArea
    }

    /**
     * 获取Fragment的根视图
     * 
     * @return Fragment的根视图
     */
    fun getRootView(): View? {
        return contentView
    }

    /**
     * 获取Fragment全局配置
     * 子类必须实现此方法以提供Fragment的行为配置
     * 
     * @return Fragment全局配置对象
     */
    abstract fun obtainGlobalConfig(): FragmentGlobalConfig

    /**
     * 判断是否已加载完所有数据
     * 根据接口返回的数据结构判断分页是否已加载完成
     * 
     * @param response 接口响应数据
     * @return true表示已加载完成，false表示还有更多数据
     */
    abstract override fun isLoadMoreFinished(response: T?): Boolean

    /**
     * 判断请求成功但数据为空
     * 根据接口返回的数据结构判断请求是否成功但返回的数据列表为空
     * 
     * @param response 接口响应数据
     * @return true表示请求成功但数据为空，false表示有数据或请求失败
     */
    abstract override fun isRequestSuccessButDataEmpty(response: T?): Boolean

    /**
     * 注册RecyclerView的ViewBinder
     * 子类需要在此方法中注册各种数据类型对应的ViewBinder
     * 
     * @param adapter MultiTypeAdapter实例
     */
    abstract fun registerViewBinder(adapter: MultiTypeAdapter)

    /**
     * 获取适配器所需的数据列表
     * 根据接口返回的数据结构，处理并返回适配器需要的数据列表
     * 
     * @param response 接口响应数据
     * @param currentItems 当前适配器中的数据列表
     * @param isRefresh 是否为刷新操作
     * @return 处理后的数据列表
     */
    abstract override fun obtainListForAdapter(
        response: T?,
        currentItems: List<Any>?,
        isRefresh: Boolean
    ): List<Any>

    /**
     * 请求数据的抽象方法
     * 子类需要实现此方法来定义具体的数据请求逻辑
     * 
     * @param options 请求参数Map，包含分页信息等
     * @return 返回ApiResponse<T>类型的Flow数据流
     */
    abstract fun requestData(options: Map<String, Any>): Flow<ApiResponse<T>>

    /**
     * 获取当前的数据列表
     * 
     * @return 当前适配器中的数据列表
     */
    protected fun getCurrentItems(): List<Any>? {
        return viewModel.listLiveData.value
    }

    /**
     * 获取当前的多类型适配器
     * 
     * @return MultiTypeAdapter实例
     */
    protected fun getCurrentAdapter(): MultiTypeAdapter {
        return adapter
    }

    /**
     * 更新当前的数据列表
     * 直接更新ViewModel中的数据，会触发UI更新
     * 
     * @param items 新的数据列表
     */
    protected fun updateCurrentItems(items: List<Any>?) {
        viewModel.listLiveData.value = items
    }

    /**
     * 请求数据
     * 执行数据请求操作，支持刷新和加载更多两种模式
     * 
     * @param isRefresh 是否为刷新操作，true为刷新，false为加载更多
     * @param showProgress 是否显示加载进度，默认为true
     */
    protected fun requestDatas(isRefresh: Boolean, showProgress: Boolean = true) {
        viewModel.isRefresh = isRefresh
        val pageIndex: Int = if (isRefresh) {
            onRefreshDatas()
            if (showProgress) {
                showLoadingView()
                if (fragmentGlobalConfig.isShowSkeleton) {
                    if (adapter.items.isEmpty() && !hasRequestSuccessOnce) {
                        showSkeletonScreen()
                    } else {
                        skeletonScreen.hide()
                    }
                }
            }
            1
        } else {
            viewModel.currentPageIndex + 1
        }

        requestParams["page"] = pageIndex
        requestParams["pageSize"] = fragmentGlobalConfig.pageSize

        viewModel.requestData(
            requestParams,
            ::requestData,
            isRefresh,
            this@BaseRefreshLoadListFragment,
        )
    }

    /**
     * 加载重试回调
     * 当加载失败时点击重试触发此方法
     */
    override fun onLoadRetry() {
        super.onLoadRetry()
        requestDatas(true)
    }

    /**
     * 提供给外部调用的数据刷新方法
     * 外部可以通过此方法主动触发数据刷新
     */
    open fun externalRequestData() {
        requestDatas(true)
    }

    /**
     * 删除指定位置的数据项
     * 删除成功后会自动刷新RecyclerView，如果删除后列表为空则显示空视图
     * 
     * @param position 要删除的数据项位置
     */
    protected fun removeItem(position: Int) {
        if (position in 0 until adapter.items.size) {
            val items = adapter.items as MutableList<Any>
            items.removeAt(position)
            adapter.notifyItemRemoved(position)
            if (items.size == 1) {
                showEmptyView()
            }
        }
    }

    /**
     * 更新指定位置的数据项
     * 更新成功后会自动刷新对应位置的RecyclerView项
     * 
     * @param position 要更新的数据项位置
     * @param item 新的数据项
     */
    protected fun updateItem(position: Int, item: Any) {
        if (position in 0 until adapter.items.size) {
            (adapter.items as MutableList<Any>)[position] = item
            adapter.notifyItemChanged(position)
        }
    }

}