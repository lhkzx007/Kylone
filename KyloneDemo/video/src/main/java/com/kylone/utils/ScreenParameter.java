package com.kylone.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.kylone.base.ComponentContext;

/**
 * Created by zack
 */

public class ScreenParameter {

    private static final float sDefaultWidth = 1280.0f;
    private static final float sDefaultHeight = 720.0f;
    private static int sWidth, sHeight;
    private static float sRatioX = 0;
    private static float sRatioY = 0;
    private static boolean isVertical = false;

    private static float sRadtio;

    public static int getScreenHeight() {
        if (sHeight == 0) {
            synchronized (ScreenParameter.class) {
                if (sHeight == 0) {
                    return getDisplaySize(true);
                }
            }
        }
        return sHeight;
    }

    public static int getScreenWidth() {
        if (sWidth == 0) {
            synchronized (ScreenParameter.class) {
                if (sWidth == 0) {
                    return getDisplaySize(false);
                }
            }
        }
        return sWidth;
    }

    public static void setVerticalMode(boolean isVertical) {
        ScreenParameter.isVertical = isVertical;
    }

    public static int getDisplaySize(boolean getHeight) {
        if (ComponentContext.getContext() != null) {
            if (sWidth * sHeight == 0) {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager) ComponentContext.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                display.getMetrics(dm);
                sWidth = dm.widthPixels;
                sHeight = dm.heightPixels;
            }
        }
        if (getHeight) {
            return sHeight;
        } else {
            return sWidth;
        }

    }

    public static int getFitSize(int size) {
        return getFitSize(size, true, false);
    }

    public static int getFitWidth(int size) {
        return getFitSize(size);
    }

    public static int getFitHeight(int size) {
        return getFitSize(size, true, true);
    }

    private static int getFitSize(int size, boolean autoFit, boolean isVertical) {
        if (!autoFit) {
            return size;
        }
        float radio = getRatio();
        if (Math.abs(getRatioX() - getRatioY()) < 0.15) {
            radio = isVertical ? getRatioY() : getRatioX();
        }
        int result = (int) (radio * size);
        return (result != 0 || size == 0) ? result : (size > 0 ? 1 : -1);
    }


    private static float getRatio() {
        if (sRadtio == 0) {
            synchronized (ScreenParameter.class) {
                if (sRadtio == 0) {
                    if (isVertical) {
                        sRatioX = Math.min(getScreenWidth(), getScreenHeight()) / sDefaultHeight;
                        sRatioY = Math.max(getScreenWidth(), getScreenHeight()) / sDefaultWidth;
                        sRadtio = sRatioY;
                    } else {
                        sRatioX = Math.max(getScreenWidth(), getScreenHeight()) / sDefaultWidth;
                        sRatioY = Math.min(getScreenWidth(), getScreenHeight()) / sDefaultHeight;
                    }
                    sRadtio = Math.min(sRatioX, sRatioY);
                    LogUtil.i("getScreenWidth() : " + getScreenWidth());
                    LogUtil.i("getScreenHeight() : " + getScreenHeight());
                    LogUtil.i(" sRadtio : " + sRadtio);
                    LogUtil.i(sRatioX + " ----- " + sRatioY);
                }
            }
        }
        return sRadtio;
    }

    private static float getRatioX() {
        getRatio();
        return sRatioX;
    }

    private static float getRatioY() {
        getRatio();
        return sRatioY;
    }


    public static ViewGroup.LayoutParams getRealLayoutParams(ViewGroup.LayoutParams params) {

        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).leftMargin = ScreenParameter.getFitWidth(
                    ((ViewGroup.MarginLayoutParams) params).leftMargin);
            ((ViewGroup.MarginLayoutParams) params).rightMargin = ScreenParameter.getFitWidth(
                    ((ViewGroup.MarginLayoutParams) params).rightMargin);
            ((ViewGroup.MarginLayoutParams) params).topMargin = ScreenParameter.getFitHeight(
                    ((ViewGroup.MarginLayoutParams) params).topMargin);
            ((ViewGroup.MarginLayoutParams) params).bottomMargin = ScreenParameter.getFitHeight(
                    ((ViewGroup.MarginLayoutParams) params).bottomMargin);
        }
        boolean isSquare = false;// 是否是一个正方形
        if (params.width != FrameLayout.LayoutParams.WRAP_CONTENT && params.width != FrameLayout.LayoutParams.MATCH_PARENT) {
            isSquare = params.width == params.height;
            params.width = ScreenParameter.getFitWidth(params.width);
        }
        if (params.height != FrameLayout.LayoutParams.WRAP_CONTENT && params.height != FrameLayout.LayoutParams.MATCH_PARENT) {
            if (isSquare) {// 如果是一个正方形，长和宽的缩放系数一致
                params.height = ScreenParameter.getFitWidth(params.height);
            } else {
                params.height = ScreenParameter.getFitHeight(params.height);
            }
        }
        return params;
    }
}
