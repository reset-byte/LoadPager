package com.github.pageloadlib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import com.github.pageloadlib.R;

/**
 * 当接口返回失败, 或网络原因造成无法获取数据时, 使用此view 向用户展示失败的原因
 * 并为用户提供点击重试的功能
 */
public class ErrorTipView extends LinearLayout implements View.OnClickListener {
    private int mFirstMargin = 0;
    private int mSecondMargin = 0;

    private int mIconResource = 0;
    private Drawable mIconDrawable = null;
    private ImageView mIvIcon;

    private TextView mTvTip;
    private int mTipTextSize;
    private CharSequence mTipText;
    private ColorStateList mTipTextColor;

    private TextView mTvRetry;
    private int mRetryTextSize;
    private CharSequence mRetryText;
    private ColorStateList mRetryTextColor;
    private Drawable mRetrybackground;

    private int mRetryWidth;
    private int mRetryHeight;

    private OnClickListener mListener;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ErrorTipView(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public ErrorTipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public ErrorTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ErrorTipView, defStyleAttr, 0);

        mIconDrawable = a.getDrawable(R.styleable.ErrorTipView_error_icon);

        mTipText = a.getText(R.styleable.ErrorTipView_tip_text);
        mTipTextColor = a.getColorStateList(R.styleable.ErrorTipView_tip_text_color);
        mTipTextSize = a.getDimensionPixelSize(R.styleable.ErrorTipView_tip_text_size, 0);

        mRetryText = a.getText(R.styleable.ErrorTipView_retry_text);
        mRetryTextColor = a.getColorStateList(R.styleable.ErrorTipView_retry_text_color);
        mRetryTextSize = a.getDimensionPixelSize(R.styleable.ErrorTipView_retry_text_size, 0);
        mRetrybackground = a.getDrawable(R.styleable.ErrorTipView_retry_background);

        mFirstMargin = a.getDimensionPixelSize(R.styleable.ErrorTipView_item_first_margin, 0);
        mSecondMargin = a.getDimensionPixelSize(R.styleable.ErrorTipView_item_second_margin, 0);

        mRetryWidth = a.getDimensionPixelSize(R.styleable.ErrorTipView_retry_width, -2);
        mRetryHeight = a.getDimensionPixelSize(R.styleable.ErrorTipView_retry_height, -2);

