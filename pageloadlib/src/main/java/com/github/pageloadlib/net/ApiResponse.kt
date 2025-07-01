package com.github.pageloadlib.net

/**
 * API响应封装类
 * 用于统一处理网络请求的响应结果
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Exception) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
}

/**
 * 通用API响应数据结构
 */
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

/**
 * 分页响应数据结构
 */
data class PageResponse<T>(
    val list: List<T>,
    val pageNum: Int,
    val pageSize: Int,
    val total: Long,
    val hasNextPage: Boolean
) 