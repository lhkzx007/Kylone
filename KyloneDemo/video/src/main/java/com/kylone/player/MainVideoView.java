package com.kylone.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kylone.player.controller.ControllerManager;
import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.MediaPerference;
import com.kylone.utils.UIRunnable;
import com.kylone.video.IPlayer;
import com.kylone.video.IPlayerInterface;
import com.kylone.video.IVideoFactory;
import com.kylone.video.VideoUrl;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.kylone.video.IVideoFactory.VIDEO_NATIVE;
import static com.kylone.video.IVideoFactory.VIDEO_SYSTEM;

/**
 * Created by  frank.z on 2016/1/12
 */
public class MainVideoView extends FrameLayout implements IPlayer, IPlayerInterface {
    protected final String TAG;
    public static final String SURFACE_TAG = "player";
//    private static final String TRY_HINT_DEFAULT = "正在试看，按 <font color='#00fe00'>OK</font> 键购买后即可完整观看";
//    private static final String TOP_HINT_DEFAULT = "VIP免广告 , 按 <font color='#ff7e00'>OK</font> 开通";

    protected String playType = VIDEO_NATIVE;
    protected int mCurrentState = STATE_IDLE;
    protected int mTargetState = STATE_IDLE;
    protected ControllerManager mControllerManager;
    // 监听器
    protected OnCompletionListener mOnCompletionListener;
    protected OnPreparedListener mOnPreparedListener;
    protected OnErrorListener mOnErrorListener;
    protected OnInfoListener mOnInfoListener;
    protected OnTimedTextChangedListener mOnTimedTextChangedListener;
    protected OnBufferingUpdateListener mOnBufferingUpdateListener;
    protected OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    protected OnSeekCompleteListener mOnSeekCompleteListener;
    protected OnLoadSDKListener mOnLoadSDKListener;
    protected OnLogoPositionListener mOnLogoPositionListener;
    protected OnPreAdPreparedListener mOnPreAdPreparedListener;
    protected OnPostrollAdPreparedListener mOnPostrollAdPreparedListener;
    protected OnDefinitionListener mOnDefinitionListener;
    protected OnMidAdPreparedListener mOnMidAdPreparedListener;

    private IPlayer player;
    private NetStateReceiver netReceiver;
    private Handler mHandler;
    private String mPath;
    private SparseArray<VideoUrl> mDefinitionList;
    private Map<String, String> mHeaders;
    private int mCurrentSize = SURFACE_BEST_FIT;
    private boolean mChangingPlayer;
    private boolean mAttached;
    private boolean isByUrl;
    private Timer mTimerPlaytime;
    private boolean mTickGo = true;
    TimerTask timerTask = null;
    private long mPostion = 0;
    private long playTime = 0;
    private long mBonusPlayTime = 0;

    private View surfaceFrame;
    private int errorCount;  //广告播放错误次数
    private BroadcastReceiver receiver;

    private LocalBroadcastManager localBroadcastManager;

    public MainVideoView(Context context) {
        super(context);
        TAG = String.format("MainVideoView@%s", Integer.toHexString(hashCode()));
        initViewSetting(null);
    }

