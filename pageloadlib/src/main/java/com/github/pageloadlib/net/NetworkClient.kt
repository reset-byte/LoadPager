package com.github.pageloadlib.net

import com.github.pageloadlib.net.interceptor.HeaderInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络客户端管理类
 * 用于配置和创建Retrofit实例
 */
class NetworkClient private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: NetworkClient? = null
        
        fun getInstance(): NetworkClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkClient().also { INSTANCE = it }
            }
        }
    }
    
    private var config: NetworkConfig? = null
    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private var gson: Gson? = null
    
    /**
     * 初始化网络配置
     */
    fun init(config: NetworkConfig): NetworkClient {
        this.config = config
        this.okHttpClient = null
        this.retrofit = null
        return this
    }
    
    /**
     * 获取OkHttpClient实例
     */
    private fun getOkHttpClient(): OkHttpClient {
        return okHttpClient ?: createOkHttpClient().also { okHttpClient = it }
    }
    
    /**
     * 获取Retrofit实例
     */
    fun getRetrofit(): Retrofit {
        return retrofit ?: createRetrofit().also { retrofit = it }
    }
    
    /**
     * 创建API服务
     */
    inline fun <reified T> createService(): T {
        return getRetrofit().create(T::class.java)
    }
    
    /**
     * 创建OkHttpClient
     */
    private fun createOkHttpClient(): OkHttpClient {
        val currentConfig = config ?: throw IllegalStateException("NetworkClient not initialized")
        
        val builder = OkHttpClient.Builder()
            .connectTimeout(currentConfig.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(currentConfig.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(currentConfig.writeTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(currentConfig.retryOnConnectionFailure)
        
        // 添加请求头拦截器
        builder.addInterceptor(HeaderInterceptor())
        
        // 添加日志拦截器
        if (currentConfig.enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        return builder.build()
    }
    
    /**
     * 创建Retrofit实例
     */
    private fun createRetrofit(): Retrofit {
        val currentConfig = config ?: throw IllegalStateException("NetworkClient not initialized")
        val currentGson = gson ?: createDefaultGson()
        
        return Retrofit.Builder()
            .baseUrl(currentConfig.baseUrl)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(currentGson))
            .build()
    }
    
    /**
     * 创建默认Gson实例
     */
    private fun createDefaultGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
    }
} 