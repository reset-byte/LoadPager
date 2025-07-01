package com.github.pageloadlib.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import androidx.annotation.RawRes;
import com.github.pageloadlib.config.PlaceholderViewStyleConfig;

import java.util.HashMap;

public class JLoading {
    private static volatile JLoading mDefault;
    private static boolean DEBUG = false;
    private Adapter mAdapter;

    private JLoading() {
    }

    /**
     * set debug mode or not
     *
     * @param debug true:debug mode, false:not debug mode
     */
    public static void debug(boolean debug) {
        DEBUG = debug;
    }

    /**
     * Create a new JLoading different from the default one
     *
     * @param adapter another adapter different from the default one
     * @return JLoading
     */
    public static JLoading from(Adapter adapter) {
        JLoading jloading = new JLoading();
        jloading.mAdapter = adapter;
        return jloading;
    }

    /**
     * get default JLoading object for global usage in whole app
     *
     * @return default JLoading object
     */
    public static JLoading getDefault() {
        if (mDefault == null) {
            synchronized (JLoading.class) {
                if (mDefault == null) {
                    mDefault = new JLoading();
                }
            }
        }
        return mDefault;
    }

    /**
     * init the default loading status view creator ({@link Adapter})
     *
     * @param adapter adapter to create all status views
     */
    public static void initDefault(Adapter adapter) {
        getDefault().mAdapter = adapter;
    }

