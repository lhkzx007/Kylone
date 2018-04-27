package com.kylone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.kylone.utils.ScreenParameter;

public class RelativeLayout extends android.widget.RelativeLayout {

    private boolean mEnableAutoFit = true;

    public RelativeLayout(Context context) {
        super(context);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
//        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
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
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams(params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }

    public void setAutoLayoutParams(ViewGroup.LayoutParams params) {
        layoutCount = 1;
        super.setLayoutParams(ScreenParameter.getRealLayoutParams(params));
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight(minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitWidth(minWidth));
    }

    public void setSuperPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setScaleX(float scaleX) {
        boolean needRebuild = scaleX != getScaleX();
        super.setScaleX(scaleX);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setScaleY(float scaleY) {
        boolean needRebuild = scaleY != getScaleY();
        super.setScaleY(scaleY);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setTranslationX(float translationX) {
        boolean needRebuild = translationX != getTranslationX();
        super.setTranslationX(translationX);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setTranslationY(float translationY) {
        boolean needRebuild = translationY != getTranslationY();
        super.setTranslationY(translationY);
        if (needRebuild) {
            invalidateParent();
        }
    }

    @Override
    public void setSelected(boolean selected) {
        boolean needRebuild = selected != isSelected();
        super.setSelected(selected);
        if (needRebuild) {
            invalidateParent();
        }
    }

    private void invalidateParent() {
        if (Build.VERSION.SDK_INT > 17) return;
        ViewParent parent = this;
        while (parent.getParent() != null && parent.getParent() instanceof View) {
            parent = parent.getParent();
        }
        ((View) parent).invalidate();
    }
}
