package com.kylone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;

import com.kylone.utils.ScreenParameter;


public class EditText extends android.widget.EditText {
    private boolean mEnableAutoFit = true;

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView(context, attrs);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView(context, attrs);
    }

    private void initAutoView(Context context, AttributeSet attrs) {
        this.setTextSize(getTextSize());
//        mEnableAutoFit = ScreenParameter.getEnableAutoFit(context, attrs);
        this.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        if (Build.VERSION.SDK_INT > 15) {
            setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier());
        }
    }


    @Override
    public void setTextSize(float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenParameter.getFitSize((int) size));
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(ScreenParameter.getFitSize(pad));
    }

    private int padingCount = 0;

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0) {
            super.setPadding(ScreenParameter.getFitWidth( left), ScreenParameter.getFitHeight( top),
                    ScreenParameter.getFitWidth( right), ScreenParameter.getFitHeight( bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }


    private int layoutCount = 0;

    @Override
    public void setLayoutParams(LayoutParams params) {
        if (layoutCount == 0) {
            super.setLayoutParams(ScreenParameter.getRealLayoutParams( params));
            layoutCount++;
        } else {
            super.setLayoutParams(params);
        }
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        super.setMinimumHeight(ScreenParameter.getFitHeight( minHeight));
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        super.setMinimumWidth(ScreenParameter.getFitWidth( minWidth));
    }
}
