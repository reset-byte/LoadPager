package com.github.loadpager.sample

import com.drakeet.multitype.MultiTypeAdapter
import com.github.loadpager.R
import com.github.pageloadlib.config.FragmentGlobalConfig
import com.github.pageloadlib.config.LayoutManagerType
import com.github.pageloadlib.fragment.BaseRefreshLoadListFragment
import com.github.pageloadlib.net.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import java.io.IOException

/**
 * 玩Android文章列表Fragment
 * 展示如何使用BaseRefreshLoadListFragment从真实API获取数据并实现下拉刷新和上拉加载更多功能
 */
class SampleListFragment : BaseRefreshLoadListFragment<WanAndroidResponse>() {

    override fun obtainGlobalConfig(): FragmentGlobalConfig {
        val config = FragmentGlobalConfig(context)
        config.layoutManagerType = LayoutManagerType.LINEAR
        config.supportPullToRefresh = true
        config.isShowSkeleton = true
        config.pageSize = 20 // 玩Android API默认页面大小为20
        config.firstPageStartFrom = 0 // 玩Android页码从0开始
        config.loadMoreWhenLeftItemCount = 3
        config.backToTopWhenShowItemCount = 10 // 滚动超过10个项目就显示返回顶部按钮
        config.backToTopDrawableRes = R.drawable.ic_back_to_top_background // 设置返回顶部按钮图标
        config.minCountToShowLoadFinishView = 20
        config.debug = true
        return config
    }

    override fun registerViewBinder(adapter: MultiTypeAdapter) {
        adapter.register(Article::class.java, SampleItemViewBinder())
    }

    override fun requestData(options: Map<String, Any>): Flow<ApiResponse<WanAndroidResponse>> {
        return flow {
            try {
                val page = options["page"] as? Int ?: 0
                
                // 构建API URL - 注意玩Android的页码是从0开始的
                val apiUrl = "https://www.wanandroid.com/article/list/${page}/json"
                
                // 创建HTTP客户端和请求
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(apiUrl)
                    .get()
                    .build()
                
                // 执行网络请求
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        // 解析JSON响应
                        val gson = Gson()
                        val wanAndroidResponse = gson.fromJson(responseBody, WanAndroidResponse::class.java)
                        
                        if (wanAndroidResponse.errorCode == 0) {
                            emit(ApiResponse.Success(wanAndroidResponse))
                        } else {
                            emit(ApiResponse.Error(Exception("API错误: ${wanAndroidResponse.errorMsg}")))
                        }
                    } else {
                        emit(ApiResponse.Error(Exception("响应体为空")))
                    }
                } else {
                    emit(ApiResponse.Error(Exception("网络请求失败: ${response.code}")))
                }
                
                response.close()
                
            } catch (e: IOException) {
                emit(ApiResponse.Error(Exception("网络连接失败: ${e.message}")))
            } catch (e: Exception) {
                emit(ApiResponse.Error(Exception("请求失败: ${e.message}")))
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun isLoadMoreFinished(response: WanAndroidResponse?): Boolean {
        // 当over为true表示已经到最后一页了
        return response?.data?.over == true
    }

    override fun isRequestSuccessButDataEmpty(response: WanAndroidResponse?): Boolean {
        return response?.errorCode == 0 && response.data.datas.isEmpty()
    }

    override fun obtainListForAdapter(
        response: WanAndroidResponse?,
        currentItems: List<Any>?,
        isRefresh: Boolean
    ): List<Any> {
        val newArticles = response?.data?.datas ?: emptyList()
        return if (isRefresh) {
            newArticles
        } else {
            (currentItems ?: emptyList()) + newArticles
        }
    }

    companion object {
        /**
         * 创建SampleListFragment实例
         * 
         * @return SampleListFragment实例
         */
        fun newInstance(): SampleListFragment {
            return SampleListFragment()
        }
    }
} 