    public MainVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TAG = String.format("MainVideoView@%s", Integer.toHexString(hashCode()));
        initViewSetting(attrs);

    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (player != null) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    LogUtil.i("---onGlobalLayout---");
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    changeScale(mCurrentSize);
                }
            });
        }
    }

    private void initViewSetting(AttributeSet attrs) {
        SurfaceView view = new SurfaceView(getContext());
//        view.setBackgroundResource(android.R.color.T);
        view.setTag(SURFACE_TAG);
        addView(view);

        mHandler = new Handler(Looper.getMainLooper());
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        boolean focusable = true;
        int bR = 0;
        if (attrs != null) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String name = attrs.getAttributeName(i);
                if (TextUtils.equals(name, "background")) {
                    bR = attrs.getAttributeResourceValue(i, android.R.color.black);
                    LogUtil.i("bR : " + bR);
                } else if (TextUtils.equals(name, "focusable")) {
                    focusable = attrs.getAttributeBooleanValue(i, focusable);
                }
            }
        }
        if (bR == 0) {
            setBackgroundResource(android.R.color.black);
        }
        setFocusable(focusable);
        setFocusableInTouchMode(focusable);
    }

    /**
     * 注册activity生命周期的监听
     */
    private void registerMusicServiceCommand() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                LogUtil.d(TAG, " command : " + command);
                if (TextUtils.equals(command, "pause")) {
                    release();
                }
            }
        };
        IntentFilter filter = new IntentFilter("com.android.music.musicservicecommand");
        localBroadcastManager.registerReceiver(receiver, filter);
        LogUtil.i(TAG, "register");
    }

    /**
     * 注销 广播
     */
    public void unregisterMusicServiceCommand() {
        if (receiver != null) {
            LogUtil.i(TAG, "unregister");
            localBroadcastManager.unregisterReceiver(receiver);
            receiver = null;
        }
    }


    public void setMediaController(ControllerManager manager) {
        if (mControllerManager != null) {
            mControllerManager.hide();
            mControllerManager.onDetached();
        }
        mControllerManager = manager;
        attachMediaController();
    }

    /**
     * 将控制器添加引用到播放器上
     */
    private void attachMediaController() {
        if (mControllerManager != null) {
            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
            mControllerManager.setAnchorView(anchorView);
            if (mAttached && !mControllerManager.isAttached()) {
                mControllerManager.onAttached();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttached = true;
        netReceiver = new NetStateReceiver(this);
        getContext().registerReceiver(netReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (mControllerManager != null && !mControllerManager.isAttached()) {
            mControllerManager.onAttached();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.i(TAG, " onDetachedFromWindow  ");
        mAttached = false;

        if (netReceiver != null) {
            getContext().unregisterReceiver(netReceiver);
            netReceiver = null;
        }
        if (mControllerManager != null && mControllerManager.isAttached()) {
            mControllerManager.onDetached();
        }
        removeAllViews();
        release();
    }

    private boolean mIgnorePosition = false;

    public void setmIgnorePosition(boolean mIgnorePosition) {
        this.mIgnorePosition = mIgnorePosition;
    }

    @Override
    public void onPrepared(IPlayer mp) {
        mTargetState = STATE_PREPARED;

        mCurrentState = STATE_PREPARED;
        LogUtil.i("-----onPrepared------------");

        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(mp);
        }
        try {
            if (mTimerPlaytime == null) {
                mTimerPlaytime = new Timer();
                if (timerTask != null) {
                    timerTask.cancel();
                }
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        long postion = getPosition();
                        if (mTickGo && (mPostion != postion || mIgnorePosition)) {
                            playTime += 1000;
                            mPostion = postion;
                        }
                    }
                };
                mTimerPlaytime.schedule(timerTask, 1000, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTickGo = true;
//        playTime = 0;
    }

    @Override
    public void onCompletion(IPlayer mp) {
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mp);
        }
    }

    @Override
    public boolean onError(IPlayer mp, int what, final int extra) {
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        HandlerUtils.runUITask(new UIRunnable(what, extra) {
            @Override
            public void run() {
                int what = (Integer) getObjs()[0];
                int extra = (Integer) getObjs()[1];
                String s = String.format(Locale.getDefault(), "播放视频失败(%d,%s) ", what, extra);
                LogUtil.e(s);
                if (TextUtils.equals(getPlayType(), VIDEO_SYSTEM)) {
                    setPlayType(VIDEO_NATIVE);
                    LogUtil.e(TAG, "change to soft decode play");
                    openVideo();
                    return;
                }
                if (mOnErrorListener != null)
                    mOnErrorListener.onError(MainVideoView.this, what, extra);
            }
        });
        return true;
    }

    @Override
    public void onBufferingUpdate(IPlayer mp, int percent) {
        if (mOnBufferingUpdateListener != null)
            mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
    }

    @Override
    public void onDefinition(SparseArray<VideoUrl> defnInfoList, VideoUrl currentDefn) {
        if (defnInfoList == null || defnInfoList.size() < 1 || currentDefn == null) {
            return;
        }
        mDefinitionList = defnInfoList;
        if (mOnDefinitionListener != null)
            mOnDefinitionListener.onDefinition(defnInfoList, currentDefn);
    }

    @Override
    public boolean onInfo(IPlayer mp, int what, int extra, Bundle extraData) {
        return mOnInfoListener != null && mOnInfoListener.onInfo(mp, what, extra, extraData);
    }

    @Override
    public void onLoadSDKCompletion() {
        if (mOnLoadSDKListener != null) {
            mOnLoadSDKListener.onLoadSDKCompletion();
        }
    }

    @Override
    public void onLogoPosition(int x, int y, int width, int height, boolean isShow) {
        if (mOnLogoPositionListener != null) {
            mOnLogoPositionListener.onLogoPosition(x, y, width, height, isShow);
        }
    }

    @Override
    public void onMidAdPrepared(IPlayer mp, long time) {
        if (mOnMidAdPreparedListener != null) {
            mOnMidAdPreparedListener.onMidAdPrepared(mp, time);
        }
    }

    @Override
    public void onPostrollAdPrepared(IPlayer mp, long time) {
        if (mOnPostrollAdPreparedListener != null) {
            mOnPostrollAdPreparedListener.onPostrollAdPrepared(mp, time);
        }
    }

    @Override
    public void onPreAdPrepared(IPlayer mp, long time) {
        mTargetState = STATE_AD_PREPARED;
        LogUtil.i("onPreAdPrepared  time =" + time);
        duration = -1;
        mCurrentState = STATE_AD_PREPARED;
        if (player != null)
            player.start();
        if (mOnPreAdPreparedListener != null) {
            mOnPreAdPreparedListener.onPreAdPrepared(mp, time);
        }

    }

    @Override
    public void onSeekComplete(IPlayer mp) {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(mp);
        }
    }

    @Override
    public void onTimedTextChanger(String text, long stat, long end) {
        if (mOnTimedTextChangedListener != null) {
            mOnTimedTextChangedListener.onTimedTextChanger(text, stat, end);
        }
    }

    @Override
    public void onVideoSizeChanged(IPlayer mp, int width, int height) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height);
        }


        HandlerUtils.runUITask(new UIRunnable(width, height) {
            @Override
            public void run() {
                int width = (Integer) getObjs()[0];
                int height = (Integer) getObjs()[1];
                if (width != 0 && height != 0) {
                    changeScale(mCurrentSize);
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.i(String.format(Locale.CHINA, " event action  %s   /  keycode %d", event.getAction() == KeyEvent.ACTION_DOWN ? "down" : "up", event.getKeyCode()));
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && mCurrentState == STATE_PAUSED) {
                start();
                return true;
            }
        }
        boolean isDispatch = mControllerManager != null && !mControllerManager.isEnabled()
                && mControllerManager.dispatchKeyEvent(event);
        return isDispatch || super.dispatchKeyEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isDispatch = mControllerManager != null && !mControllerManager.isEnabled()
                && mControllerManager.dispatchTouchEvent(ev);
        return isDispatch || super.dispatchTouchEvent(ev);
    }

    /**
     * 设置播放器类型
     *
     * @param type target Type
     */
    public synchronized void setPlayType(String type) {
        if (player != null && TextUtils.equals(type, playType)) {
            return;
        }
        mChangingPlayer = true;
        LogUtil.i("current play type  " + playType + "   ,  target play type " + type);
        playType = type;
    }

    public String getPlayType() {
        return playType;
    }

    public long getPlayTime() {
        long duration = getDuration();
        if (playTime < 0) {
            if (duration > 100) {
                playTime = duration;
            } else {
                playTime = 100;
            }
        } else if (duration > 100 && playTime > duration) {
            //如果时长可以取到 并且计算的时间大于观看时间,则用时长代替
            playTime = duration;
        }
        if (playTime > 14400000) {//时间大于四个小时,按两个小时算
            playTime = 7200000;
        }
        return playTime;
    }

    public void clearTime() {
        mTickGo = false;
        playTime = 0;
    }

    public synchronized void openVideo() {
        LogUtil.i(TAG, "open video");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //停止其他音乐播放
                if (getContext() != null) {
                    LogUtil.i(TAG, " send pause broadcast");
                    Intent i = new Intent("com.android.music.musicservicecommand");
                    i.putExtra("command", "pause");
                    getContext().sendBroadcast(i);
                    localBroadcastManager.sendBroadcastSync(i);
                }
                if (player != null) {
                    release();
                    player = null;
                }
                mCurrentState = STATE_PREPARING;
                mTargetState = STATE_PREPARING;
                playType = MediaPerference.getBoolean(IPlayer.SETTING_SOFT_DECODE)?VIDEO_NATIVE:VIDEO_SYSTEM;
                player = IVideoFactory.createIVideo(getContext(), playType);
//                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                addPlayerView(player);
                player.setOnBufferingUpdateListener(MainVideoView.this);
                player.setOnCompletionListener(MainVideoView.this);
                player.setOnPreparedListener(MainVideoView.this);
                player.setOnErrorListener(MainVideoView.this);
                player.setOnSeekCompleteListener(MainVideoView.this);
                player.setOnInfoListener(MainVideoView.this);
                player.setOnTimedTextChangedListener(MainVideoView.this);
                player.setOnVideoSizeChangedListener(MainVideoView.this);
                player.setOnPreAdPreparedListener(MainVideoView.this);
                player.setOnPostrollAdPreparedListener(MainVideoView.this);
                player.setOnMidAdPreparedListener(MainVideoView.this);
                player.setOnLogoPositionListener(MainVideoView.this);
                player.setOnDefinitionListener(MainVideoView.this);
                player.setOnLoadSDKListener(MainVideoView.this);
                player.setAutoCharge(mAutoCharge);
//        player.changeScale(mCurrentSize);
                setKeepScreenOn(true);
                mChangingPlayer = false;
                LogUtil.i(TAG, "创建播放器成功 playType = " + playType + ",mPath =" + mPath);
                if (mPath != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", mPath);
                    player.notification(bundle);
                    LogUtil.i(TAG, "设置播放地址");
                    if (isByUrl) {
                        setVideoPathByUrl(mPath, mHeaders);
                    } else {
                        setVideoPath();
                    }
                }
                registerMusicServiceCommand();
            }
        });

    }

    private void setVideoPath() {
        player.setVideoPath(mPath, mHeaders);
    }

    private void addPlayerView(IPlayer player) {
        if (player != null) {
            LogUtil.i(TAG, "addPlayerView ");
            View videoView = findViewWithTag(SURFACE_TAG);
            View surface = player.getTranslateView();
            if (surface != videoView) {
                surfaceFrame = surface;
                removeView(videoView);
                surface.setTag(SURFACE_TAG);
                surface.setFocusable(false);
                addView(surface, 0, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Deprecated
    public View getTranslateView() {
        //this
        return player != null ? player.getTranslateView() : null;
    }


    public IPlayer getIVideo() {
        return player;
    }

    @Override
    public void changeScale(int size) {
        LogUtil.d(TAG, "changeScale size = " + size);
        mCurrentSize = size;
        if (player != null) {
            player.changeScale(size);
        }
    }

    @Override
    public int getVideoWidth() {
        if (player != null) {
            return player.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (player != null) {
            return player.getVideoHeight();
        }
        return 0;
    }

    public int getSurfaceWidth() {
        return player != null ? player.getSurfaceWidth() : 0;
    }

    public int getSurfaceHeight() {
        return player != null ? player.getSurfaceHeight() : 0;
    }

    @Override
    public boolean release() {
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        LogUtil.i(TAG, "-----release-------- ");
        unregisterMusicServiceCommand();
        boolean r = player != null && player.release();
        try {
            if (mTimerPlaytime != null) {
                mTimerPlaytime.cancel();
                mTimerPlaytime = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mDefinitionList = null;
//        mPath = null;
//        mHeaders = null;

        player = null;
        duration = -1;
        return r;
    }

    @Override
    public void replay() {
//        player = null;
        openVideo();
    }

    @Override
    public void setAdFrame(ViewGroup adFrame) {
        player.setAdFrame(adFrame);
    }

    @Override
    public void start() {
        try {
            if (player != null && (isPlaybackState() || mCurrentState == STATE_AD_PREPARED)) {
                player.start();
                mCurrentState = STATE_PLAYING;

            }
            mTargetState = STATE_PLAYING;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            if (player != null && isPlaybackState()) {
                player.pause();
                mCurrentState = STATE_PAUSED;
            }
            mTargetState = STATE_PAUSED;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(int position) {
        if (mCurrentState == STATE_AD_PREPARED || mCurrentState != mTargetState) {
            return;
        }
        try {
            if (player != null && isPlaybackState()) {
                player.seekTo(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

        try {
            if (player != null && isPlaybackState()) {
                player.stop();
                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public long getPosition() {
        if (mCurrentState == STATE_AD_PREPARED) {
            return 0;
        }
        try {
            if (player != null && isPlaybackState()) {
                return player.getPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long duration = -1;

    @Override
    public long getDuration() {
        if (mCurrentState == STATE_AD_PREPARED || mCurrentState != mTargetState) {
            return duration;
        }
        try {
            if (player != null && isPlaybackState()) {
                duration = player.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    @Override
    public int getBufferPercent() {
        try {
            if (player != null && isPlaybackState()) {
                return player.getBufferPercent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return player != null && isPlaybackState() && player.isPlaying();
    }

    @Override
    public boolean isPlaybackState() {
        return mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING;
    }

    @Override
    public boolean isPlayingAd() {
        return mCurrentState == STATE_AD_PREPARED || (player != null && player.isPlayingAd());
    }

    @Override
    public void reset() {
        if (player != null) {
            player.reset();
            mCurrentState = STATE_IDLE;
        }
        mTargetState = STATE_IDLE;
    }

    @Override
    public void setDecodeType(int decodeType) {
        if (player != null) {
            player.setDecodeType(decodeType);
        }
    }

    @Override
    public int getDecodeType() {
        if (player != null) {
            return player.getDecodeType();
        }
        return HARD_DECODE;
    }

    @Override
    public boolean isTry() {
        return player != null && player.isTry();
    }

    @Override
    public void startVipCharge() {
        Map<String, String> map = new HashMap<>();
        //type = 会员类型 如 腾讯会员|单片付费|国广会员
        //position = "广告时间"|"用户中心"|"影片试看"|"影片详情"|运营页

    }

    @Override
    public SparseArray<VideoUrl> getDefinitionList() {
        return mDefinitionList;
    }

    @Override
    public void switchDefinition(int definition) {
        LogUtil.i(TAG, "switch definition : " + definition);
        if (player != null) {
            player.switchDefinition(definition);
        }
    }

    /**
     * 内地址需要解析,
     * <p>
     * 通过实现 {@link OnDefinitionListener }接口获取解析后的清晰度列表
     *
     * @param path    路径地址
     * @param headers 头信息
     */
    @Override
    public void setVideoPath(String path, Map<String, String> headers) {

        LogUtil.i(TAG, " set video path");
        duration = -1;
        isByUrl = false;
        mPath = path;
        mHeaders = headers;
        errorCount = 0;
        if (isPlaying()) {
            try {
                LogUtil.i(" stop previous player");
                player.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        openVideo();
    }

    @Override
    public void setNextLoopVideoInfo(String path) {
        if (null != player) {
            player.setNextLoopVideoInfo(path);
        }
    }

    @Override
    public String notification(Bundle msg) {

        return null;
    }

    private boolean mAutoCharge = true;

    @Override
    public void setAutoCharge(boolean auto) {
        mAutoCharge = auto;
    }

    /**
     * 真实地址播放
     *
     * @param url     真实播放地址
     * @param headers 缺省
     */
    @Override
    public void setVideoPathByUrl(String url, Map<String, String> headers) {
        isByUrl = true;
        mPath = url;
        mHeaders = headers;
        if (null != player && !mChangingPlayer) {
            LogUtil.i("推入播放地址 path-->" + mPath);
            player.setVideoPathByUrl(mPath, mHeaders);
            mCurrentState = STATE_PREPARING;
        } else {
            openVideo();
        }
        mTargetState = STATE_PREPARING;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    @Override
    public void setOnPreAdPreparedListener(OnPreAdPreparedListener listener) {
        mOnPreAdPreparedListener = listener;
    }

    @Override
    public void setOnPostrollAdPreparedListener(OnPostrollAdPreparedListener listener) {
        mOnPostrollAdPreparedListener = listener;
    }

    @Override
    public void setOnMidAdPreparedListener(OnMidAdPreparedListener listener) {
        mOnMidAdPreparedListener = listener;
    }


    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnLogoPositionListener(OnLogoPositionListener onLogoPositionListener) {
        mOnLogoPositionListener = onLogoPositionListener;
    }

    @Override
    public void setSubtitlePath(Uri uri, long offset) {
        if (player != null) {
            player.setSubtitlePath(uri, offset);
        }
    }

    @Override
    public void setSubtitleOffset(long offset) {
        if (player != null) {
            player.setSubtitleOffset(offset);
        }
    }

    @Override
    public void setOnTimedTextChangedListener(OnTimedTextChangedListener listener) {
        mOnTimedTextChangedListener = listener;
    }

    @Override
    public void setOnLoadSDKListener(OnLoadSDKListener listener) {
        mOnLoadSDKListener = listener;
    }

    public void setOnDefinitionListener(OnDefinitionListener listener) {
        mOnDefinitionListener = listener;
    }


    private static class NetStateReceiver extends BroadcastReceiver {
        WeakReference<MainVideoView> weakReference;

        public NetStateReceiver(MainVideoView videoView) {
            super();
            weakReference = new WeakReference<MainVideoView>(videoView);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                MainVideoView mVideoView = weakReference.get();
                if (mVideoView == null) {
                    return;
                }
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    int type = info.getType();
                    LogUtil.i("当前网络类型 : " + type);
                    if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_ETHERNET) {
//                        if ( mVideoView.isPlaybackState()) {
//                            mVideoView.start();
//                        }
//                        Toast.makeText(context,"网络已连接!",Toast.LENGTH_LONG).onShow();

                    } else {
                        Toast.makeText(context, "当前使用的是手机网络,请注意流量!", Toast.LENGTH_LONG).show();
                    }
//                    String name = info.getTypeName();
//                    LogUtil.i("当前网络名称 : " + name);
//                    NetworkInfo.State s = info.getState();
//                    String sName = s.name();
//                    LogUtil.i("当前网络状态 :" + sName);

                } else {
//                    if (mVideoView.isPlaybackState()) {
//                        mVideoView.pause();
//                    }
//                    LogUtil.i("没有可用网络");
                    Toast.makeText(context, "网络连接已断开!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean mIgnoreEnable;

    public void setIgnoreEnable(boolean ignore) {
        mIgnoreEnable = ignore;
    }

}
