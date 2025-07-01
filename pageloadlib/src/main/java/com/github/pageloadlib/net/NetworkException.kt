package com.github.pageloadlib.net

import java.io.IOException

/**
 * 网络异常基类
 */
sealed class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * 网络连接异常
     */
    class NetworkConnectionException(message: String = "网络连接失败", cause: Throwable? = null) : 
        NetworkException(message, cause)
    
    /**
     * 超时异常
     */
    class TimeoutException(message: String = "请求超时", cause: Throwable? = null) : 
        NetworkException(message, cause)
    
    /**
     * HTTP异常
     */
    class HttpException(val code: Int, message: String = "HTTP错误: $code", cause: Throwable? = null) : 
        NetworkException(message, cause)
    
    /**
     * JSON解析异常
     */
    class JsonParseException(message: String = "数据解析失败", cause: Throwable? = null) : 
        NetworkException(message, cause)
    
    /**
     * 业务逻辑异常
     */
    class BusinessException(val code: Int, message: String, cause: Throwable? = null) : 
        NetworkException(message, cause)
    
    /**
     * 未知异常
     */
    class UnknownException(message: String = "未知错误", cause: Throwable? = null) : 
        NetworkException(message, cause)
}

/**
 * 网络异常处理工具类
 */
object NetworkExceptionHandler {
    
    fun handleException(throwable: Throwable): NetworkException {
        return when (throwable) {
            is NetworkException -> throwable
            is java.net.SocketTimeoutException -> NetworkException.TimeoutException(cause = throwable)
            is java.net.ConnectException -> NetworkException.NetworkConnectionException(cause = throwable)
            is java.net.UnknownHostException -> NetworkException.NetworkConnectionException("网络不可用", throwable)
            is IOException -> NetworkException.NetworkConnectionException("网络异常", throwable)
            is com.google.gson.JsonSyntaxException -> NetworkException.JsonParseException(cause = throwable)
            is retrofit2.HttpException -> NetworkException.HttpException(throwable.code(), throwable.message(), throwable)
            else -> NetworkException.UnknownException(throwable.message ?: "未知错误", throwable)
        }
    }
} 