package com.kylone.player.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.Scroller;
import android.widget.Space;

import java.util.List;

public class SimpleWheelLayout extends ViewGroup {

    private final static int DEFAULT_VISIBLE_ITEMCOUNT = 5;
    private int mVisibleItemCount = DEFAULT_VISIBLE_ITEMCOUNT;
    private int mItemHeight;
    private int mItemWidth;
    private int mSelection = -1;
    private int mOldSelection = -1;
    private boolean mCycel = false;
    private int mDeltaY = 0;
    private Scroller mScroller;
    private boolean mIsScrolling = false;
    private boolean mInLayout = false;
    private OnItemSelectedListener mOnItemSelectedListener = null;
    private List<CharSequence> mData = null;

    private class ViewReciyle {

    }

    public SimpleWheelLayout(Context context) {
        super(context);
        init(context);
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        if (visibleItemCount % 2 == 0) {
            throw new IllegalArgumentException("visibleItemCount  must be singular number!");
        } else {
            if (mVisibleItemCount != visibleItemCount) {
                mVisibleItemCount = visibleItemCount;
                // mItemHeight = getHeight() / mVisibleItemCount;
                // postInvalidate();
            }
        }
    }

    public boolean isCycel() {
        return mCycel;
    }

    public void setCycel(boolean isCycel) {
        mCycel = isCycel;
    }

