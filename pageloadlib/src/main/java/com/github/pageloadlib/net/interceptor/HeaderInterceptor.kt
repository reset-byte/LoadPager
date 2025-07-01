package com.github.pageloadlib.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 请求头拦截器
 * 用于添加公共请求头
 */
class HeaderInterceptor(
    private val headers: Map<String, String> = emptyMap()
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
        
        // 添加公共请求头
        headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }
        
        // 添加默认请求头
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        
        return chain.proceed(builder.build())
    }
} 