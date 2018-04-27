package com.kylone.player.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kylone.player.callback.MenuControl;
import com.kylone.player.view.SeekView;
import com.kylone.player.view.TextDrawable;
import com.kylone.utils.ExtraUitls;
import com.kylone.utils.LogUtil;
import com.kylone.video.IPlayer;
import com.kylone.video.R;

/**
 * 进度条 Created by 张兴 on 2015/4/13.
 */
public class SeekController extends Controller {
    public static final String SEEK_CONTROLLER = "seekController";

    private TextView mTxtFilmName; // 影片名
//    private TextView mTxtQuality; // 分辨率 清晰度
    private TextView mTxtDuration; // 总时长
    //    private ImageView mImgPlatform;// 源
    private SeekView mSeekView; // 进度条
    private TextView mTxtRateSpeed;// 读取速度

    private View pauseView; // 暂停按钮
    //    private AdView adView;// 广告View
    private FrameLayout mStateView;
    //    private AdManager adManager;
    private String mAdKey;//广告接口需要的key

    private boolean mDragging = false;

    private static final int SET_PROCESS = 0x11;
    private static final int UPDATE_VIEW = SET_PROCESS + 1;
    private static final int PLAY_V_AD = UPDATE_VIEW + 1;
    private static final int ProgressIncrement = 20000;
    private MenuControl mControl;// 留用
    private TextDrawable mPositionDrawable;
    private ImageView seekBView;

    private GestureDetector mGestureDetector = null;
    private boolean isPlayVAd;
    private boolean isLive;
//    private com.vst.player.view.AlwaysMarqueeTextView mTvSource;

    public SeekController(Context context, MenuControl control) {
        super(context);
        setFocusable(true);
        mControl = control;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == SET_PROCESS) {
                setProgress();
                if (mTxtRateSpeed != null) {
                    mTxtRateSpeed.setText(mControl.getRate());
                }
            } else if (msg.what == UPDATE_VIEW) {
                updateView();
            } else if (msg.what == PLAY_V_AD) {
                //判断是否播放播放器内部广告
//                IPlayer iplayer = getControllerManager().getVideoPlayer();
//                isPlayVAd = iplayer != null && iplayer.isPlayingAd();
//                if (!isPlayVAd) {
//                adManager.attach();
//                }
            }
            return false;
        }
    });

    private void setProgress() {
        if (mControl != null && !mDragging) {
            long position = mControl.getPosition();
            long duration = mControl.getDuration();
            int bufferPosition = mControl.getBufferPercentage();
            if (mSeekView != null) {
                mSeekView.setMax((int) duration);
                mSeekView.setProgress((int) position);
                mSeekView.setBufferProgress(bufferPosition);
                mSeekView.setKeyProgressIncrement(ProgressIncrement);
            }
        }
        mHandler.removeMessages(SET_PROCESS);
        mHandler.sendEmptyMessageDelayed(SET_PROCESS, 1000);
    }

    public void setSeekDrawable(Drawable backDrawable, Drawable progressDrawable, Drawable changedDrawable,
                                Drawable bufferDrawable) {
        if (mSeekView != null) {
            mSeekView.setDrawable(backDrawable, progressDrawable, changedDrawable, bufferDrawable);
        }
    }

    public void setAdKey(String key) {
        mAdKey = key;
    }

    @Override
    protected View createControlView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_control_seek, this, false);
        mTxtRateSpeed = (TextView) view.findViewById(R.id.seek_info_speed);
        mTxtFilmName = (TextView) view.findViewById(R.id.seek_info_name);
//        mTxtQuality = (TextView) view.findViewById(R.id.seek_info_quality);
        mTxtDuration = (TextView) view.findViewById(R.id.seek_info_duration);
//        mImgPlatform = (ImageView) view.findViewById(R.id.seek_info_platform);
        seekBView = (ImageView) view.findViewById(R.id.seek_control_iv);
//        mTvSource = (com.vst.player.view.AlwaysMarqueeTextView) view.findViewById(R.id.seek_info_source);
        mSeekView = (SeekView) view.findViewById(R.id.seek_info_seekbar);
        if (isLive){
            mSeekView.setVisibility(View.INVISIBLE);
        }else{
            mSeekView.setVisibility(View.VISIBLE);
            mSeekView.setProgressGravity(SeekView.PROGRESS_TOP);
            mSeekView.setOnSeekChangedListener(mOnSeekChangedListener);
            Resources res = getContext().getResources();
            mSeekView.setDrawable(res.getDrawable(R.mipmap.bg_liv_progress_nor),
                    res.getDrawable(R.mipmap.bg_liv_progress2),
                    res.getDrawable(R.mipmap.bg_liv_progress),
                    res.getDrawable(R.mipmap.bg_liv_progress3));
            mPositionDrawable = new TextDrawable(getContext());
            mPositionDrawable.setText(ExtraUitls.stringForTime(0));
            mPositionDrawable.setBackDrawable(getContext().getResources().getDrawable(R.mipmap.bg_seek_time));
            mPositionDrawable.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
            mPositionDrawable.setTextColor(0xffffffff);
            mSeekView.setThumb(mPositionDrawable);
            int offset = mPositionDrawable.getIntrinsicWidth();
            mSeekView.setThumbOffset(offset / 2);
            mSeekView.setProgressMinHeight(8);
        }

        pauseView = view.findViewById(R.id.seek_control_pause);
        mStateView = (FrameLayout) view.findViewById(R.id.seek_control_extend);

