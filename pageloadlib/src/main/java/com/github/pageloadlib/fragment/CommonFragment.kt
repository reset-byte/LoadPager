package com.github.pageloadlib.fragment

import android.content.DialogInterface
import android.view.View
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import com.github.pageloadlib.config.PlaceholderViewStyleConfig
import com.github.pageloadlib.util.CommonProgressDialog
import com.github.pageloadlib.util.ErrorViewAdapter
import com.github.pageloadlib.util.JLoading

abstract class CommonFragment : Fragment() {
    private var loadingHolder: JLoading.Holder? = null
    private var progressDialog: CommonProgressDialog? = null

    private fun initStatusViewIfNeed() {
        if (loadingHolder == null && getLoadingWrapperView() != null) {
            JLoading.getDefault().adapter ?: JLoading.getDefault().setAdapter(
                ErrorViewAdapter()
            )
            if (getLoadingWrapperView() == view) {
                loadingHolder = JLoading.getDefault().cover(getLoadingWrapperView())
                        .withRetry { onLoadRetry() }
            } else {
                loadingHolder = JLoading.getDefault().wrap(getLoadingWrapperView())
                        .withRetry { onLoadRetry() }
            }
        }
    }

    fun setErrorViewConfig(errorViewConfig: PlaceholderViewStyleConfig?) {
        initStatusViewIfNeed()
        loadingHolder?.errorViewConfig = errorViewConfig
    }

    fun setLoadingErrorViewBottomPadding(bottomPaddingDp: Int) {
        initStatusViewIfNeed()
        loadingHolder?.bottomPadding = bottomPaddingDp
    }

    fun setLoadingViewAnimation(@RawRes animResId: Int) {
        initStatusViewIfNeed()
        loadingHolder?.loadingAnimResId = animResId
    }

    open fun onLoadRetry() {
    }

    /**
     * 加载动画和错误页面所在的view
     */
    open fun getLoadingWrapperView(): View? {
        return view
    }

    fun showLoadingView() {
        initStatusViewIfNeed()
        loadingHolder?.showLoading()
    }

    fun hideLoadingView() {
        initStatusViewIfNeed()
        loadingHolder?.showLoadSuccess()
    }

    fun showLoadFailed() {
        initStatusViewIfNeed()
        loadingHolder?.showLoadFailed()
    }

    fun showNetError() {
        initStatusViewIfNeed()
        loadingHolder?.showNetError()
    }

    fun showEmpty() {
        initStatusViewIfNeed()
        loadingHolder?.showEmpty()
    }

    fun isLoading(): Boolean {
        initStatusViewIfNeed()
        if (loadingHolder == null) {
            return false
        }
        return loadingHolder!!.isLoading
    }

//    fun showMessage(@StringRes stringRes: Int, iconType: IconType = IconType.HIDE) {
//        showMessage(resources.getString(stringRes), iconType)
//    }

//    fun showMessage(msg: String, iconType: IconType = IconType.HIDE) {
//        when (iconType) {
//            IconType.HIDE -> showSnackBar(msg)
//            else -> showSnackBar(msg, iconType)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingHolder?.destroy()
        loadingHolder = null
    }

    fun showLoadingDialog(
        isCancelable: Boolean,
        onCancelListener: DialogInterface.OnCancelListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = null
    ) {
        if (progressDialog == null) {
            progressDialog = CommonProgressDialog(requireContext())
        }
        progressDialog?.let {
            try {
                hideLoadingDialog()
                if (!it.isShowing) {
                    it.setCanceledOnTouchOutside(false)
                    it.setCancelable(isCancelable)
                    it.setOnCancelListener(onCancelListener)
                    it.setOnDismissListener(onDismissListener)
                    it.show()
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun hideLoadingDialog() {
        progressDialog?.let {
            try {
                it.dismiss()
            } catch (ignored: Exception) {

            }
        }
    }
}