package com.kylone.player.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kylone.player.MainVideoView;
import com.kylone.player.callback.ControlListener;
import com.kylone.player.callback.MenuControl;
import com.kylone.player.callback.SettingInfo;
import com.kylone.player.view.VodLoadingView;
import com.kylone.utils.ExtraUitls;
import com.kylone.utils.LogUtil;
import com.kylone.utils.MediaPerference;
import com.kylone.utils.MediaResourceHelper;
import com.kylone.utils.SpeedChangedReceiver;
import com.kylone.video.IPlayer;
import com.kylone.video.R;
import com.kylone.video.VideoUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class VodControllerManager extends ControllerManager implements MenuControl, SpeedChangedReceiver.CallBack, IPlayer.OnErrorListener, IPlayer.OnCompletionListener, IPlayer.OnInfoListener,
        IPlayer.OnPreparedListener, IPlayer.OnBufferingUpdateListener, IPlayer.OnPreAdPreparedListener {
    private static final int HANDLE_ERROR = 102;
    private static final int INIT_RESULT = 109;
    private static final int CHANG_HIDE = 666;
    public static final int FINAL_PLAY = 0xffffff;
    private int mSeekWhenPrepared = 0;
    private int mScaleSize = IPlayer.SURFACE_BEST_FIT;
    private SeekController mSeekController = null;
    private SpeedChangedReceiver speedReceiver;
    private String mRateSpeed;
    private int mDecodeType = IPlayer.HARD_DECODE;
    private boolean init = false;
    private GestureDetector mGestureDetector;
    private PopupWindow mLoadingWindow = null;
    SparseArray<SettingInfo> settings = new SparseArray<SettingInfo>();
    private String mOriginUrl = null;
    private VideoUrl mVideoUrl = null;
    private HashMap<String, String> mHeader = null;
    private ControlListener mControlListener = null;


    /* videoName */
    private Bundle mExtraBundle = null;
    private int mCurrentBufferPercentage;
    private boolean isPlayAD;
    private boolean isLive = false;

    public VodControllerManager(Context context) {
        super(context);
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }

    private void initMenuSetting() {
        //设置解码方式列表
        ArrayList<Integer> decodes = new ArrayList<Integer>();
        decodes.add(IPlayer.INTELLIGENT_DECODE);
        decodes.add(IPlayer.HARD_DECODE);
        decodes.add(IPlayer.SOFT_DECODE);

        //设置播放大小列表
        ArrayList<Integer> scales = new ArrayList<Integer>();
//        scales.add(IPlayer.SURFACE_16_9);
//        scales.add(IPlayer.SURFACE_4_3);
        scales.add(IPlayer.SURFACE_BEST_FIT);
        scales.add(IPlayer.SURFACE_FILL);

        int size = MediaPerference.getVodScale();

        SettingInfo<Integer> volume = new SettingInfo<Integer>(INDEX_VOLUME, R.string.menu_controller_item_volume_set, R.mipmap.ic_menu_sound, null, 0);
        SettingInfo<Integer> decode = new SettingInfo<Integer>(INDEX_DECODE, R.string.menu_controller_item_decode_set, R.mipmap.ic_menu_jiema, decodes, mDecodeType, false);
        SettingInfo<Integer> scale = new SettingInfo<Integer>(INDEX_SCALE, R.string.menu_controller_item_scalesize_set, R.mipmap.ic_menu_scale, scales, size);

        settings.put(volume.getSettingIndex(), volume);
        settings.put(decode.getSettingIndex(), decode);
        settings.put(scale.getSettingIndex(), scale);
    }

    private void initControllerManager() {
        putController(MenuController.MENU_CONTROLLER, new MenuController(mContext, this));
        mSeekController = new SeekController(mContext, this);
        mSeekController.setIsLive(isLive);
        putController(SeekController.SEEK_CONTROLLER, mSeekController);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        mDecodeType = MediaPerference.getPlayerDecoder();
        mGestureDetector = new GestureDetector(mContext, new VodGestureListener());
        speedReceiver = new SpeedChangedReceiver(this);
        mContext.registerReceiver(speedReceiver, new IntentFilter(
                SpeedChangedReceiver.SPEED_CHANGED_BROADCAST));
        showLoadingView();
    }

    @Override
    public String getPlatformIconUrl(String platform) {
        return null;
    }

    @Override
    public String getSource() {
        String source = "";
        if (mVideoUrl != null) {
            source = mVideoUrl.url;
        }
        return source;
    }

    @Override
    public void onDetached() {
        super.onDetached();
        mHandler.removeCallbacksAndMessages(null);
        mContext.unregisterReceiver(speedReceiver);

        if (mLoadingWindow != null && mLoadingWindow.isShowing()) {
            mLoadingWindow.dismiss();
        }
    }

    private void handleError(String msg) {
        mHandler.sendMessage(mHandler.obtainMessage(HANDLE_ERROR, msg));
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_RESULT:
                    init = (Boolean) msg.obj;
                    if (init && mVideoUrl != null) {
                        initControllerManager();
                        sendEmptyMessage(FINAL_PLAY);
                    } else {
                        handleError("获取影片信息失败");
                        if (mContext != null && mContext instanceof Activity) {
                            ((Activity) mContext).finish();
                        }
                    }
                    break;
                case FINAL_PLAY:

                    String finalUrl = mVideoUrl.url;
                    if (mPlayer != null && finalUrl != null) {
                        if (mDecodeType == IPlayer.INTELLIGENT_DECODE) {
                            mPlayer.setDecodeType(IPlayer.HARD_DECODE);
                        } else {
                            mPlayer.setDecodeType(mDecodeType);
                        }
                        mPlayer.setVideoPath(finalUrl, null);
                        if (mVideoUrl.srtUrl != null) {
                            mPlayer.setSubtitlePath(Uri.parse(mVideoUrl.srtUrl), 0);
                        }
//                            mPlayer.start();
                        if (mSeekWhenPrepared > 0) {
                            mPlayer.seekTo(mSeekWhenPrepared);
                            mSeekWhenPrepared = 0;
                        }
                    }
//                    }
                    break;
                case HANDLE_ERROR:

                    break;
            }
        }
    };

    @Override
    public void setVideoPlayer(MainVideoView player) {
        super.setVideoPlayer(player);
//        mPlayer.setPlayType(IVideoFactory.VIDEO_OTHER);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnPreAdPreparedListener(this);
        mPlayer.setOnInfoListener(this);
    }


    //
    @Override
    public CharSequence getFilmTitle() {
        if (mExtraBundle != null) {
            String title = mExtraBundle.getString("title");
            return TextUtils.isEmpty(title) ? mOriginUrl : title;
        }
        return null;
    }

    public int getQuality() {
        if (mVideoUrl != null) {
            return mVideoUrl.quality;
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public boolean seekTo(int pos) {
        if (mPlayer != null) {
//            if (pos > mPlayer.getPosition()) {
//                analytics(PlayAnalytic.ANALYTIC_PLAY_SEEK, "快进", pos);
//            } else {
//                analytics(PlayAnalytic.ANALYTIC_PLAY_SEEK, "快退", pos);
//            }
            mPlayer.seekTo(pos);
            return true;
        }
        return false;
    }

    @Override
    public String getRate() {
        return mRateSpeed;
    }

    public Drawable getBackground() {
        return null;
    }


    public int getDecodeType() {
        return mDecodeType;
    }

    public void changeDecodeType(int decodeType) {
        if (mDecodeType == IPlayer.SOFT_DECODE) {
            if (decodeType == IPlayer.HARD_DECODE || decodeType == IPlayer.INTELLIGENT_DECODE) {
                if (mPlayer != null) {
                    mSeekWhenPrepared = (int) mPlayer.getPosition();
                    if (mVideoUrl != null) {
//                        ThreadManager.execute(new ParseTask(mHandler, mVideoUrl.url, mContext));
//                        mHandler.sendMessage(mHandler.obtainMessage(CHANG_SHOW,
//                                makeChangTip("")));
                    }
                }
            }
        } else {
            if (decodeType == IPlayer.SOFT_DECODE) {
                if (mPlayer != null) {
                    mSeekWhenPrepared = (int) mPlayer.getPosition();
                    if (mVideoUrl != null) {
//                        ThreadManager.execute(new ParseTask(mHandler, mVideoUrl.url, mContext));
//                        mHandler.sendMessage(mHandler.obtainMessage(CHANG_SHOW,
//                                makeChangTip("")));
                    }
                }
            }
        }
        mDecodeType = decodeType;
        MediaPerference.setPlayerDecoder(decodeType);
    }


    public void changScaleSet(int size) {
        if (mScaleSize != size) {
            mScaleSize = size;
            if (mPlayer != null) {
                mPlayer.changeScale(size);
            }
        }
    }

    @Override
    public void onPrepared(IPlayer mp) {
        hide();
        mHandler.sendEmptyMessageDelayed(CHANG_HIDE, 3000);
        mPlayer.changeScale(mScaleSize);
        mPlayer.start();
        mStartTime = System.currentTimeMillis();
//        analytics(PlayAnalytic.ANALYTIC_PLAY_CAHNGE_SET, "");
        isPlayAD = false;
        if (mControlListener != null) {
            mControlListener.onPrepared();
        }
    }

    @Override
    public boolean onError(IPlayer mp, int what, int extra) {
        hide();
//        if (what == IPlayer.VLC_INIT_ERROR) {
//            changeDecodeType(IPlayer.HARD_DECODE);
//        } else {
//            handleError(mContext.getResources().getString(R.string.play_error_txt));
//        }
        if (mControlListener != null) {
            mControlListener.onError();
        }
        return true;
    }

    @Override
    public void onCompletion(IPlayer mp) {
        if (mControlListener != null) {
            mControlListener.onCompletion();
        }
    }

    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        LogUtil.i("---------------------------------------------------------keyCode-------------" + keyCode);
        boolean uniqueDown = event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0;
        LogUtil.i("---------------------------------------------------------uniqueDown-------------" + uniqueDown);
        if (uniqueDown) {

            //可以响应的按键
            boolean f = keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                    && keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_ESCAPE;
            LogUtil.i("---------f----" + f);
            //判断是否在播放广告   // 判断是否正在加载页
            if ((isPlayAD || (isShowing() && TextUtils.equals(mCurrentControllerID, CONTROLLER_LOADING))) && f) {
                LogUtil.i(String.format(Locale.CHINA, "isPlayAD [%b] || controller [%s]", isPlayAD, mCurrentControllerID));
                return true;
            }


            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                show(SeekController.SEEK_CONTROLLER);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (isPlaying()) {
                    executePause();
                } else {
                    executePlay();
                }
                show(SeekController.SEEK_CONTROLLER);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                show(MenuController.MENU_CONTROLLER);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            mGestureDetector.onTouchEvent(ev);
            return true;
        }
        return false;
    }


    @Override
    public SparseArray<SettingInfo> supportSetting() {
        return settings;
    }

    @Override
    public void changeSetting(int index, Object change) {
        switch (index) {
            case INDEX_DECODE:
                changeDecodeType((Integer) change);
                break;
            case INDEX_SCALE:
                changScaleSet((Integer) change);
                break;
        }
    }

    @Override
    public Object getSetting(int index) {
        if (settings != null && settings.get(index) != null) {
            return settings.get(index).getSetting();
        }
        return null;
    }

    @Override
    public ArrayList<Object> getSettings(int index) {
        if (settings != null && settings.get(index) != null) {
            return settings.get(index).getSettings();
        }
        return null;
    }

    @Override
    public String parseName(int index, Object name) {
        switch (index) {
            case INDEX_DECODE:
                return MediaResourceHelper.getDecode((Integer) name);
            case INDEX_SCALE:
                return MediaResourceHelper.getScaleName((Integer) name);
            case INDEX_QUALITY:
                return MediaResourceHelper.getQualityName((Integer) name);
        }
        return "";
    }

    @Override
    public void post(int index, Object... o) {
        switch (index) {
            case VodLoadingView.INDEX_VODLOADING:
                displayTxt(o);
                break;
        }
    }

    public void displayTxt(Object... textViews) {
        try {
            if (textViews == null) {
                return;
            }
            TextView mTxtTitle = (TextView) textViews[0];
            TextView mTxtSpeed = (TextView) textViews[1];
            if (mTxtTitle != null) {
                mTxtTitle.setText(getFilmTitle());
            }
            if (mTxtSpeed != null) {
                mTxtSpeed.setText(mRateSpeed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 返回当前控制器是属于直播还是点播;
     *
     * @return "直播" or "点播" or other
     */
    @Override
    public String getControllerType() {
        return "NoPaser";
    }

    @Override
    public void onBufferingUpdate(IPlayer mp, int percent) {
        mCurrentBufferPercentage = percent;
    }

    @Override
    public void speedChanged(String speed) {
        mRateSpeed = speed;
    }

    @Override
    public void onPreAdPrepared(IPlayer mp, long time) {
        isPlayAD = true;
        hide();
        mHandler.sendEmptyMessageDelayed(CHANG_HIDE, 3000);
        mPlayer.changeScale(mScaleSize);
//        mPlayer.start();
        mStartTime = System.currentTimeMillis();
//        analytics(PlayAnalytic.ANALYTIC_PLAY_CAHNGE_SET, "");
    }

    public void setControlListener(ControlListener mControlListener) {
        this.mControlListener = mControlListener;
    }

    private class VodGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mPlayer == null || !isShowing()) {
                // getActivity().finish();
                return false;
            }
            float y = e.getY();
            int height = mPlayer.getHeight();
            if (y < height / 3) {
                show(SeekController.SEEK_CONTROLLER);
            } else if (y > height / 3 && y < height * 2 / 3) {
                show(MenuController.MENU_CONTROLLER);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x1 = e1.getX();
            float y1 = e1.getY();
            float x2 = e2.getX();
            float y2 = e2.getY();
            int width = mPlayer.getWidth();
            int HEIGHT = mPlayer.getHeight();
            if (x1 < width / 3 && x2 < width / 3) {
                float PERCENT = Math.max((HEIGHT * 0.008f), 10f);
                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                    int d;
                    if (distanceY < 0) {
                        d = -1;
                    } else {
                        d = 1;
                    }
                    ExtraUitls.changeVolume(d, mContext);
                }
            } else if (x1 > width * 2 / 3 && x2 > width * 2 / 3) {
                float PERCENT = Math.max((HEIGHT * 0.008f), 10f);
                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                    int d;
                    if (distanceY < 0) {
                        d = (int) Math.floor(distanceY / PERCENT);
                    } else {
                        d = (int) Math.ceil(distanceY / PERCENT);
                    }
                    ExtraUitls.setScreenLightness(mContext, ExtraUitls.getScreenLightness(mContext) + d);
                }
            }
            return true;
        }
    }


    @Override
    public void changArguments(Bundle args) {
        if (args != null) {
            mExtraBundle = args;
            String url = args.getString("url");
            LogUtil.i("url = " + url);
            if (!TextUtils.isEmpty(url) && !url.equals(mOriginUrl)) {
                mOriginUrl = url;
                mVideoUrl = new VideoUrl();
                mVideoUrl.url = url;
                initMenuSetting();
//                boolean result = true;
//                if (mPlayer.getDecodeType() == IPlayer.SOFT_DECODE) {
//                    result = ExtraUitls.initMediaLibray(mContext);
//                }
                mHandler.sendMessage(mHandler.obtainMessage(INIT_RESULT, true));
            }

        }
    }

    private long bufferTime = 0;
    private long bufferTotalTime = 0;

    @Override
    public boolean onInfo(IPlayer mp, int what, int extra, Bundle b) {
        if (what == IPlayer.MEDIA_INFO_TIMEOUT) {
            String uri = b.getString("uri");
            int seek = b.getInt("seek");
            int count = b.getInt("count");
            if (count <= 1) {
                if (mPlayer != null) {
                    mPlayer.setDecodeType(mDecodeType);
                    mPlayer.setVideoPath(uri, null);
                    mPlayer.start();
                    if (seek > 0) {
                        mPlayer.seekTo(seek);
                    }
                }
            }
            return true;
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            bufferTime = System.currentTimeMillis();
//            analytics(PlayAnalytic.ANALYTIC_PLAY_BUFFER_COUNT, "");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (bufferTime > 0) {
                bufferTotalTime = bufferTotalTime + System.currentTimeMillis() - bufferTime;
            }
        }

        if (mControlListener!=null){
            mControlListener.onInfo(what);
        }
        return false;
    }

    private boolean isPaused = false;

    @Override
    public void executePlay() {
        if (mPlayer != null) {
            mPlayer.start();
            if (pauseTime > 0) {
                pauseTotalTime = pauseTotalTime + System.currentTimeMillis() - pauseTime;
            }
            isPaused = false;
        }
    }

    @Override
    public void executePause() {
        if (mPlayer != null) {
            mPlayer.pause();
            pauseTime = System.currentTimeMillis();
            isPaused = true;
        }
    }

    private long mStartTime = 0;
    private long pauseTotalTime = 0;
    private long pauseTime = 0;

//    public void analytics(String action, Object... objects) {
//        if (mVideoUrl != null && mPlayer != null && mContext != null) {
//            PlayAnalytic mPlayAnalytic = new PlayAnalytic();
//            mPlayAnalytic.definition = mVideoUrl.name;
//            mPlayAnalytic.title = getFilmTitle() + "";
//            mPlayAnalytic.playType = mPlayer.getPlayType();
//            String modeType = mPlayer.isInTouchMode() ? "touch" : "tv";
//            mPlayAnalytic.modeType = modeType;
//            mPlayAnalytic.cid = VSTMapping.TYPE_PUSH + "";
//            long realTime;
//            if (isPaused) {
//                realTime = pauseTime - mStartTime - bufferTotalTime - pauseTotalTime;
//            } else {
//                realTime = (Time.getServerTime(mContext) - mStartTime - pauseTotalTime - bufferTotalTime);
//            }
//            mPlayAnalytic.playTime = realTime;
//            mPlayAnalytic.analytics(mContext, action, objects);
//        }
//    }
}
