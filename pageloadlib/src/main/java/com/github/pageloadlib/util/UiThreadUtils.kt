/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.pageloadlib.util

import android.os.Handler
import android.os.Looper

/**
 * Utility for interacting with the UI thread.
 * @author open source
 */
object UiThreadUtils {

    private var sMainHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * @return `true` if the current thread is the UI thread.
     */
    val isUiThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()

    /**
     * Runs the given `Runnable` on the UI thread.
     */
    fun post(runnable: Runnable) {
        sMainHandler.post(runnable)
    }

    /**
     * Runs the given `Runnable` on the UI thread.
     */
    fun post(runnable: Runnable, delayMillis: Int) {
        sMainHandler.postDelayed(runnable, delayMillis.toLong())
    }

    /**
     * remove exist runnable that not handle.
     */
    fun removeCallbacks(runnable: Runnable) {
        sMainHandler.removeCallbacks(runnable)
    }
}
