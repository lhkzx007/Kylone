package com.kylone.player.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.kylone.player.MainVideoView;
import com.kylone.player.callback.MenuControl;
import com.kylone.player.view.VodLoadingView;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.NetSpeedTaskUtil;
import com.kylone.utils.SpeedChangedReceiver;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by zack
 */
public class ControllerManager implements SpeedChangedReceiver.CallBack{
    protected static final int DEFAULT_TIME = 4000;
    protected static final int FADE_OUT = 1;
    public static final String CONTROLLER_LOADING = "controller_loading";

    protected boolean mAttach = false;
    protected boolean isEnabled = false;
    protected boolean isShowing = false;

    private VodLoadingView loadingView;

    private ArrayMap<String, Controller> controllerHashMap;

    protected MainVideoView mPlayer;

    protected String mCurrentControllerID;
    protected View mAnchorView;
    protected Bundle mArguments;

    private int mTempTime;

    private PopupWindow mWindow;
    protected Context mContext;
    private NetSpeedTaskUtil speedUtil;

    public ControllerManager(Context context) {
        speedUtil = new NetSpeedTaskUtil(this);
        initWindow(context);
    }

    protected void initWindow(Context context) {
        mContext = context;
        mWindow = new PopupWindow(context);
        mWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setFocusable(true);
        mWindow.setTouchable(true);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                mLastController = mCurrentControllerID;
                LogUtil.i("------controller dismiss-----");
                Controller controller = getController(mCurrentControllerID);
                if (controller != null) {
                    controller.setShowing(false);
                    controller.onDismiss();
                }
                mCurrentControllerID = "";
            }
        });
