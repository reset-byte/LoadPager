package com.github.pageloadlib.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.github.pageloadlib.R

class CommonProgressDialog(context: Context, themeResId: Int = R.style.common_ui_Progress_Dialog) :
    Dialog(context, themeResId) {
    private val loadingView = CommonLoadingView(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(loadingView)
    }

    override fun onStart() {
        super.onStart()
        loadingView.show()
    }

    override fun onStop() {
        super.onStop()
        loadingView.hide()
    }
}