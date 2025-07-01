package com.github.pageloadlib.util

import android.view.View

class ErrorViewAdapter: JLoading.Adapter {

    override fun getView(
        holder: JLoading.Holder,
        convertView: View?,
        status: LoadingStatus
    ): View? {
        var statusView: DefaultStatusView? = null
        if (convertView is DefaultStatusView) {
            statusView = convertView
        }
        if (statusView == null) {
            statusView = DefaultStatusView(holder.context, holder.retryTask, holder.errorViewConfig, holder.bottomPadding)
        }
        statusView.setStatus(status)
        return statusView
    }
}