package com.kylone.video;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kylone.utils.HandlerUtils;
import com.kylone.utils.LogUtil;
import com.kylone.utils.StringUtils;
import com.kylone.utils.ThreadManager;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zack
 */
public class BaseVideoView extends IVideoView implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private static final String TAG = "BaseVideoView";
    private static final int HANDLER_MSG_RELEASE = 50001;
    private static final int PREVIEW_DURATION = 300000;
    private int startPosition;
    private boolean isNeedOpen;
    private Handler threadHandler;

    protected MediaPlayer mMediaPlayer = null;
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected long mDuration = -1;
    protected boolean isPrepared = false;
    protected boolean isClose;
    protected int mBufferPercent;
    protected int lastDefinition = -1; //上个清晰度
    protected int currentQuality = IPlayer.DEFINITION_AUTO;
    protected VideoUrl mCurrentQuality;
    protected SparseArray<VideoUrl> mQualityList;
    protected String mPath;
    protected Map<String, String> mHeaders;



    public BaseVideoView(Context context) {
        super(context);
        init();
    }

    @Override
    public int getVideoWidth() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public int getSurfaceWidth() {
        return mSurfaceView != null ? mSurfaceView.getWidth() : 0;
    }

    @Override
    public int getSurfaceHeight() {
        return mSurfaceView != null ? mSurfaceView.getHeight() : 0;
    }

    private void init() {
        mVideoWidth = 0;
        mVideoHeight = 0;

        mSurfaceView = new SurfaceView(mContext);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSHCallback);
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceView.setKeepScreenOn(true);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        HandlerThread ht = new HandlerThread("video");
        ht.start();
        threadHandler = new THandler(ht.getLooper(), this);
    }

    /**
     * 运行在线程上
     */
    class THandler extends Handler {
        private WeakReference<BaseVideoView> serviceRef;

        public THandler(Looper looper, BaseVideoView view){
            super(looper);
            serviceRef = new WeakReference<BaseVideoView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseVideoView video = serviceRef.get();
            if (video == null){
                return;
            }
            switch (msg.what){
                case HANDLER_MSG_RELEASE:
                    video._release();
                    break;
            }
        }
    }

    protected void openVideo() {
        isNeedOpen = true;
        LogUtil.i(TAG,"--openVideo--");
        if (TextUtils.isEmpty(mPath) || mSurfaceHolder == null || !isSurfaceCreated) {
            LogUtil.e(TAG, String.format(Locale.getDefault(), "path = %s , surfaceHolder = %s , player = %s ", mPath, mSurfaceHolder, mMediaPlayer));
            LogUtil.e(TAG, "not ready for playback just yet");
            return;
        }
        _release();
        try {
            isPrepared = false;
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mDuration = -1;
            mMediaPlayer.setDataSource(mContext, Uri.parse(mPath), null);
            mMediaPlayer.prepareAsync();
            LogUtil.i(TAG, "开始异步加载MediaPlayer");
            isNeedOpen = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchDefinition(int definition) {
        isClose = true;
        LogUtil.i("currentQuality : " + currentQuality + "---switchDefinition------" + definition);
        if (mQualityList != null && mQualityList.size() > 0) {
            VideoUrl videoUrl = mQualityList.get(definition);
            if (videoUrl != null) {
                lastDefinition = currentQuality;
                mCurrentQuality = videoUrl;
                currentQuality = definition;
                startPosition = (int) getPosition();
                setVideoPathByUrl(mCurrentQuality.url, null);
            }
        }
    }

    @Override
    public SparseArray<VideoUrl> getDefinitionList() {
        return mQualityList;
    }

    @Override
    public View getTranslateView() {
        return mSurfaceView;
    }


    private boolean isSurfaceCreated;
    /**
     * surface 回调
     */
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            LogUtil.i(TAG, "surface变更...." + holder);
            HandlerUtils.runUITask(new Runnable() {
                @Override

                public void run() {
                    try {
                        if (mMediaPlayer != null && mSurfaceHolder.getSurface() != null && mSurfaceHolder.getSurface().isValid()) {
                            mMediaPlayer.setDisplay(mSurfaceHolder);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mSurfaceHolder = holder;
//            mSurfaceWidth = w;
//            mSurfaceHeight = h;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.i(TAG, "surface创建...." + holder);
            mSurfaceHolder = holder;
            isSurfaceCreated = true;
            if (mPath != null && isNeedOpen)
                openVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.i(TAG, "surface销毁....");
            mSurfaceHolder = null;
            isSurfaceCreated = false;
//            release();
        }
    };

    @Override
    public void replay() {
        LogUtil.i(TAG, "replay");
        openVideo();
    }

    @Override
    public void start() {
        try {
            if (mMediaPlayer != null && isPrepared && !isPlaying()) {
                LogUtil.i(TAG, "start");
                mMediaPlayer.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && isPrepared && isPlaying()) {
            LogUtil.i(TAG, "pause");
            mMediaPlayer.pause();
        }
    }

    @Override
    public void seekTo(int pos) {
        try {
            if (mMediaPlayer != null && isPrepared && pos >= 0) {
                if (isTry() && pos >= PREVIEW_DURATION) {
                    if (mOnCompletionListener!=null){
                        mOnCompletionListener.onCompletion(this);
                    }
                    return;
                }
                LogUtil.d(TAG, "seek  to  " + pos);
                mMediaPlayer.seekTo(pos);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getPosition() {
        try {
            if (mMediaPlayer != null && isPrepared) {
                return mMediaPlayer.getCurrentPosition();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public long getDuration() {
        if (isTry()) {
            return (mMediaPlayer != null && isPrepared) ? PREVIEW_DURATION : -1;
        }
        if (mDuration > 0) {
            return mDuration;
        }
        try {
            if (mMediaPlayer != null && isPrepared) {
                mDuration = mMediaPlayer.getDuration();
                if (mDuration <= 0) {
                    mDuration = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDuration;
    }

    @Override
    public int getBufferPercent() {
        return mBufferPercent;
    }

    @Override
    public boolean isPlaying() {
        try {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean release() {
        //判断是否主线程
        if ( Thread.currentThread() != Looper.getMainLooper().getThread()){
            return _release();
        }
        threadHandler.sendEmptyMessage(HANDLER_MSG_RELEASE);
        return false;
    }

    private boolean _release() {
        try {
            if (mMediaPlayer != null) {
                isPrepared = false;
                mDuration = -1;
//                mMediaPlayer.setDisplay(null);
//                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                LogUtil.i(TAG, "释放播放器");
            }
//            mQualityList = null;
//            mCurrentQuality = null;
            isClose = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            isPrepared = false;
            mDuration = -1;
            mMediaPlayer.reset();
        }
    }

    @Override
    public int getDecodeType() {
        return HARD_DECODE;
    }

    @Override
    public boolean isTry() {
        return false;
    }

    /**
     * 设置播放路径
     *
     * @param path    path
     * @param headers headers
     */
    @Override
    public void setVideoPath(final String path, Map<String, String> headers) {
        LogUtil.i(TAG, "--------setVideoPath------------" + path);
        mPath = null;
        mHeaders = null;
        if (headers != null) {
            String qua = headers.get(IPlayer.KEY_DEFINITION);

            currentQuality = StringUtils.parseInt(qua, IPlayer.DEFINITION_AUTO);
            startPosition = StringUtils.parseInt(headers.get(KEY_INTENT_POSITION), 0);
        }
    }


    /**
     * 不需要解析直接播放
     *
     * @param path    播放地址
     * @param headers 待用
     */
    @Override
    public void setVideoPathByUrl(final String path, final Map<String, String> headers) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String mUrl = parseUrl(path);
                if (TextUtils.isEmpty(mUrl)) {
                    currentQuality = lastDefinition;
                    lastDefinition = -1;
                }
                if (mOnDefinitionListener != null) {
//                    VideoUrl currentUrl = null;
//                    if (mQualityList != null && currentQuality < mQualityList.size()) {
//                        currentUrl = mQualityList.get(currentQuality);
//                    } else {
//                        currentUrl = new VideoUrl();
//                        currentUrl.quality = currentQuality;
//                    }

                    mOnDefinitionListener.onDefinition(mQualityList, mCurrentQuality);
                }

                mPath = mUrl;
                mHeaders = headers;
                openVideo();
            }
        };

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            ThreadManager.execute(runnable);
        } else {
            runnable.run();
        }
    }

    public String parseUrl(String url) {
        return url;
    }

    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (isPlaying()) {
            mMediaPlayer.stop();
            isPrepared = false;
            mDuration = -1;
        }
    }

    @Override
    public void changeScale(int size) {
        mCurrentSize = size;
        if (mSurfaceHolder != null) {
            changeSurfaceSize(mVideoWidth, mVideoHeight, mSarNum, mSarDen);
        }
    }


    /**
     * 变更Surface大小 张兴 2015-3-24下午4:04:48
     */
    protected void changeSurfaceSize(int width, int height, int sar_num, int sar_den) {
        if (mSurfaceHolder == null) {
            LogUtil.e(TAG, "无效的 surface ");
            return;
        }
        ViewGroup parent = (ViewGroup) mSurfaceView.getParent();
        if (parent == null) {
            return;
        }
        int dw = parent.getWidth();
        int dh = parent.getHeight();

        LogUtil.i(TAG, "  dw =" + dw + " , dh =" + dh);
        int[] s = changeSize(dw, dh);
        if (s == null) {
            return;
        }
        dw = s[0];
        dh = s[1];

        LogUtil.i(TAG, "  dw =" + dw + " , dh =" + dh);
        mSurfaceHolder.setFixedSize(width, height);
        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).gravity = Gravity.CENTER;
        }
        mSurfaceView.setLayoutParams(lp);
//        mSurfaceView.requestLayout();
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mBufferPercent = percent;
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!isPrepared) {
            LogUtil.e(TAG, "onCompletion is called in a error status , is not prpared");
            return;
        }
        LogUtil.i(TAG, "onCompletion");
        release();
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, final int what, final int extra) {
        LogUtil.i(TAG, String.format("onError  what[%s],extra[%s]", what, extra));
        return mOnErrorListener != null && mOnErrorListener.onError(this, what, extra);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, final int what, final int extra) {
        LogUtil.i(TAG, String.format("onInfo  what[%s],extra[%s]", what, extra));
        return mOnInfoListener != null && mOnInfoListener.onInfo(this, what, 0, null);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        changeScale(mCurrentSize);
        LogUtil.i(TAG, "base video onPrepared");
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        isPrepared = true;
        if (startPosition > 0) {
            seekTo(startPosition);
            startPosition = 0;
        }

        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
//        start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(this, mVideoWidth, mVideoHeight);
        }

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(BaseVideoView.this, MediaPlayer.MEDIA_INFO_BUFFERING_END, 0, null);
        }
    }
}
