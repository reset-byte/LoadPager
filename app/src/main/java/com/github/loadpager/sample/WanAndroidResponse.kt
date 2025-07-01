package com.github.loadpager.sample

/**
 * 玩Android API响应数据模型
 */
data class WanAndroidResponse(
    val errorCode: Int,
    val errorMsg: String,
    val data: ArticlePageData
)

/**
 * 文章分页数据模型
 */
data class ArticlePageData(
    val curPage: Int,
    val datas: List<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
) 