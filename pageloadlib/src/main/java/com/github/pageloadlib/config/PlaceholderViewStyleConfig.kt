package com.github.pageloadlib.config

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.pageloadlib.R

/**
 * 用于配置兜底页相关变量配置
 */

class PlaceholderViewStyleConfig {

    /**
     * 加载失败页面兜底图
     * 0表示不设置
     */
    @DrawableRes
    var requestErrorIconDrawable = 0

    /**
     * 加载失败页面提示文案
     */
    var requestErrorTipText: String? = null

    /**
     * 加载失败页面重试按钮文案
     */
    var requestErrorRetryText: String? = null

    /**
     * 加载失败页面提示文案
     */
    @StringRes
    var requestErrorTipTextResId: Int = R.string.common_ui_error_view_request_error_tip

    /**
     * 加载失败页面重试按钮文案
     */
    @StringRes
    var requestErrorRetryTextResId: Int = R.string.common_ui_error_view_retry_btn_text

    /**
     * 加载失败页面重试按钮背景
     */
    @get:DrawableRes
    var requestErrorRetryBgDrawable = R.drawable.common_ui_default_retry_btn_bg

    /**
     * 空页面兜底图
     */
    @get:DrawableRes
    var emptyIconDrawable = R.drawable.common_ui_list_search_empty

    /**
     * 空页面提示文案
     */
    var emptyTipText: String? = null

    /**
     * 空页面重试按钮文案
     */
    var emptyRetryText: String? = null

    /**
     * 空页面提示文案Id
     */
    @StringRes
    var emptyTipTextResId: Int = R.string.common_ui_error_view_empty_text

    /**
     * 空页面重试按钮文案Id，默认不展示重试文案
     */
    @StringRes
    var emptyRetryTextResId: Int = -1

    /**
     * 空页面重试按钮背景
     */
    @get:DrawableRes
    var emptyRetryBgDrawable = R.drawable.common_ui_default_retry_btn_bg

    /**
     * 无网络页面兜底图，0表示不设置
     */
    @get:DrawableRes
    var networkErrorIconDrawable = R.drawable.common_ui_request_failed_placeholder

    /**
     * 无网络页面提示文案
     */
    var networkErrorTipText: String? = null

    /**
     * 无网络页面重试按钮文案
     */
    var networkErrorRetryText: String? = null

    /**
     * 无网络页面重试按钮背景
     */
    @get:DrawableRes
    var networkErrorRetryBgDrawable = R.drawable.common_ui_default_refresh_btn_bg

    /**
     * 无网络页面提示文案Id
     */
    @StringRes
    var networkErrorTipTextResId: Int = R.string.common_ui_error_view_network_error_tip

    /**
     * 无网络页面重试按钮文案Id
     */
    @StringRes
    var networkErrorRetryTextResId: Int = R.string.common_ui_error_view_network_error_refresh

    /**
     * 展位图片到文字的距离
     */
    @Dimension(unit = Dimension.DP)
    var imageTipMarginDp: Float? = 12f

    /**
     * 重试按钮到文字的距离
     */
    @Dimension(unit = Dimension.DP)
    var tipRetryBtnMarginDp: Float? = 10f

    /**
     * 重试按钮高度
     */
    @Dimension(unit = Dimension.DP)
    var retryHeightDp: Float? = 30f

    /**
     * 重试按钮宽度
     */
    @Dimension(unit = Dimension.DP)
    var retryWidthDp: Float? = 72f

    /**
     * tips文案颜色
     */
    @ColorInt
    var tipsTextColor: Int = Color.parseColor("#999999")

    /**
     * tips文案字体大小
     */
    @Dimension(unit = Dimension.SP)
    var tipsTextSize: Int = 14

    /**
     * 重试按钮文字颜色
     */
    @ColorInt
    var retryTextColor: Int = Color.parseColor("#666666")

    /**
     * 重试按钮文字大小
     */
    @Dimension(unit = Dimension.SP)
    var retryTextSize: Int = 14
}