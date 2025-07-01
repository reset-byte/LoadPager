package com.github.pageloadlib.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.github.pageloadlib.R;

/**
 * CommonLoadingView
 */
public class CommonLoadingView extends FrameLayout {
    private LottieAnimationView mLottieView;

    public CommonLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public CommonLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int size = DensityUtils.Companion.dp2px(context, 40);
        mLottieView = (LottieAnimationView) LayoutInflater.from(context).inflate(R.layout.common_ui_loading_content, this, false);
        if (mLottieView != null) {
            LayoutParams layoutParams = new LayoutParams(size, size);
            layoutParams.gravity = Gravity.CENTER;
            mLottieView.setLayoutParams(layoutParams);
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.common_ui_CommonLoadingView);
            int rawResId = ta.getResourceId(R.styleable.common_ui_CommonLoadingView_common_ui_loading_anim, 0);
            if (rawResId != 0) {
                mLottieView.setAnimation(rawResId);
            }
            ta.recycle();
            addView(mLottieView);
        }
    }

    public void setLoadingAnimRes(@RawRes int rawResId) {
        if (rawResId != 0) {
            mLottieView.setAnimation(rawResId);
        }
    }

    public void attach(RelativeLayout rootView, boolean blockViewsBelow) {
        if (rootView != null) {
            if (blockViewsBelow) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                setLayoutParams(layoutParams);
                setClickable(true);
            }
            rootView.addView(this);
        }
    }

    public void attach(FrameLayout rootView, boolean blockViewsBelow) {
        if (rootView != null) {
            if (blockViewsBelow) {
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                setLayoutParams(layoutParams);
                setClickable(true);
            }
            rootView.addView(this);
        }
    }

    public void attach(ConstraintLayout rootView, boolean blockViewsBelow) {
        if (rootView != null) {
            if (blockViewsBelow) {
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                setLayoutParams(layoutParams);
                setClickable(true);
            }
            rootView.addView(this);
        }
    }

    public void attach(Activity activity, boolean blockViewsBelow) {
        if (activity != null) {
            FrameLayout root = activity.findViewById(android.R.id.content);
            attach(root, blockViewsBelow);
        }
    }

    public void hide() {
        if (mLottieView != null) {
            mLottieView.pauseAnimation();
        }
        super.setVisibility(View.GONE);
    }

    public void show() {
        if (mLottieView != null) {
            mLottieView.playAnimation();
        }
        super.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            show();
        } else {
            hide();
        }
    }
}