        a.recycle();
        init(context);
        setOrientation(VERTICAL);
    }

    /**
     * create child view
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    private void init(Context context) {
        mIvIcon = new ImageView(context);
        mIvIcon.setScaleType(ImageView.ScaleType.CENTER);
        LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(mIvIcon, iconParams);

        mTvTip = new TextView(context);
        LayoutParams tipParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tipParams.setMargins(0, mFirstMargin, 0, 0);
        addView(mTvTip, tipParams);
        mTvTip.setGravity(Gravity.CENTER);

        mTvRetry = new TextView(context);
        LayoutParams retryParams = new LayoutParams(mRetryWidth, mRetryHeight);
        retryParams.setMargins(0, mSecondMargin, 0, 0);
        addView(mTvRetry, retryParams);
        mTvRetry.setClickable(true);
        mTvRetry.setGravity(Gravity.CENTER);

        if (mTipTextColor == null) {
            mTipTextColor = ColorStateList.valueOf(0xFF262626);
        }

        if (mRetryTextColor == null) {
            mRetryTextColor = ColorStateList.valueOf(0xFF262626);
        }

        if (mIconDrawable != null) {
            mIvIcon.setImageDrawable(mIconDrawable);
        }

        if (!TextUtils.isEmpty(mTipText)) {
            mTvTip.setText(mTipText);
        }

        mTvTip.setTextColor(mTipTextColor);
        if (mTipTextSize > 0) {
            mTvTip.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize);
        } else {
            mTvTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        if (!TextUtils.isEmpty(mRetryText)) {
            mTvRetry.setText(mRetryText);
        }

        mTvRetry.setTextColor(mRetryTextColor);
        if (mRetryTextSize > 0) {
            mTvRetry.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRetryTextSize);
        } else {
            mTvRetry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        if (mRetrybackground != null) {
            mTvRetry.setBackgroundDrawable(mRetrybackground);
        }
    }

    /**
     * Sets a drawable as the content of this ImageView.
     *
     * @param resId the resource identifier of the drawable
     * @attr ref android.R.styleable#ErrorTipView_error_icon
     */
    public void setImageResource(@DrawableRes int resId) {
        if (resId != 0 && mIconResource != resId) {
            mIconResource = resId;
            mIvIcon.setImageResource(resId);
        }
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resid the resource identifier of the string resource to be displayed
     * @attr ref android.R.styleable#ErrorTipView_tip_text
     * @see #setTipText(CharSequence)
     */
    public void setTipText(@StringRes int resid) {
        mTvTip.setText(resid);
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param resid the resource identifier of the string resource to be displayed
     * @attr ref android.R.styleable#ErrorTipView_retry_text
     * @see #setRetryText(CharSequence)
     */
    public void setRetryText(@StringRes int resid) {
        mTvRetry.setText(resid);
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     * @attr ref R.styleable#ErrorTipView_tip_text
     */
    public void setTipText(CharSequence text) {
        mTipText = text;
        mTvTip.setText(text);
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     * @attr ref R.styleable#ErrorTipView_retry_text
     */
    public void setRetryText(CharSequence text) {
        mRetryText = text;
        mTvRetry.setText(text);
    }

    /**
     * Set the retryButtonBackground
     */
    public void setRetryBgResource(@DrawableRes int backgroundResource) {
        if (backgroundResource != 0) {
            mRetrybackground = ResourcesCompat.getDrawable(getResources(), backgroundResource, getContext().getTheme());
            mTvRetry.setBackgroundDrawable(mRetrybackground);
        }
    }

    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param color A color value in the form 0xAARRGGBB.
     *              Do not pass a resource ID. To get a color value from a resource ID, call
     *              {@link androidx.core.content.ContextCompat#getColor(Context, int) getColor}.
     * @attr ref R.styleable#ErrorTipView_tip_text_color
     */
    public void setTipTextColor(int color) {
        mTipTextColor = ColorStateList.valueOf(color);
        mTvTip.setTextColor(color);
    }

    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param color A color value in the form 0xAARRGGBB.
     *              Do not pass a resource ID. To get a color value from a resource ID, call
     *              {@link androidx.core.content.ContextCompat#getColor(Context, int) getColor}.
     * @attr ref R.styleable#ErrorTipView_retry_text_color
     */
    public void setRetryTextColor(int color) {
        mRetryTextColor = ColorStateList.valueOf(color);
        mTvRetry.setTextColor(color);
    }

    /**
     * 新增设置提示字体大小
     * @param size
     */
    public void setTipTextSize(int size) {
        mTipTextSize = size;
        mTvTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 新增设置重试字体大小
     * @param size
     */
    public void setRetryTextSize(int size) {
        mRetryTextSize = size;
        mTvRetry.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setRetryVisibility(int visibility) {
        mTvRetry.setVisibility(visibility);
    }

    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setIconVisibility(int visibility) {
        mIvIcon.setVisibility(visibility);
    }

    /**
     * Set the visibility state of this view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    public void setTipVisibility(int visibility) {
        mTvTip.setVisibility(visibility);
    }

    public void setFirstMargin(int firstMargin) {
        this.mFirstMargin = firstMargin;
        ((LayoutParams) mTvTip.getLayoutParams()).setMargins(0, firstMargin, 0, 0);
    }

    public void setSecondMargin(int secondMargin) {
        this.mSecondMargin = secondMargin;
        ((LayoutParams) mTvRetry.getLayoutParams()).setMargins(0, secondMargin, 0, 0);
    }

    public void setRetryWidth(int retryWidth) {
        this.mRetryWidth = retryWidth;
        mTvRetry.getLayoutParams().width = retryWidth;
    }

    public void setRetryHeight(int retryHeight) {
        this.mRetryHeight = retryHeight;
        mTvRetry.getLayoutParams().height = retryHeight;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mListener = l;
        mTvRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(this);
        }
    }
}