    public SimpleWheelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        setFocusable(true);
        setStaticTransformationsEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // int width = MeasureSpec.getSize(widthMeasureSpec);
        // int widthSpecMod = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        final int verticalPadding = caluteVerticalPadding();
        height = height - verticalPadding;
        mItemHeight = height / mVisibleItemCount;
        final int count = getChildCount();
        int maxChildWidth = 0;
        for (int i = 0; i < count; ++i) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
            mItemWidth = Math.max(maxChildWidth, childView.getMeasuredWidth());
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // mItemWidth = getWidth();
        // mItemHeight = getHeight() / mVisibleItemCount;
    }

    private int caluteVerticalPadding() {
        return Math.max(getPaddingTop(), getPaddingBottom());
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        super.setOnKeyListener(l);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mInLayout) {
            return;
        }
        mInLayout = true;
        View selectedChild = getChildAt(mSelection);
        if (selectedChild != null) {
            if (mIsScrolling) {
                layoutChild(mOldSelection);
            } else {
                layoutChild(mSelection);
                selectedChild.performClick();
            }
            selectedChild.setSelected(true);
        }
        mInLayout = false;
    }

    public void setSelected(int slected) {
        mOldSelection=mSelection;
        mSelection = slected;
        postInvalidate();
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(mSelection);
        }
    }

    private void layoutChild(int selected) {
        int childTop = layoutSelected(selected);
        layoutDown(selected + 1, childTop + mItemHeight);
        layoutUp(selected - 1, childTop);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private int layoutSelected(int selectedId) {
        int left = getPaddingLeft();
        int top = calculateSelectedTop(mDeltaY);
        View child = getChildAt(selectedId);
        child.setSelected(false);
        child.layout(left, top, left + mItemWidth, top + mItemHeight);
        return top;
    }

    @SuppressLint("NewApi")
    private void layoutDown(int stratChild, int topStart) {
        int left = getPaddingLeft();
        int top = topStart;
        final int end = getHeight() - caluteVerticalPadding();
        final int count = getChildCount();
        while (top < end) {
            if (isCycel()) {
                while (stratChild < 0) {
                    stratChild += count;
                }
                if (count > 0) {
                    stratChild = stratChild % count;
                }
            } else {

            }
            View child = getChildAt(stratChild);
            if (child == null) {
                child = new Space(getContext());
            }
            child.layout(left, top, left + mItemWidth, top + mItemHeight);
            top += mItemHeight;
            stratChild += 1;
        }
    }

    @SuppressLint("NewApi")
    private void layoutUp(int startChild, int bottomEnd) {
        int left = getPaddingLeft();
        int bottom = bottomEnd;
        final int end = caluteVerticalPadding();
        final int count = getChildCount();
        while (bottom > end) {
            if (isCycel()) {
                while (startChild < 0) {
                    startChild += count;
                }
                startChild = startChild % count;
            } else {

            }
            View child = getChildAt(startChild);
            if (child == null) {
                child = new Space(getContext());
            }

            child.layout(left, bottom - mItemHeight, left + mItemWidth, bottom);
            bottom -= mItemHeight;
            startChild -= 1;
        }
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        Camera mCamera = new Camera();
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        float delta = calculateFloat(child);
        mCamera.translate(0.0f, 0.0f, delta * 380.0f);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(child.getWidth() / 2), -(child.getHeight() / 2));
        imageMatrix.postTranslate((child.getWidth() / 2), (child.getHeight() / 2));
//        child.setSelected(true);
        return true;
    }

    private float calculateFloat(View child) {
        int centerY = (child.getBottom() + child.getTop()) / 2;
        int parentCenter = calculateCenter();
        float delta = ((Math.abs(parentCenter - centerY) * 1.0f) / (parentCenter * 1.0f));
        return delta;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean drawChild = super.drawChild(canvas, child, drawingTime);
        return drawChild;
    }

    private int calculateCenter() {
        return getHeight() / 2;
    }

    private int calculateSelectedTop(int deltaY) {
        final int center = calculateCenter();
        final int height = getHeight();
        int top = center - mItemHeight / 2 - mDeltaY;
        if (top > height) {
            top = top % height;
        } else if (top < 0) {
            top = top % height + height;
        }
        return top;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mIsScrolling = true;
            mDeltaY = mScroller.getCurrY();
            forceLayoutChildren();
        } else {
            mDeltaY = 0;
            mIsScrolling = false;
            if (mOldSelection != mSelection) {
            }
        }
    }

    private void forceLayoutChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.requestLayout();
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void arrowScroll(int items) {
        mScroller.startScroll(0, 0, 0, items * mItemHeight);
        postInvalidate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        try {
            boolean uniquDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
            int keyCode = event.getKeyCode();
            final int count = getChildCount();
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (uniquDown && !mIsScrolling) {
                    if (isCycel() || mSelection > 0) {
                        arrowScroll(-1);
                        mOldSelection = mSelection;
                        mSelection -= 1;
                        while (mSelection < 0) {
                            mSelection += count;
                        }
                        if (count > 0) {
                            mSelection %= count;
                        }
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(mSelection);
                        }
                    }
                }
                return true;
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (uniquDown && !mIsScrolling) {
                    if (isCycel() || mSelection < count - 1) {
                        arrowScroll(1);
                        mOldSelection = mSelection;
                        mSelection += 1;
                        mSelection %= count;
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(mSelection);
                        }
                    }
                }
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return super.onKeyDown(keycode, event);
    }

    float touchY;
    final int defaultLongte=100;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchY=  event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                float y = event.getY();
                float tempY = y - touchY;
                final int count = getChildCount();

                if (tempY>defaultLongte){
                    if (!mIsScrolling) {
                        if (isCycel() || mSelection > 0) {
                            arrowScroll(-1);
                            mOldSelection = mSelection;
                            mSelection -= 1;
                            while (mSelection < 0) {
                                mSelection += count;
                            }
                            if (count > 0) {
                                mSelection %= count;
                            }
                        }
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(mSelection);
                        }
                    }
                    return true;
                }else if(tempY<-defaultLongte){
                    if ( !mIsScrolling) {
                        if (isCycel() || mSelection < count - 1) {
                            arrowScroll(1);
                            mOldSelection = mSelection;
                            mSelection += 1;
                            mSelection %= count;
                        }
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(mSelection);
                        }
                    }
                    return true;
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int selected);
    }
}