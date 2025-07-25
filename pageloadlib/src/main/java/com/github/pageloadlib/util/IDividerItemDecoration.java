package com.github.pageloadlib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * features:
 * 1. 支持divider height 、color 、padding 等属性设置
 * 2. 自定义Divider Drawable
 * 3. divider绘制位置。call method {@link #setOffsetMode(int)}
 * <p>
 * Created by jiantao on 2017/6/23.
 * <p>
 * usage:
 * <p>
 * IDividerItemDecoration divierDecoration = new IDividerItemDecoration(this,IDividerItemDecoration.VERTICAL)
 * .setVerticalDividerHeight(50)
 * .setDividerColor(Color.BLUE)
 * .setOffsetMode(IDividerItemDecoration.OFFSET_MODE_TOP)
 * .setPadding(30, 0, 0, 0);
 * <p>
 * // or setCustomDrawable
 * // divierDecoration.setDrawable(getResources().getDrawable(R.drawable.custom_divider))
 * recyclerView.addItemDecoration(divierDecoration);
 */
public class IDividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    /**
     * divider 在itemView中的绘制位置。
     */
    public static final int OFFSET_MODE_TOP = 2;
    public static final int OFFSET_MODE_LEFT = 3;

    private final GradientDrawable mDivider;
    private final Rect mBounds = new Rect();
    /**
     * 是否不绘制最后一个divider（在offsetMode不等于OFFSET_MODE_TOP或OFFSET_MODE_LEFT场景下）
     */
    public boolean ignoreLastItem;
    /**
     * custom divider
     */
    private Drawable mCustomDivider;
    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;
    /**
     * 竖直方向divider高度
     */
    private int mVerticalDividerHeight;
    private int mHorizontalDividerWidth;
    private int mDividerColor;
    private int mDividerPadding;
    private int mPaddingLeft, mPaddingRight;
    private int mPaddingTop, mPaddingBottom;
    /**
     * divider 绘制位置。 默认水平方向绘制在itemView右边，竖直方向绘制在itemView底部
     */
    private int mOffsetMode = -1;

    public IDividerItemDecoration(Context context) {
        this(context, VERTICAL);
    }

    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * A {@link androidx.recyclerview.widget.LinearLayoutManager} implementation which provides
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public IDividerItemDecoration(Context context, int orientation) {
        mDivider = new GradientDrawable();
        //默认divider 1dp
        mVerticalDividerHeight = dp2px(context, 1);
        mHorizontalDividerWidth = dp2px(context, 1);
        mDividerColor = Color.YELLOW;
        setOrientation(orientation);
    }

    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public IDividerItemDecoration setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
        return this;
    }

    /**
     * Sets the {@link Drawable} for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    public IDividerItemDecoration setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mCustomDivider = drawable;
        return this;
    }

    /**
     * @param verticalDividerHeight the vertical height in pixels
     * @return
     */
    public IDividerItemDecoration setVerticalDividerHeight(@Px int verticalDividerHeight) {
        this.mVerticalDividerHeight = verticalDividerHeight;
        return this;
    }

    /**
     * @param horizontalDividerWidth the horizontal width in pixels
     * @return
     */
    public IDividerItemDecoration setHorizontalDividerWidth(@Px int horizontalDividerWidth) {
        this.mHorizontalDividerWidth = horizontalDividerWidth;
        return this;
    }

    public IDividerItemDecoration setDividerColor(@ColorInt int dividerColor) {
        this.mDividerColor = dividerColor;
        return this;
    }

    /**
     * @param dividerPadding the divider padding in pixels
     * @return
     */
    public IDividerItemDecoration setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;
        return this;
    }

    /**
     * set divider's position
     *
     * @param offsetMode {@link #OFFSET_MODE_LEFT} or {@link #OFFSET_MODE_TOP}
     */
    public IDividerItemDecoration setOffsetMode(int offsetMode) {
        this.mOffsetMode = offsetMode;
        return this;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent, state);
        } else {
            drawHorizontal(c, parent, state);
        }
    }

    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        canvas.save();
        boolean topMode = mOffsetMode == OFFSET_MODE_TOP;
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int itemCount = state.getItemCount();
        // use customDivider drawable
        if (mCustomDivider != null) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int childRealPosition = parent.getChildAdapterPosition(child);
                if (!ignoreLastItem(childRealPosition, itemCount)) {
                    parent.getDecoratedBoundsWithMargins(child, mBounds);
                    int bottom, top;
                    if (topMode) {
                        top = mBounds.top + Math.round(child.getTranslationY());
                        bottom = top + mCustomDivider.getIntrinsicHeight();
                    } else {
                        bottom = mBounds.bottom + Math.round(child.getTranslationY());
                        top = bottom - mCustomDivider.getIntrinsicHeight();
                    }
                    mCustomDivider.setBounds(left, top, right, bottom);
                    mCustomDivider.draw(canvas);
                }
            }
            canvas.restore();
            return;
        }

        left = left + mPaddingLeft;
        right = right - mPaddingRight;

        mDivider.setColor(mDividerColor);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int childRealPosition = parent.getChildAdapterPosition(child);
            if (!ignoreLastItem(childRealPosition, itemCount)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                int bottom, top;
                if (topMode) {
                    top = mBounds.top + Math.round(child.getTranslationY());
                    bottom = top + mVerticalDividerHeight;
                } else {
                    bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
                    top = bottom - mVerticalDividerHeight;
                }

                mDivider.setBounds(left, top, right, bottom);
                //mDivider.setCornerRadius();//设置矩形圆角
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        canvas.save();
        boolean leftMode = mOffsetMode == OFFSET_MODE_LEFT;
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        int itemCount = state.getItemCount();
        // use customDivider drawable
        if (mCustomDivider != null) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final int childRealPosition = parent.getChildAdapterPosition(child);
                if (!ignoreLastItem(childRealPosition, itemCount)) {
                    parent.getDecoratedBoundsWithMargins(child, mBounds);
                    parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                    int right, left;
                    if (leftMode) {
                        left = mBounds.left + Math.round(child.getTranslationX());
                        right = left + mCustomDivider.getIntrinsicWidth();
                    } else {
                        right = mBounds.right + Math.round(child.getTranslationX());
                        left = right - mCustomDivider.getIntrinsicWidth();
                    }
                    mCustomDivider.setBounds(left, top, right, bottom);
                    mCustomDivider.draw(canvas);
                }
            }
            canvas.restore();
            return;
        }

        top = top + mPaddingTop;
        bottom = bottom - mPaddingBottom;

        mDivider.setColor(mDividerColor);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int childRealPosition = parent.getChildAdapterPosition(child);
            if (!ignoreLastItem(childRealPosition, itemCount)) {
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                int right, left;
                if (leftMode) {
                    left = mBounds.left + Math.round(child.getTranslationX());
                    right = left + mHorizontalDividerWidth;
                } else {
                    right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
                    left = right - mHorizontalDividerWidth;
                }
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        // hide the divider for the last child
        if (ignoreLastItem(position, state.getItemCount())) {
            outRect.setEmpty();
        } else {
            StaggeredGridLayoutManager.LayoutParams params =
                    (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams)
                            ? (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()
                            : null;
            if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int height = mVerticalDividerHeight > 0 ? mVerticalDividerHeight : mDivider.getIntrinsicHeight();
                if (params != null && params.isFullSpan()) {
                    outRect.set(0, 0, 0, height);
                }
            } else if (mOrientation == VERTICAL) {
                int height = mVerticalDividerHeight > 0 ? mVerticalDividerHeight : mDivider.getIntrinsicHeight();
                int top = mOffsetMode == OFFSET_MODE_TOP ? height : 0;
                int bottom = mOffsetMode == OFFSET_MODE_TOP ? 0 : height;
                outRect.set(0, top, 0, bottom);
            } else {
                int width = mHorizontalDividerWidth > 0 ? mHorizontalDividerWidth : mDivider.getIntrinsicWidth();
                int left = mOffsetMode == OFFSET_MODE_LEFT ? width : 0;
                int right = mOffsetMode == OFFSET_MODE_LEFT ? 0 : width;
                outRect.set(left, 0, right, 0);
            }
        }
    }

    private boolean ignoreLastItem(int curPosition, int count) {
        return (ignoreLastItem && (curPosition == count - 1)
                && (mOffsetMode != OFFSET_MODE_LEFT && mOffsetMode != OFFSET_MODE_TOP));
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
