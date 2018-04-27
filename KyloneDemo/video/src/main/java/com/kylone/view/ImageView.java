package com.kylone.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import com.kylone.utils.ScreenParameter;


public class ImageView extends android.widget.ImageView {

    //    private boolean isShowAnimation = true;
    private boolean mEnableAutoFit = true;
    private int padingCount = 0;
    private int layoutCount = 0;

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAutoView();
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAutoView();
    }

    private void initAutoView() {
        this.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (padingCount == 0 && mEnableAutoFit) {
            super.setPadding(ScreenParameter.getFitWidth(left), ScreenParameter.getFitHeight(top),
                    ScreenParameter.getFitWidth(right), ScreenParameter.getFitHeight(bottom));
            padingCount++;
        } else {
            super.setPadding(left, top, right, bottom);
        }
    }

    @Override
    public void setLayoutParams(LayoutParams params) {
        if (layoutCount == 0 && mEnableAutoFit) {
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

//    public void setIsShowAnimation(boolean isShowAnimation) {
//        this.isShowAnimation = isShowAnimation;
//    }

//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        if (isShowAnimation && (getAnimation() == null || !getAnimation().hasStarted())) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_action);
//            startAnimation(anim);
//        }
//        super.setImageBitmap(bm);
//    }
//
//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        if (isShowAnimation && (getAnimation() == null || !getAnimation().hasStarted())) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_action);
//            startAnimation(anim);
//        }
//        super.setImageDrawable(drawable);
//    }
}
