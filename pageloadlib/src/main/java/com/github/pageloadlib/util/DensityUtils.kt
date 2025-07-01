package com.github.pageloadlib.util

import android.content.Context
import android.util.TypedValue

class DensityUtils private constructor() {
    companion object {
        fun dp2px(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.resources.displayMetrics
            ).toInt()
        }

        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spVal, context.resources.displayMetrics
            ).toInt()
        }

        fun px2dp(context: Context, pxVal: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxVal / scale
        }

        fun px2sp(context: Context, pxVal: Float): Float {
            return pxVal / context.resources.displayMetrics.scaledDensity
        }

        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        fun getScreenHeight(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }

        fun getStatusBarHeight(context: Context): Int {
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return context.resources.getDimensionPixelSize(resourceId)
        }

        fun getTitleBarHeight(context: Context): Int {
            return dp2px(context, 56f)
        }
    }

    init {
        throw UnsupportedOperationException("cannot be instantiated")
    }
}