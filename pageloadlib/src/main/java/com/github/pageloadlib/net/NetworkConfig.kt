package com.github.pageloadlib.net

/**
 * 网络配置类
 * 用于管理网络请求的基础配置
 */
data class NetworkConfig(
    val baseUrl: String,
    val connectTimeout: Long = 30L,
    val readTimeout: Long = 30L,
    val writeTimeout: Long = 30L,
    val enableLogging: Boolean = true,
    val retryOnConnectionFailure: Boolean = true
) 