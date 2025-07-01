package com.github.pageloadlib.net.repository

import com.github.pageloadlib.net.ApiResponse
import com.github.pageloadlib.net.BaseResponse
import com.github.pageloadlib.net.NetworkException
import com.github.pageloadlib.net.NetworkExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * Repository基类
 * 提供网络请求的通用处理方法
 */
abstract class BaseRepository {
    
    /**
     * 执行网络请求
     * @param apiCall 网络请求方法
     * @return Flow<ApiResponse<T>>
     */
    protected suspend fun <T> executeRequest(
        apiCall: suspend () -> Response<BaseResponse<T>>
    ): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == SUCCESS_CODE) {
                    body.data?.let {
                        emit(ApiResponse.Success(it))
                    } ?: emit(ApiResponse.Error(NetworkException.JsonParseException("数据为空")))
                } else {
                    emit(ApiResponse.Error(
                        NetworkException.BusinessException(
                            body?.code ?: UNKNOWN_ERROR_CODE,
                            body?.message ?: "业务异常"
                        )
                    ))
                }
            } else {
                emit(ApiResponse.Error(
                    NetworkException.HttpException(response.code(), response.message())
                ))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(NetworkExceptionHandler.handleException(e)))
        }
    }
    
    /**
     * 执行简单网络请求（不包装BaseResponse）
     */
    protected suspend fun <T> executeSimpleRequest(
        apiCall: suspend () -> Response<T>
    ): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ApiResponse.Success(it))
                } ?: emit(ApiResponse.Error(NetworkException.JsonParseException("数据为空")))
            } else {
                emit(ApiResponse.Error(
                    NetworkException.HttpException(response.code(), response.message())
                ))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(NetworkExceptionHandler.handleException(e)))
        }
    }
    
    /**
     * 执行直接返回数据的网络请求
     */
    protected suspend fun <T> executeDirectRequest(
        apiCall: suspend () -> T
    ): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading)
        try {
            val result = apiCall()
            emit(ApiResponse.Success(result))
        } catch (e: Exception) {
            emit(ApiResponse.Error(NetworkExceptionHandler.handleException(e)))
        }
    }
    
    companion object {
        private const val SUCCESS_CODE = 200
        private const val UNKNOWN_ERROR_CODE = -1
    }
} 