//        adManager = new AdManager(getContext(), mStateView, mAdKey);
//        if (getControllerManager() instanceof VodControllerManager) {
//            int idx = ((VodControllerManager) getControllerManager()).getCurrentIdx();
//            String title = ((VodControllerManager) getControllerManager()).getFilmTitle().toString();
//            String type = getControllerManager().getVideoPlayer().getPlayType();
//            adManager.setInfo(type, title, idx);
//        }
//        adManager.setLoadAdListener(new AdManager.LoadAdListener() {
//            @Override
//            public void onLoadAdComplete(String url) {
//                getControllerManager().analytics(PlayAnalytic.ANALYTIC_PLAY_PAUSE_AD, url);
//            }
//        });


        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mSeekView.onTouchEvent(e2);
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mSeekView.onTouchEvent(e2);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER, 0);
                onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, event);
                return super.onSingleTapConfirmed(e);
            }
        });
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        updateView();
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
//        if (adManager != null) {
//            adManager.detach();
//        }
        mHandler.removeMessages(SET_PROCESS);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void updateView() {
        LogUtil.i(" update  seek control  view  mControl = " + mControl);
        if (mControl != null) {

            if (mTxtFilmName != null) {
                mTxtFilmName.setText(mControl.getFilmTitle());
            }
            if (mTxtRateSpeed != null) {
                String mRateSpeed = mControl.getRate();
                mTxtRateSpeed.setText(mRateSpeed);
            }
//            if (mTxtQuality != null) {
//                Object quality = mControl.getSetting(MenuControl.INDEX_QUALITY);
//                LogUtil.i(" quality : " + quality);
//                if (quality != null) {
//
//                    String qName = mControl.parseName(MenuControl.INDEX_QUALITY, quality);
//                    if (!TextUtils.isEmpty(qName)) {
//                        mTxtQuality.setText(qName);
//                    } else {
//                        mTxtQuality.setVisibility(View.GONE);
//                    }
//                } else {
//                    mTxtQuality.setVisibility(View.GONE);
//                }
//            }

            if (mTxtDuration != null) {
                mTxtDuration.setText(ExtraUitls.stringForTime(mControl.getDuration()));
            }
            if (mSeekView != null) {
                mHandler.removeMessages(SET_PROCESS);
                mHandler.sendEmptyMessage(SET_PROCESS);
            }


            int visible = View.GONE;
            if (!mControl.isPlaying()) {
                visible = View.VISIBLE;
            }
            if (pauseView != null) {
                pauseView.setVisibility(visible);
            }
            if (mSeekView != null) {
                mStateView.setVisibility(visible);
//                if (visible == View.VISIBLE) {
//                    mHandler.sendEmptyMessageDelayed(PLAY_V_AD, 1000);
//                } else {
//                    adManager.detach();
//                }
            }


        }
    }

    public ViewGroup getAdFrame() {
        return mStateView;
    }

    public void executePause() {
        try {

            if (mStateView != null && !mStateView.isShown()) {
                mStateView.setVisibility(View.VISIBLE);
            }

//            if (mStateView != null && adManager != null && !mStateView.isShown()) {
//                mStateView.setVisibility(View.VISIBLE);
//                LogUtil.i("AdManager", "isPlayVAd =" + isPlayVAd);
////                if (!isPlayVAd) {
//                adManager.attach();
////                }
//            }
            if (pauseView != null && !pauseView.isShown()) {
                pauseView.setVisibility(View.VISIBLE);
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
            if (seekBView != null && seekBView.isShown()) {
                seekBView.setVisibility(View.GONE);
            }
            if (mStateView != null && mStateView.isShown()) {
                mStateView.setVisibility(View.GONE);
            }
//            adManager.detach();
            if (pauseView != null && pauseView.isShown()) {
                pauseView.setVisibility(View.GONE);
            }
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
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!isPlayVAd && mStateView != null && !mControl.isPlaying()) {
                if (mStateView.getVisibility() == View.VISIBLE) {
//                            adManager.detach();
                    mStateView.setVisibility(View.GONE);
                } else {
//                    adManager.changeAd();
//                            adManager.attach();
                    mStateView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
//            getControllerManager().hide();
            getControllerManager().show(MenuController.MENU_CONTROLLER);
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
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mStateView != null && !mControl.isPlaying() && isPlayVAd) {
//                IPlayer iplayer = getControllerManager().getVideoPlayer().getIVideo();
//                if (iplayer instanceof TencentVideo) {
//                    ((TencentVideo) iplayer).closeAd(event);
//                    return true;
//                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private int mProgress, mStartprogress;
    private SeekView.OnSeekChangedListener mOnSeekChangedListener = new SeekView.OnSeekChangedListener() {
        @Override
        public void onSeekChanged(SeekView bar, int progress, int startprogress, boolean increase) {
            mProgress = progress;
            mStartprogress = startprogress;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(mSeekRunnable, 500);
        }

        @Override
        public void onProgressChanged(SeekView bar, int progress, boolean fromuser) {
            mPositionDrawable.setText(ExtraUitls.stringForTime(progress));
        }

        @Override
        public void onShowSeekBarView(boolean increase) {
            mHandler.removeCallbacksAndMessages(null);
            showSeekBarView(increase);
        }
    };

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
                    executePlay();
                }
                mDragging = false;
            }
            mHandler.sendEmptyMessageDelayed(SET_PROCESS, 1000);
        }
    };

    private void showSeekBarView(boolean increase) {
        if (increase) {
            seekBView.setImageResource(R.mipmap.ic_seekforward);
        } else {
            seekBView.setImageResource(R.mipmap.ic_seekbackward);
        }
        if (seekBView != null) {
            seekBView.setVisibility(View.VISIBLE);
        }
    }

    private boolean mEnableBack = true;

    public void setEnableBack(boolean enable) {
        mEnableBack = enable;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }
}
