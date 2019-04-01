package com.kylone.player.controller;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kylone.player.callback.IPlayerControlCallback;
import com.kylone.utils.ExtraUitls;
import com.kylone.utils.LogUtil;
import com.kylone.video.R;

/**
 * 进度条 Created by 张兴 on 2015/4/13.
 */
public class SeekController extends Controller {
    public static final String SEEK_CONTROLLER = "seekController";

    private TextView mTxtFilmName; // 影片名
    private TextView mTxtDuration; // 总时长
    private SeekBar mSeekView; // 进度条

    private View pauseView; // 暂停按钮
    private View seek_btn; // 暂停按钮

    private boolean mDragging = false;

    private static final int SET_PROCESS = 0x11;
    private static final int UPDATE_VIEW = SET_PROCESS + 1;
    private static final int PLAY_V_AD = UPDATE_VIEW + 1;
    private static final int HIDE_PAUSE = PLAY_V_AD + 1;
    private static final int ProgressIncrement = 20000;
    private IPlayerControlCallback mControl;// 留用


    private boolean isLive;

    private SeekController(Context context) {
        super(context);
    }

    public SeekController(Context context, IPlayerControlCallback control) {
        this(context);
        setFocusable(true);
        mControl = control;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SET_PROCESS) {
                setProgress();
            } else if (msg.what == UPDATE_VIEW) {
                updateView();
            } else if (msg.what == PLAY_V_AD) {
                //判断是否播放播放器内部广告
            } else if (msg.what == HIDE_PAUSE) {
                pauseView.setVisibility(View.GONE);
            }
            return false;
        }
    });

    private void setProgress() {
        if (mControl != null && !mDragging) {
            long position = mControl.getPosition();
            long duration = mControl.getDuration();
            LogUtil.i(position + "  /   " + duration);
            int bufferPosition = mControl.getBufferPercentage();
            if (mSeekView != null) {
                mSeekView.setMax((int) duration);
                mSeekView.setProgress((int) position);
                mSeekView.setSecondaryProgress(bufferPosition);
                mSeekView.setKeyProgressIncrement(duration > 30 * 60000 ? ProgressIncrement : mSeekView.getKeyProgressIncrement());
            }

            if (mTxtDuration != null) {
                mTxtDuration.setText(String.format("%s / %s", ExtraUitls.stringForTime(mControl.getPosition()), ExtraUitls.stringForTime(mControl.getDuration())));
            }
        }
        mHandler.removeMessages(SET_PROCESS);
        mHandler.sendEmptyMessageDelayed(SET_PROCESS, 1000);
    }


    @Override
    protected View createControlView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_control_seek, this, false);
        mSeekView = (SeekBar) view.findViewById(R.id.seek_bar);

        mSeekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mProgress = progress;
                    mHandler.removeCallbacksAndMessages(null);

                    LogUtil.i(" onProgressChanged -->  " + progress);
                    if (mTxtDuration != null) {
                        mTxtDuration.setText(String.format("%s / %s", ExtraUitls.stringForTime(progress), ExtraUitls.stringForTime(mControl.getDuration())));
                    }
                    mHandler.postDelayed(mSeekRunnable, 500);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartprogress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTxtDuration = (TextView) view.findViewById(R.id.seek_time);
        pauseView = view.findViewById(R.id.seek_control_pause);
        seek_btn = view.findViewById(R.id.seek_btn);
