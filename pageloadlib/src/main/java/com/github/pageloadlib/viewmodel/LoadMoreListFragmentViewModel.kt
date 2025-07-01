package com.github.pageloadlib.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pageloadlib.event.EntityUIEvent
import com.github.pageloadlib.net.ApiResponse
import com.github.pageloadlib.net.NetworkException
import com.github.pageloadlib.responselistener.OnProcessResponseListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * 带分页加载功能的列表ViewModel
 */
class LoadMoreListFragmentViewModel<T> : ViewModel() {
    
    // 用于取消网络请求的Job
    private var requestJob: Job? = null
    
    // 调试模式开关
    var debug: Boolean = false
    
    // 当前是否为刷新请求
    var isRefresh: Boolean = true
    
    // 当前是否正在加载
    var isLoading: Boolean = false
    
    // 是否发生网络错误
    var isNetworkError: Boolean = false
    
    // 当前页码索引
    var currentPageIndex: Int = 0
    
    // 列表数据LiveData
    val listLiveData = MutableLiveData<List<Any>?>()
    
    // UI事件LiveData
    val eventLiveData = MutableLiveData<EntityUIEvent>()

    /**
     * 请求列表接口数据并更新LiveData
     * @param params 请求接口的参数map
     * @param requestData 请求的函数，返回Flow<ApiResponse<T>>
     * @param isRefresh 是否刷新
     * @param listener 监听接口，提供业务逻辑判断
     */
    fun requestData(
        params: Map<String, Any>,
        requestData: (Map<String, Any>) -> Flow<ApiResponse<T>>,
        isRefresh: Boolean,
        listener: OnProcessResponseListener<T>
    ) {
        // 取消之前的请求
        requestJob?.cancel()
        
        this.isRefresh = isRefresh
        this.isLoading = true
        this.isNetworkError = false
        
        requestJob = viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                if (debug) {
                    throwable.printStackTrace()
                }
                handleException(NetworkException.UnknownException(throwable.message ?: "Error", throwable))
            }
        ) {
            try {
                requestData(params)
                    .flowOn(Dispatchers.IO)
                    .catch { throwable ->
                        // 转换为NetworkException
                        val networkException = when (throwable) {
                            is NetworkException -> throwable
                            else -> NetworkException.UnknownException(throwable.message ?: "Error", throwable)
                        }
                        handleException(networkException)
                    }
                    .collect { response ->
                        handleApiResponse(response, listener)
                    }
            } catch (e: Exception) {
                if (debug) {
                    e.printStackTrace()
                }
                val networkException = when (e) {
                    is NetworkException -> e
                    else -> NetworkException.UnknownException(e.message ?: "Error", e)
                }
                handleException(networkException)
            }
        }
    }
    
    /**
     * 处理API响应
     */
    private fun handleApiResponse(
        response: ApiResponse<T>,
        listener: OnProcessResponseListener<T>
    ) {
        when (response) {
            is ApiResponse.Loading -> {
                // 加载状态已在requestData开始时设置，这里不需要额外处理
                if (debug) println("LoadMoreListFragmentViewModel: Loading...")
            }
            
            is ApiResponse.Success -> {
                handleSuccessResponse(response.data, listener)
            }
            
            is ApiResponse.Error -> {
                handleException(response.exception)
            }
        }
    }
    
    /**
     * 处理成功响应
     */
    private fun handleSuccessResponse(
        data: T?,
        listener: OnProcessResponseListener<T>
    ) {
        isLoading = false
        isNetworkError = false
        
        // 更新页码
        if (isRefresh) {
            currentPageIndex = 1
        } else {
            currentPageIndex += 1
        }
        
        // 检查数据是否为空且是刷新请求
        if (listener.isRequestSuccessButDataEmpty(data) && isRefresh) {
            listLiveData.value = arrayListOf()
            if (listener.isLoadMoreFinished(data)) {
                eventLiveData.value = EntityUIEvent(EntityUIEvent.EventType.LOAD_FINISH)
            }
        } else {
            // 更新列表数据
            listLiveData.value = listener.obtainListForAdapter(
                data,
                listLiveData.value,
                isRefresh
            )
            
            // 检查是否加载完成
            if (listener.isLoadMoreFinished(data)) {
                eventLiveData.value = EntityUIEvent(EntityUIEvent.EventType.LOAD_FINISH)
            } else {
                eventLiveData.value = EntityUIEvent(EntityUIEvent.EventType.LOADING)
            }
        }
    }
    
    /**
     * 处理异常
     */
    private fun handleException(exception: Exception) {
        isLoading = false
        isNetworkError = true
        
        if (debug) {
            println("LoadMoreListFragmentViewModel Error: ${exception.message}")
            exception.printStackTrace()
        }
        
        if (isRefresh) {
            // 刷新失败，清空列表
            listLiveData.value = null
        } else {
            // 加载更多失败
            eventLiveData.value = EntityUIEvent(EntityUIEvent.EventType.LOAD_MORE_FAILED)
        }
    }

    /**
     * 判断是否正在请求
     */
    fun isRequesting(): Boolean {
        return isLoading
    }

    /**
     * onDestroy()中取消请求
     */
    fun cancelRequest() {
        requestJob?.cancel()
        requestJob = null
        isLoading = false
    }
    
    /**
     * 获取当前网络错误状态
     */
    fun getNetworkErrorStatus(): Boolean {
        return isNetworkError
    }
    
    /**
     * 重置网络错误状态
     */
    fun resetNetworkErrorStatus() {
        isNetworkError = false
    }
    
    override fun onCleared() {
        super.onCleared()
        cancelRequest()
    }
}