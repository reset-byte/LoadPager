package com.github.loadpager.sample

/**
 * 玩Android文章数据模型
 * 对应API返回的文章数据结构
 */
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val niceDate: String,
    val niceShareDate: String,
    val link: String,
    val chapterName: String,
    val superChapterName: String,
    val desc: String,
    val descMd: String,
    val publishTime: Long,
    val shareDate: Long,
    val collect: Boolean,
    val zan: Int,
    val tags: List<Tag> = emptyList()
)

/**
 * 标签数据模型
 */
data class Tag(
    val name: String,
    val url: String
) 