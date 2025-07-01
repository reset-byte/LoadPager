package com.github.pageloadlib.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.github.pageloadlib.R
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

/**
 * created by yangtianbin on 2023/3/16 6:16 PM
 */
class AndroidViewHeader : LinearLayout, RefreshHeader {

    private var mAct: Activity? = null
    private var mLottieAnimationView: LottieAnimationView? = null
    private var mHasInit = false

    private var kernel: RefreshKernel? = null

    private var mDesColor: Int = 0

    constructor(context: Context):super(context){
        mAct=context as Activity
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, def: Int) : super(context, attr, def)

    private fun initLottie() {
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,400)
        mLottieAnimationView = LottieAnimationView(mAct)
        mLottieAnimationView!!.layoutParams = layoutParams
        mLottieAnimationView!!.loop(true)
        mLottieAnimationView?.setFailureListener { t ->
            Log.d("xxxxxxxxx","error-> $t")
        }
        mLottieAnimationView?.setAnimation(R.raw.common_ui_loading_progress)
        gravity = Gravity.CENTER
        orientation = VERTICAL
        addView(mLottieAnimationView)
    }


    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None -> {}
            RefreshState.PullDownToRefresh -> {}
            RefreshState.PullUpToLoad -> {}
            RefreshState.PullDownCanceled -> {}
            RefreshState.PullUpCanceled -> {}
            RefreshState.ReleaseToRefresh -> {}
            RefreshState.ReleaseToLoad -> {}
            RefreshState.ReleaseToTwoLevel -> {}
            RefreshState.TwoLevelReleased -> {}
            RefreshState.RefreshReleased -> {}
            RefreshState.LoadReleased -> {}
            RefreshState.Refreshing -> {}
            RefreshState.Loading -> {}
            RefreshState.TwoLevel -> {}
            RefreshState.RefreshFinish -> {}
            RefreshState.LoadFinish -> {}
            RefreshState.TwoLevelFinish -> {}
        }
    }

    override fun getView() = this

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {

    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        this.kernel = kernel
        kernel.requestDrawBackgroundFor(
            this,
            if (mDesColor != 0) mDesColor else Color.TRANSPARENT
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        if (!mHasInit) {
            initLottie()
            mHasInit = true;
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        Log.d("xxxxxxxxx","onReleased")
        mLottieAnimationView?.playAnimation();
    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        return 0
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag() = false
    
    override fun autoOpen(duration: Int, dragRate: Float, animationOnly: Boolean): Boolean {
        return false
    }
}