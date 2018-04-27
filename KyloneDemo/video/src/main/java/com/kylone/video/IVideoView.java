package com.kylone.video;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class IVideoView implements IPlayer {


    //    protected ControllerManager mControllerManager;
    // 监听器
    protected OnCompletionListener mOnCompletionListener;
    protected OnPreparedListener mOnPreparedListener;
    protected OnErrorListener mOnErrorListener;
    protected OnInfoListener mOnInfoListener;
    protected OnDefinitionListener mOnDefinitionListener;
    protected OnTimedTextChangedListener mOnTimedTextChangedListener;
    protected OnBufferingUpdateListener mOnBufferingUpdateListener;
    protected OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    protected OnSeekCompleteListener mOnSeekCompleteListener;
    protected OnLoadSDKListener mOnLoadSDKListener;
    protected OnPreAdPreparedListener mOnPreAdPreparedListener;   //前贴广告准备完成
    protected OnPostrollAdPreparedListener mOnPostrollAdPreparedListener;//后贴广告准备完成
    protected OnMidAdPreparedListener mOnMidAdPreparedListener;
    protected OnLogoPositionListener mOnLogoPositionListener;

    protected int mVideoWidth;
    protected int mVideoHeight;
    protected int mSarNum = 1;
    protected int mSarDen = 1;

    protected int mCurrentSize = SURFACE_BEST_FIT;
    protected Context mContext;

    protected ViewGroup adFrame;
    protected boolean mAutoCharge = true;

    public IVideoView(Context context) {
        mContext = context;
    }


    @Override
    public View getTranslateView() {
        return null;
    }

    public void changeScale(int size) {

    }

    @Override
    public void setAdFrame(ViewGroup adFrame) {
        this.adFrame = adFrame;
    }

    @Override
    public int getSurfaceWidth() {
        return 0;
    }

    @Override
    public int getSurfaceHeight() {
        return 0;
    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public void replay() {
    }

    @Override
    public void start() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void seekTo(int position) {
    }

    @Override
    public void stop() {
    }

    @Override
    public long getPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return -1;
    }

    @Override
    public int getBufferPercent() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isPlaybackState() {
        return false;
    }

    @Override
    public boolean isPlayingAd() {
        return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public void setDecodeType(int decodeType) {
    }

    @Override
    public int getDecodeType() {
        return 0;
    }

    @Override
    public boolean isTry() {
        return false;
    }

    @Override
    public void startVipCharge() {

    }

    @Override
    public SparseArray<VideoUrl> getDefinitionList() {
        return null;
    }

    @Override
    public void switchDefinition(int definition) {

    }


    /**
     * @param path    路径地址
     * @param headers 头信息  key = {@link #KEY_INTENT_POSITION} 会传入播放起始位置
     */
    @Override
    public void setVideoPath(String path, Map<String, String> headers) {
    }

    @Override
    public void setVideoPathByUrl(String url, Map<String, String> headers) {

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
    public void setOnSeekCompleteListener(OnSeekCompleteListener mOnSeekCompleteListener) {
        this.mOnSeekCompleteListener = mOnSeekCompleteListener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mOnPreparedListener = listener;
    }

    @Override
    public void setOnDefinitionListener(OnDefinitionListener listener) {
        mOnDefinitionListener = listener;
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
    }

    @Override
    public void setSubtitleOffset(long offset) {
    }

    @Override
    public void setOnTimedTextChangedListener(OnTimedTextChangedListener listener) {
        mOnTimedTextChangedListener = listener;
    }

    @Override
    public void setNextLoopVideoInfo(String path) {

    }

    @Override
    public String notification(Bundle msg) {
        return "";
    }


    @Override
    public void setAutoCharge(boolean auto) {
        mAutoCharge = auto;
    }

    public void setOnLoadSDKListener(OnLoadSDKListener mOnLoadSDKListener) {
        this.mOnLoadSDKListener = mOnLoadSDKListener;
    }

    protected int[] changeSize(int dw, int dh) {
        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e("IVideoView", "无效的 surface size");
            return null;
        }
        double density = (double) mSarNum / (double) mSarDen;
        double ar = (double) mVideoWidth / (double) mVideoHeight * density;
        double dar = (double) dw / (double) dh;
        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
        }
        return new int[]{dw, dh};
    }

}
