package com.kylone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.kylone.utils.ScreenParameter;

public class RecyclerView extends android.support.v7.widget.RecyclerView {

    public RecyclerView(Context context) {
        super(context);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAutoView(context, attrs);

//        android:clipChildren="false"
//        android:clipToPadding="false"

    }

    private void initAutoView(Context context, AttributeSet attrs) {
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight(minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitSize(minWidth));
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0) {
            super.setPadding(ScreenParameter.getFitWidth(left), ScreenParameter.getFitHeight(top),
                    ScreenParameter.getFitWidth(right), ScreenParameter.getFitHeight(bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    private int layoutCount = 0;

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }
}
