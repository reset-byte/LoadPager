package com.github.pageloadlib.net

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * API扩展函数
 * 提供便捷的网络请求方法
 */

/**
 * ApiResponse扩展函数：获取数据或抛出异常
 */
fun <T> ApiResponse<T>.getDataOrThrow(): T {
    return when (this) {
        is ApiResponse.Success -> data
        is ApiResponse.Error -> throw exception
        is ApiResponse.Loading -> throw IllegalStateException("数据仍在加载中")
    }
}

/**
 * ApiResponse扩展函数：获取数据或返回默认值
 */
fun <T> ApiResponse<T>.getDataOrDefault(defaultValue: T): T {
    return when (this) {
        is ApiResponse.Success -> data
        else -> defaultValue
    }
}

/**
 * ApiResponse扩展函数：检查是否成功
 */
fun <T> ApiResponse<T>.isSuccess(): Boolean {
    return this is ApiResponse.Success
}

/**
 * ApiResponse扩展函数：检查是否失败
 */
fun <T> ApiResponse<T>.isError(): Boolean {
    return this is ApiResponse.Error
}

/**
 * ApiResponse扩展函数：检查是否加载中
 */
fun <T> ApiResponse<T>.isLoading(): Boolean {
    return this is ApiResponse.Loading
}

/**
 * Flow扩展函数：处理网络请求的通用错误
 */
fun <T> Flow<ApiResponse<T>>.handleNetworkError(
    onError: (NetworkException) -> Unit = {}
): Flow<ApiResponse<T>> {
    return this.catch { throwable ->
        val networkException = NetworkExceptionHandler.handleException(throwable)
        onError(networkException)
        emit(ApiResponse.Error(networkException))
    }
}

/**
 * Flow扩展函数：只发送成功的数据
 */
fun <T> Flow<ApiResponse<T>>.onlySuccess(): Flow<T> {
    return this.map { response ->
        when (response) {
            is ApiResponse.Success -> response.data
            is ApiResponse.Error -> throw response.exception
            is ApiResponse.Loading -> throw IllegalStateException("数据仍在加载中")
        }
    }
}

/**
 * Flow扩展函数：添加加载状态
 */
fun <T> Flow<T>.asApiResponse(): Flow<ApiResponse<T>> {
    return this.map<T, ApiResponse<T>> { ApiResponse.Success(it) }
        .onStart { emit(ApiResponse.Loading) }
        .catch { emit(ApiResponse.Error(NetworkExceptionHandler.handleException(it))) }
} 