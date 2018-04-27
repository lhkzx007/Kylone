package com.kylone.player.controller;

import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 控制器
 * Created by zack
 */
public abstract class Controller extends FrameLayout {
    //    private final Context mContext;
    private String mName;
    protected View mControlView;
    private boolean isShowing;
    private ControllerManager controllerManager;

    public Controller(Context context) {
        super(context);
    }


    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    /**
     * 获取ControlView
     *
     * @return 控制器视图
     */
    public View getControlView() {
        if (mControlView == null) {
            mControlView = createControlView();
            this.addView(mControlView);
//            if (mControlView != null) {
//                mControlView.setOnKeyListener(this);
//                mControlView.setOnTouchListener(this);
//            }
        }
        return this;
    }


    /**
     * 创建ControlView
     *
     * @return 控制器视图
     */
    protected abstract View createControlView();

    /**
     * 释放资源
     */
    public abstract void release();

    /**
     * 更新Control内容
     *
     * @param build 需要的数据
     */
    public abstract void updateControlView(Build build);

    public abstract void onShow();

    public abstract void onHide();

    public void onDismiss() {
        onHide();
    }

    void setControllerManager(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    protected ControllerManager getControllerManager() {
        return controllerManager;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getControllerManager() != null) {
            getControllerManager().resetHideTime();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getControllerManager() != null) {
            getControllerManager().resetHideTime();
        }
        return super.dispatchTouchEvent(ev);
    }
}
