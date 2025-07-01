package com.github.pageloadlib.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.github.pageloadlib.R
import com.github.pageloadlib.config.PlaceholderViewStyleConfig
import com.github.pageloadlib.util.DensityUtils.Companion.dp2px
import com.github.pageloadlib.widget.ErrorTipView

/**
 * 全局加载和错误页面，请勿外部使用！
 */
@SuppressLint("ViewConstructor")
class DefaultStatusView(
    context: Context,
    private val retryTask: Runnable?,
    config: PlaceholderViewStyleConfig? = null,
    private var bottomPadding: Int = 0
) : FrameLayout(context), View.OnClickListener {

    private val loadingView: CommonLoadingView
    private val errorTipView: ErrorTipView
    private var placeholderConfig: PlaceholderViewStyleConfig

    fun setStatus(status: LoadingStatus?) {
        var show = true
        when (status) {
            LoadingStatus.STATUS_LOAD_SUCCESS -> show = false
            LoadingStatus.STATUS_LOADING -> {
                errorTipView.visibility = GONE
                loadingView.visibility = VISIBLE
            }
            LoadingStatus.STATUS_LOAD_FAILED -> {
                errorTipView.visibility = VISIBLE
                loadingView.visibility = GONE
                errorTipView.setImageResource(placeholderConfig.requestErrorIconDrawable)
                errorTipView.setRetryBgResource(placeholderConfig.requestErrorRetryBgDrawable)
                if (placeholderConfig.requestErrorTipText == null) {
                    if (placeholderConfig.requestErrorTipTextResId > 0) {
                        errorTipView.setTipText(placeholderConfig.requestErrorTipTextResId)
                    } else {
                        errorTipView.setTipText("")
                    }
                } else {
                    errorTipView.setTipText(placeholderConfig.requestErrorTipText)
                }
                if (placeholderConfig.requestErrorRetryText.isNullOrBlank()) {
                    if (placeholderConfig.requestErrorRetryTextResId > 0) {
                        errorTipView.setRetryVisibility(VISIBLE)
                        errorTipView.setRetryText(placeholderConfig.requestErrorRetryTextResId)
                    } else {
                        errorTipView.setRetryVisibility(GONE)
                    }
                } else {
                    errorTipView.setRetryVisibility(VISIBLE)
                    errorTipView.setRetryText(placeholderConfig.requestErrorRetryText)
                }
            }
            LoadingStatus.STATUS_EMPTY_DATA -> {
                errorTipView.visibility = VISIBLE
                loadingView.visibility = GONE
                errorTipView.setImageResource(placeholderConfig.emptyIconDrawable)
                errorTipView.setRetryBgResource(placeholderConfig.emptyRetryBgDrawable)
                if (placeholderConfig.emptyTipText == null) {
                    if (placeholderConfig.emptyTipTextResId > 0) {
                        errorTipView.setTipText(placeholderConfig.emptyTipTextResId)
                    } else {
                        errorTipView.setTipText("")
                    }
                } else {
                    errorTipView.setTipText(placeholderConfig.emptyTipText)
                }
                if (placeholderConfig.emptyRetryText.isNullOrBlank()) {
                    if (placeholderConfig.emptyRetryTextResId > 0) {
                        errorTipView.setRetryVisibility(VISIBLE)
                        errorTipView.setRetryText(placeholderConfig.emptyRetryTextResId)
                    } else {
                        errorTipView.setRetryVisibility(GONE)
                    }
                } else {
                    errorTipView.setRetryVisibility(VISIBLE)
                    errorTipView.setRetryText(placeholderConfig.emptyRetryText)
                }
            }
            LoadingStatus.STATUS_NET_ERROR -> {
                errorTipView.visibility = VISIBLE
                loadingView.visibility = GONE
                errorTipView.setImageResource(placeholderConfig.networkErrorIconDrawable)
                errorTipView.setRetryBgResource(placeholderConfig.networkErrorRetryBgDrawable)
                if (placeholderConfig.networkErrorTipText == null) {
                    if (placeholderConfig.networkErrorTipTextResId > 0) {
                        errorTipView.setTipText(placeholderConfig.networkErrorTipTextResId)
                    } else {
                        errorTipView.setTipText("")
                    }
                } else {
                    errorTipView.setTipText(placeholderConfig.networkErrorTipText)
                }
                if (placeholderConfig.networkErrorRetryText.isNullOrBlank()) {
                    if (placeholderConfig.networkErrorRetryTextResId > 0) {
                        errorTipView.setRetryVisibility(VISIBLE)
                        errorTipView.setRetryText(placeholderConfig.networkErrorRetryTextResId)
                    } else {
                        errorTipView.setRetryVisibility(GONE)
                    }
                } else {
                    errorTipView.setRetryVisibility(VISIBLE)
                    errorTipView.setRetryText(placeholderConfig.networkErrorRetryText)
                }
            }
            else -> {
            }
        }
        visibility = if (show) VISIBLE else GONE
    }

    override fun onClick(v: View) {
        retryTask?.run()
    }

    init {
        View.inflate(context, R.layout.common_ui_layout_global_loading_status, this)
        loadingView = findViewById(R.id.page_loading_view)
        errorTipView = findViewById(R.id.page_error_view)
        placeholderConfig = config ?: PlaceholderViewStyleConfig()
        bottomPadding = dp2px(context, bottomPadding.toFloat())
        placeholderConfig.imageTipMarginDp?.let {
            errorTipView.setFirstMargin(dp2px(context, it))
        }
        placeholderConfig.tipRetryBtnMarginDp?.let {
            errorTipView.setSecondMargin(dp2px(context, it))
        }
        placeholderConfig.retryWidthDp?.let {
            errorTipView.setRetryWidth(dp2px(context, it))
        }
        placeholderConfig.retryHeightDp?.let {
            errorTipView.setRetryHeight(dp2px(context, it))
        }
        errorTipView.setTipTextColor(placeholderConfig.tipsTextColor)
        errorTipView.setTipTextSize(placeholderConfig.tipsTextSize)
        errorTipView.setRetryTextColor(placeholderConfig.retryTextColor)
        errorTipView.setRetryTextSize(placeholderConfig.retryTextSize)
        errorTipView.setOnClickListener(this)
        setPadding(0, 0, 0, bottomPadding)
    }
}