    private static void printLog(String msg) {
        if (DEBUG) {
            Log.e("JLoading", msg);
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    /**
     * JLoading(loading status view) wrap the whole activity
     * wrapper is android.R.id.content
     *
     * @param activity current activity object
     * @return holder of JLoading
     */
    public Holder wrap(Activity activity) {
        ViewGroup wrapper = activity.findViewById(android.R.id.content);
        return new Holder(mAdapter, activity, wrapper);
    }

    /**
     * JLoading(loading status view) wrap the specific view.
     *
     * @param view view to be wrapped
     * @return Holder
     */
    public Holder wrap(View view) {
        FrameLayout wrapper = new FrameLayout(view.getContext());
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            wrapper.setLayoutParams(lp);
        }
        if (view.getParent() != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(wrapper, index);
        }
        LayoutParams newLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        wrapper.addView(view, newLp);
        return new Holder(mAdapter, view.getContext(), wrapper);
    }

    /**
     * loadingStatusView shows cover the view with the same LayoutParams object
     * this method is useful with RelativeLayout and ConstraintLayout
     *
     * @param view the view which needs show loading status
     * @return Holder
     */
    public Holder cover(View view) {
        ViewParent parent = view.getParent();
        if (parent == null) {
            throw new RuntimeException("view has no parent to show JLoading as cover!");
        }
        ViewGroup viewGroup = (ViewGroup) parent;
        FrameLayout wrapper = new FrameLayout(view.getContext());
        viewGroup.addView(wrapper, view.getLayoutParams());
        return new Holder(mAdapter, view.getContext(), wrapper);
    }

    /**
     * Provides view to show current loading status
     */
    public interface Adapter {
        /**
         * get view for current status
         *
         * @param holder      Holder
         * @param convertView The old view to reuse, if possible.
         * @param status      current status
         * @return status view to show. Maybe convertView for reuse.
         * @see Holder
         */
        View getView(Holder holder, View convertView, LoadingStatus status);
    }

    /**
     * JLoading holder<br>
     * create by {@link JLoading#wrap(Activity)} or {@link JLoading#wrap(View)}<br>
     * the core API for showing all status view
     */
    public static class Holder {
        private Adapter mAdapter;
        private Context mContext;
        private Runnable mRetryTask;
        private View mCurStatusView;
        private ViewGroup mWrapper;
        private LoadingStatus curState;
        private HashMap<LoadingStatus, View> mStatusViews = new HashMap<>(5);
        private Object mData;
        private PlaceholderViewStyleConfig mErrorViewConfig;
        private int bottomPadding;
        private int loadingAnimResId;

        private Holder(Adapter adapter, Context context, ViewGroup wrapper) {
            this.mAdapter = adapter;
            this.mContext = context;
            this.mWrapper = wrapper;
        }

        /**
         * set retry task when user click the retry button in load failed page
         *
         * @param task when user click in load failed UI, run this task
         * @return this
         */
        public Holder withRetry(Runnable task) {
            mRetryTask = task;
            return this;
        }

        public PlaceholderViewStyleConfig getErrorViewConfig() {
            return mErrorViewConfig;
        }

        public void setErrorViewConfig(PlaceholderViewStyleConfig errorViewConfig) {
            this.mErrorViewConfig = errorViewConfig;
        }

        public int getBottomPadding() {
            return bottomPadding;
        }

        public void setBottomPadding(int bottomPadding) {
            this.bottomPadding = bottomPadding;
        }

        @RawRes
        public int getLoadingAnimResId() {
            return loadingAnimResId;
        }

        public void setLoadingAnimResId(@RawRes int loadingAnimResId) {
            this.loadingAnimResId = loadingAnimResId;
        }

        /**
         * set extension data
         *
         * @param data extension data
         * @return this
         */
        public Holder withData(Object data) {
            this.mData = data;
            return this;
        }

        /**
         * restore origin view structure,must called in fragment onDestroyView,
         * otherwise fragment view could not be removed
         */
        public void destroy() {
            mAdapter = null;
            if (mWrapper.getChildCount() > 0) {
                View origin = mWrapper.getChildAt(0);
                mWrapper.removeAllViews();
                if (mWrapper.getParent() != null) {
                    ViewGroup parent = (ViewGroup) mWrapper.getParent();
                    int index = parent.indexOfChild(mWrapper);
                    parent.removeViewAt(index);
                    boolean isStatusView = false;
                    for (View value : mStatusViews.values()) {
                        if (value == origin) {
                            isStatusView = true;
                            break;
                        }
                    }
                    if (!isStatusView) {
                        parent.addView(origin, index, mWrapper.getLayoutParams());
                    }
                }
            }
            mWrapper = null;
        }

        public void showLoading() {
            showLoadingStatus(LoadingStatus.STATUS_LOADING);
        }

        public void showLoadSuccess() {
            showLoadingStatus(LoadingStatus.STATUS_LOAD_SUCCESS);
        }

        public void showLoadFailed() {
            showLoadingStatus(LoadingStatus.STATUS_LOAD_FAILED);
        }

        public void showNetError() {
            showLoadingStatus(LoadingStatus.STATUS_NET_ERROR);
        }

        public void showEmpty() {
            showLoadingStatus(LoadingStatus.STATUS_EMPTY_DATA);
        }

        public boolean isLoading() {
            return curState == LoadingStatus.STATUS_LOADING;
        }

        /**
         * Show specific status UI
         *
         * @param status status
         * @see #showLoading()
         * @see #showLoadFailed()
         * @see #showNetError()
         * @see #showLoadSuccess()
         * @see #showEmpty()
         */
        public void showLoadingStatus(LoadingStatus status) {
            if (curState == status || !validate()) {
                return;
            }
            curState = status;
            //first try to reuse status view
            View convertView = mStatusViews.get(status);
            if (convertView == null) {
                //secondly try to reuse current status view
                convertView = mCurStatusView;
            }
            try {
                //call customer adapter to get UI for specific status. convertView can be reused
                View view = mAdapter.getView(this, convertView, status);
                if (view == null) {
                    printLog(mAdapter.getClass().getName() + ".getView returns null");
                    return;
                }
                if (view != mCurStatusView || mWrapper.indexOfChild(view) < 0) {
                    if (mCurStatusView != null) {
                        mWrapper.removeView(mCurStatusView);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setElevation(Float.MAX_VALUE);
                    }
                    mWrapper.addView(view);
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (lp != null) {
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                } else if (mWrapper.indexOfChild(view) != mWrapper.getChildCount() - 1) {
                    // make sure loading status view at the front
                    view.bringToFront();
                }
                mCurStatusView = view;
                mStatusViews.put(status, view);
            } catch (Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }

        private boolean validate() {
            if (mAdapter == null) {
                printLog("JLoading.Adapter is not specified.");
            }
            if (mContext == null) {
                printLog("Context is null.");
            }
            if (mWrapper == null) {
                printLog("The mWrapper of loading status view is null.");
            }
            return mAdapter != null && mContext != null && mWrapper != null;
        }

        public Context getContext() {
            return mContext;
        }

        /**
         * get wrapper
         *
         * @return container of JLoading
         */
        public ViewGroup getWrapper() {
            return mWrapper;
        }

        /**
         * get retry task
         *
         * @return retry task
         */
        public Runnable getRetryTask() {
            return mRetryTask;
        }

        /**
         * get extension data
         *
         * @param <T> return type
         * @return data
         */
        public <T> T getData() {
            try {
                return (T) mData;
            } catch (Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
