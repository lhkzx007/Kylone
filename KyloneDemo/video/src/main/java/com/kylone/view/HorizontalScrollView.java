package com.kylone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.kylone.utils.ScreenParameter;

public class HorizontalScrollView extends android.widget.HorizontalScrollView {


    public HorizontalScrollView(Context context) {
        super(context);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
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
    public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight(minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitWidth(minWidth));
    }
}