//        updateView();
        return view;
    }

    @Override
    public void release() {

    }

    @Override
    public void updateControlView(Build build) {

    }

    @Override
    public void onShow() {
        mHandler.sendEmptyMessage(UPDATE_VIEW);
    }

    @Override
    public void onHide() {
        mHandler.removeMessages(SET_PROCESS);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void updateView() {
        LogUtil.i(" update  seek control  view  mControl = " + mControl);
        if (mControl != null) {

            if (mTxtDuration != null) {
                mTxtDuration.setText(String.format("%s / %s", ExtraUitls.stringForTime(mControl.getPosition()), ExtraUitls.stringForTime(mControl.getDuration())));
            }
            if (mSeekView != null) {
                mHandler.removeMessages(SET_PROCESS);
                mHandler.sendEmptyMessage(SET_PROCESS);
            }


            int visible = View.GONE;
            if (!mControl.isPlaying()) {
                visible = View.VISIBLE;
                seek_btn.setBackgroundResource(R.mipmap.seek_btn_start);
            } else {
                seek_btn.setBackgroundResource(R.mipmap.seek_btn_pause);
            }
            if (pauseView != null) {
                pauseView.setVisibility(visible);
            }

        }
    }

    public void executePause() {
        try {
            seek_btn.setBackgroundResource(R.mipmap.seek_btn_start);
            if (pauseView != null) {
                mHandler.removeMessages(HIDE_PAUSE);
                pauseView.setVisibility(View.VISIBLE);
                pauseView.setBackgroundResource(R.mipmap.seek_pause);
            }
            if (mControl != null) {
                mControl.executePause();
                if (isShowing()) {
                    getControllerManager().setHideTime(0);
                } else {
                    getControllerManager().show(SEEK_CONTROLLER, 0);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void executePlay() {
        try {
            seek_btn.setBackgroundResource(R.mipmap.seek_btn_pause);

            if (mControl != null) {
                mControl.executePlay();
                if (mSeekView != null && !mSeekView.isInTouchMode()) {
                    if (isShowing()) {
                        getControllerManager().setHideTime(ControllerManager.DEFAULT_TIME);
                    } else {
                        getControllerManager().show(getName());
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mEnableBack || mControl.isPlaying()) {
                getControllerManager().hide();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            showSeekBarView(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT);
            mSeekView.onKeyDown(keyCode, event);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            LogUtil.i("---KEYCODE_DPAD_CENTER------");
            if (mControl != null) {
                if (mControl.isPlaying()) {
                    executePause();
                } else {
                    if (pauseView != null) {
                        pauseView.setVisibility(View.VISIBLE);
                        pauseView.setBackgroundResource(R.mipmap.seek_start);
                        mHandler.removeMessages(HIDE_PAUSE);
                        mHandler.sendEmptyMessageDelayed(HIDE_PAUSE, 3000);
                    }
                    executePlay();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if (mControl != null) {
                long p = (int) mControl.getPosition();
                if (p > 15000) {
                    mControl.seekTo((int) p - 15000);
                }
            }
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if (mControl != null) {
                long p = mControl.getPosition();
                long duration = mControl.getDuration();
                if (p > 0 && p < duration - 15000) {
                    mControl.seekTo((int) p + 15000);
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mSeekView.onKeyUp(keyCode, event);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private int mProgress, mStartprogress;

    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgress == mStartprogress) {
                mDragging = true;
            } else {

                LogUtil.i("  mControl ==> " + mControl);
                if (mControl != null) {
                    if (mProgress >= mControl.getDuration()) {
                        mProgress = (int) mControl.getDuration();
                    }

                    if (mProgress <= 0) {
                        mProgress = 0;
                    }
                    mControl.seekTo(mProgress);
                    mHandler.removeMessages(HIDE_PAUSE);
                    mHandler.sendEmptyMessage(HIDE_PAUSE);
                    executePlay();
                }
                mDragging = false;
            }
            mHandler.sendEmptyMessageDelayed(SET_PROCESS, 1000);
        }
    };

    private void showSeekBarView(boolean increase) {
        if (pauseView != null) {
            if (increase) {
                pauseView.setBackgroundResource(R.mipmap.ic_seekforward);
            } else {
                pauseView.setBackgroundResource(R.mipmap.ic_seekbackward);
            }
            pauseView.setVisibility(View.VISIBLE);
        }
    }

    private boolean mEnableBack = true;

    public void setEnableBack(boolean enable) {
        mEnableBack = enable;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }

    public boolean isLive() {
        return isLive;
    }
}