//        mAnimStyle = android.R.style.Animation;
    }

    public Context getContext() {
        return mContext;
    }

    public Handler getHandler() {
        return mHandler;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
            }
            return false;
        }
    });

    public void resetHideTime() {
        cancelHide();
        if (mTempTime > 0) {
            mHandler.sendEmptyMessageDelayed(FADE_OUT, mTempTime);
        }
    }

    /**
     * 设置隐藏时间
     *
     * @param time
     */
    public void setHideTime(int time) {
        mTempTime = time;
        resetHideTime();
    }

    public void cancelHide() {
        mHandler.removeMessages(FADE_OUT);
    }

    /**
     * 添加控制器
     *
     * @param controllerId 控制器ID
     * @param controller   控制器
     */
    public void putController(String tag, Controller controller) {
        if (controllerHashMap == null) {
            controllerHashMap = new ArrayMap<String, Controller>();
        }

        boolean isContains = controllerHashMap.containsValue(controller);
        if (!isContains) {
            LogUtil.i("ControllerManager", "putController tag :" + tag);
            controller.setTag(tag);
            controller.setControllerManager(this);
            controllerHashMap.put(tag, controller);
        }
    }

    /**
     * 移除控制器，并将控制器冲视图中一处
     *
     * @param controllerId 控制器ID
     */
    public void removeController(Controller controller) {
        if (controllerHashMap != null) {
            controller.release();
            controllerHashMap.remove(controller);
        }
    }

    /**
     * 通过id 获取 控制器
     *
     * @param controllerId 控制器ID
     * @return 控制器
     */
    public Controller getController(String tag) {
        if (controllerHashMap != null) {
            return controllerHashMap.get(tag);
        }
        return null;
    }

    /**
     * 移除所有控制器，并销毁
     */
    public void removeAllController() {
        LogUtil.i(" removeAllController ");
        if (controllerHashMap != null && controllerHashMap.size() > 0) {
            Set<String> keySet = controllerHashMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Controller controller = controllerHashMap.get(key);
                controller.release();
                iterator.remove();
            }
            if (mWindow.isShowing()) {
                mWindow.dismiss();
            }
            mCurrentControllerID = null;
        }
    }

    public boolean isShowing() {
        return (mWindow != null && mWindow.isShowing()) || isShowing;
    }

    /**
     * 判断当前是哪个controller
     *
     * @param controllerID
     * @return
     */
    public boolean isShowing(String controllerID) {
        return isShowing() && TextUtils.equals(mCurrentControllerID, controllerID);
    }

    public boolean isAttached() {
        return mAttach;
    }

    public void changArguments(Bundle args) {
    }

    public void setArguments(Bundle args) {
        mArguments = args;
        changArguments(mArguments);
    }


    public void finishActivity() {
        HandlerUtils.runUITask(new Runnable() {
            @Override
            public void run() {
                if (mContext != null && mContext instanceof Activity) {
                    ((Activity) mContext).finish();
                }
            }
        });

    }

    public void show(String controllerId) {
        show(controllerId, DEFAULT_TIME);
    }

    public void show(String controllerId, int time) {
        LogUtil.i("show controller  id : " + controllerId);
        if (TextUtils.equals(controllerId, mCurrentControllerID) && isShowing()) {
            LogUtil.i(String.format("this controllerId[%s] is showing ", controllerId));
            return;
        }
        if (TextUtils.equals(controllerId, CONTROLLER_LOADING)) {
            hide();
            if (mPlayer != null && loadingView != null) {
                if (loadingView.getParent() != mPlayer) {
                    mPlayer.addView(loadingView);
                }
                isShowing = true;
                mCurrentControllerID = controllerId;
                loadingView.setVisibility(View.VISIBLE);
                loadingView.bringToFront();
                loadingView.show();
            }
            return;
        }
        if (isEnabled()) {
            return;
        }
        try {
            Controller controller = getController(controllerId);
            if (controller == null) {
                return;
            }
            if (!TextUtils.equals(controllerId, mCurrentControllerID)) {
                hide();
                View view = controller.getControlView();
                if (view != null) {
                    View contentView = mWindow.getContentView();
                    if (view != contentView) {
                        mWindow.setContentView(view);
                    }
                }
            }
            if (!isShowing()) {
                int[] location = new int[2];
                mAnchorView.getLocationOnScreen(location);
                mWindow.showAtLocation(mAnchorView, Gravity.NO_GRAVITY, location[0], location[1]);
                mCurrentControllerID = controllerId;
                controller.setShowing(true);
                controller.onShow();
                mTempTime = time;
                if (time > 0) {
                    resetHideTime();
                } else {
                    mHandler.removeMessages(FADE_OUT);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected MenuControl getMenuControl() {
        if (this instanceof MenuControl) {
            return (MenuControl) this;
        }
        return null;
    }

    public void showLoadingView() {
        HandlerUtils.runUITask(new Runnable() {
            @Override
            public void run() {
                showLoadingView(17);
            }
        });
    }

    public void showLoadingView(int type) {
        if (loadingView == null) {
            loadingView = new VodLoadingView(mContext, type, !isEnabled());
            loadingView.setControl(getMenuControl());
        } else {
            loadingView.setIsFull(!isEnabled());
        }
        show(CONTROLLER_LOADING, 0);
    }

    public void showProgressView() {
        if (loadingView != null) {
            loadingView.showProgressView();
        }
    }

    /**
     * 只是用来隐藏加载圈
     */
    public void hideProgressView() {
        if (loadingView != null) {
            LogUtil.i("-----hideProgressView------");
            loadingView.hideProgressView();
        }
    }

    /**
     * 是否开启小窗口~~
     *
     * @return
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * 设置是否开启小窗口~~~~
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        Log.i("zack", LogUtil.buildMessage("  isEnabled : " + enabled));
        isEnabled = enabled;
        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
            loadingView.setIsFull(!isEnabled);
        }
    }

    /**
     * 隐藏当前控制器
     */
    public void hide() {
        hide(mCurrentControllerID);
    }

    /**
     * 隐藏指定的控制器
     *
     * @param controllerId
     */
    public void hide(String controllerId) {
        try {
            LogUtil.i("---hide--controllerId---" + controllerId);
            if (TextUtils.equals(controllerId, CONTROLLER_LOADING)) {
                if (loadingView != null && (loadingView.getVisibility() == View.VISIBLE || isShowing)) {
                    LogUtil.i("隐藏-------");
//                loadingView.setVisibility(View.GONE);
                    loadingView.hide();
                    isShowing = false;
                }
                return;
            }

            if (isShowing()) {
                mWindow.dismiss();
                Controller controller = getController(controllerId);
                if (controller != null) {
                    controller.setShowing(false);
                    controller.onHide();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setVideoPlayer(MainVideoView player) {
        mPlayer = player;
        if (mPlayer != null)
            mPlayer.setMediaController(this);
    }

    protected MainVideoView getVideoPlayer() {
        return mPlayer;
    }

    public void setAnchorView(View view) {
        if (view != null) {
            mAnchorView = view;
        }
    }

    public long getPosition() {
        if (mPlayer != null) {
            return mPlayer.getPosition();
        }
        return 0;
    }

    public long getDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return -1;
    }

    public void onAttached() {
        mAttach = true;
        if (speedUtil != null) {
            speedUtil.startShowNetSpeed();
        }
    }

    public void onDetached() {
        mAttach = false;
        if (speedUtil != null) {
            speedUtil.stop();
        }
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public void onActivityResume() {
    }

    public void onActivityPause() {
    }

    public void analytics(String key, Object... o) {

    }

    @Override
    public void speedChanged(String speed) {

    }
}
