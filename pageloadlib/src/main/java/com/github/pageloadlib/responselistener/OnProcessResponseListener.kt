package com.github.pageloadlib.responselistener

/**
 * 用于处理请求返回的数据，将数据转换为Adapter需要的数据
 */
interface OnProcessResponseListener<T> {

    /**
     * 获取的列表数据提供给Adapter渲染
     * @param response 返回数据
     * @param currentItems 当前列表数据
     * @param isRefresh 是否刷新
     */
    fun obtainListForAdapter(response: T?, currentItems: List<Any>?, isRefresh: Boolean): List<Any>

    /**
     * 判断请求接口数据成功但是返回数据列表是[]
     * @param response 返回数据
     */
    fun isRequestSuccessButDataEmpty(response: T?): Boolean

    /**
     * 判断加载更多完成
     * @param response 返回数据
     */
    fun isLoadMoreFinished(response: T?): Boolean

}