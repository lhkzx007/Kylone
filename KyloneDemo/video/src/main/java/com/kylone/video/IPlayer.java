package com.kylone.video;

import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * 播放器公共接口 User: zhang.xing Date: 2015-07-13 Time: 16:16
 */
public interface IPlayer {
    int SURFACE_BEST_FIT = 0;  //原始比例
    int SURFACE_16_9 = 2;
    int SURFACE_4_3 = 3;
    int SURFACE_FILL = 1;    //填充

    int DEFINITION_AUTO = -1;// 自动
    int DEFINITION_LD = 0; // 流畅
    int DEFINITION_SD = 1; // 标清
    int DEFINITION_HD = 2; // 高清
    int DEFINITION_FULLHD = 3; // 超清
    int DEFINITION_BLUE = 4; // 蓝光
    int DEFINITION_1080P = 5; // 原画
    int DEFINITION_4K = 6; // 4K

    int[] DEFINITION_ORDER = {DEFINITION_LD, DEFINITION_SD, DEFINITION_HD, DEFINITION_FULLHD, DEFINITION_BLUE,
            DEFINITION_1080P, DEFINITION_4K}; // 清晰度排序排序

    int HARD_DECODE = 100;
    int SOFT_DECODE = 101;
    int INTELLIGENT_DECODE = 102;
    int VLC_INIT_ERROR = 1000;
    int VLC_ERROR = 1001;
    int VLC_INFO_POSITION_CHANGED = 1004;
    int MEDIA_INFO_TIMEOUT = 0xffff;

    // all possible internal states
    int STATE_ERROR = -1; // 错误状态
    int STATE_IDLE = 0; // 闲置状态
    int STATE_PREPARING = 1; // 准备中
    int STATE_AD_PREPARED = 7; // 广告准备完成
    int STATE_AD_PREPARING = 8; // 广告准备中
    int STATE_PREPARED = 2; // 准备结束
    int STATE_PLAYING = 3; // 播放中
    int STATE_PAUSED = 4; // 暂停中
    int STATE_PLAYBACK_COMPLETED = 5; // 播放完成
    int STATE_CHANGE_TYPE = 6; // 播放完成

    int PLAY_TYPE_PREVUE = 0;                                //预告片
    int PLAY_TYPE_FEATURE_FILM = 1;                  //正片
    int PLAY_TYPE_CASE_FILM = 2;                          //抢先看
    int PLAY_TYPE_TENCENT_QE = 3;                      //企鹅会员
    int PLAY_TYPE_TENCENT_DJ = 4;                       //鼎级剧场
    int PLAY_TYPE_TENCENT_TY = 5;                      //体育会员
    int PLAY_TYPE_TENCENT_QD = 6;                     //企鹅影院（腾讯会员）+鼎级剧场（腾讯会员）
    int PLAY_TYPE_TENCENT_QT = 7;                     //企鹅影院（腾讯会员）+体育会员（腾讯会员）
    int PLAY_TYPE_TENCENT_DT = 8;                     //鼎级剧场（腾讯会员）+体育会员（腾讯会员）
    int PLAY_TYPE_TENCENT_QDT = 9;                  //企鹅影院（腾讯会员）+鼎级剧场（腾讯会员）+体育会员（腾讯会员）
    int PLAY_TYPE_DD = 11;                  //单点
    int PLAY_TYPE_BY = 10;                  //包月
    int PLAY_TYPE_DDBY = 12;                  //单点+包月
    int PLAY_TYPE_QUAN = 13;                  //用券
    String KEY_INTENT_TRY = "isTry";                        //是否试看
    String KEY_INTENT_POSITION = "startPosition";           //开始播放的位置
    String KEY_INTENT_MEDIA_TYPE = "mediaType";             // 播放类型 1直播 ,2 点播 ,3 轮播
    String KEY_TITLE = "title";                             // 当前播放影片的名
    String KEY_INTENT_PREVUE = "prevue";
    String KEY_INTENT_UUID = "uuid";                        //影片的UUID
    String KEY_INTENT_NUM = "setnum";                       //影片的集数
    String KEY_SCANMODE = "scanModel";
    String KEY_INTENT_VIP = "isVip";                        //用于电视剧限免判断 value 1 收费  /  2 免费
    String KEY_DEFINITION = "definition";                   //播放器清晰度预设
    String KEY_INTENT_LUNBO = "isLunbo";                    //是否轮播
    String KEY_INTENT_LUNBO_POSITION = "lunbo_position";    //轮播起始位置
    String KEY_SET_INDEX = "setIndex"; //当前播放集数的序列号  , (在总集数中的第几集)


    int MEDIA_TYPE_LIVE = 1;              //直播
    int MEDIA_TYPE_VOD = 2;              //点播
    int MEDIA_TYPE_LOOP_VOD = 8; //轮播


    int INFO_MSG_START_CHARGE = 100001;  //开始调起购买页面


    /**
     * 清晰度解析为空
     */
    int MEDIA_ERROR_DEFINITION_EMPTY = 10004;

    /**
     * Unspecified media player error.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_UNKNOWN = 1;

    /**
     * Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_SERVER_DIED = 100;

    /**
     * The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    /**
     * File or network related operation errors.
     */
    int MEDIA_ERROR_IO = -1004;
    /**
     * Bitstream is not conforming to the related coding standard or file spec.
     */
    int MEDIA_ERROR_MALFORMED = -1007;
    /**
     * Bitstream is conforming to the related coding standard or file spec, but
     * the media framework does not support the feature.
     */
    int MEDIA_ERROR_UNSUPPORTED = -1010;
    /**
     * Some operation takes too long to complete, usually more than 3-5 seconds.
     */
    int MEDIA_ERROR_TIMED_OUT = -110;

    // /**
    // *
    // */
    // void setMediaController(ControllerManager manager);

    View getTranslateView();

    void changeScale(int size);

    void setAdFrame(ViewGroup adFrame);

    int getSurfaceWidth();

    int getSurfaceHeight();

    /**
     * 获取影片的宽度
     *
     * @return
     */
    int getVideoWidth();

    /**
     * 获取影片的高度
     *
     * @return
     */
    int getVideoHeight();

    /**
     * 释放
     *
     * @return
     */
    boolean release();

    /**
     * 重新播放
     */
    void replay();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 拖动至时间点 position
     *
     * @param position 指定时间点
     */
    void seekTo(int position);

    /**
     * 停止播放
     */
    void stop();

    /**
     * 获取当前播放位置
     *
     * @return long
     */
    long getPosition();

    /**
     * 获取播放总时长
     *
     * @return long
     */
    long getDuration();

    /**
     * 获取缓冲进度
     *
     * @return int 缓冲进度
     */
    int getBufferPercent();

    /**
     * 判断是否正在播放中
     *
     * @return boolean
     */
    boolean isPlaying();

    /**
     * 是否在播放状态
     *
     * @return
     */
    boolean isPlaybackState();

    /**
     * 是否播放广告
     *
     * @return
     */
    boolean isPlayingAd();

    /**
     * 重置播放器 将播放器设置到初始化状态
     */
    void reset();

    void setDecodeType(int decodeType);

    int getDecodeType();

    /**
     * 当前影片是否为试看
     *
     * @return
     */
    boolean isTry();

    /**
     * 开启vip
     */
    void startVipCharge();

    /**
     * 获取清晰度列表
     *
     * @return
     */
    SparseArray<VideoUrl> getDefinitionList();

    /**
     * 切换清晰度
     *
     * @param definition
     */
    void switchDefinition(int definition);

    /**
     * 设置播放路径(需要解析清晰度)
     *
     * @param path    路径地址
     * @param headers 头信息
     */
    void setVideoPath(String path, Map<String, String> headers);

    /**
     * 设置播放路径 (不需要解析清晰度)
     *
     * @param url 播放地址
     */
    void setVideoPathByUrl(String url, Map<String, String> headers);

    /*---------------相关监听器对象引用------------------------------*/
    void setOnErrorListener(OnErrorListener listener);

    void setOnInfoListener(OnInfoListener listener);

    void setOnCompletionListener(OnCompletionListener listener);

    void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

    void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    void setOnPreparedListener(OnPreparedListener listener);

    /**
     * 设置清晰度解析后的回调
     *
     * @param listener
     */
    void setOnDefinitionListener(OnDefinitionListener listener);

    /**
     * 前贴广告准备播放
     *
     * @param listener
     */
    void setOnPreAdPreparedListener(OnPreAdPreparedListener listener);

    /**
     * 后贴广告准备中
     *
     * @param listener
     */
    void setOnPostrollAdPreparedListener(OnPostrollAdPreparedListener listener);

    /**
     * 后贴广告准备中
     *
     * @param listener
     */
    void setOnMidAdPreparedListener(OnMidAdPreparedListener listener);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);

    void setOnLogoPositionListener(OnLogoPositionListener onLogoPositionListener);

    void setOnLoadSDKListener(OnLoadSDKListener mOnLoadSDKListener);
    /*-----------字幕相关设置--------------------*/

    /**
     * 字幕路径
     *
     * @param uri
     * @param offset
     */
    void setSubtitlePath(Uri uri, long offset);

    /**
     * 字幕出现时间偏移量
     *
     * @param offset
     */
    void setSubtitleOffset(long offset);

    /**
     * 设置字幕文字出现时间监听器对象
     *
     * @param listener
     */
    void setOnTimedTextChangedListener(OnTimedTextChangedListener listener);

    /**
     * 设置轮播下个播放地址
     *
     * @param path
     */
    void setNextLoopVideoInfo(String path);

    /**
     * @param msg
     * @return
     */
    String notification(Bundle msg);

    /**
     * 加载SDK 及 SO 后的回调
     *
     * @author zhangxing
     */
    interface OnLoadSDKListener {
        void onLoadSDKCompletion();
    }

    /**
     * 播放错误回调
     */
    interface OnErrorListener {
        /**
         * Called to indicate an error.
         *
         * @param mp    the MediaPlayer the error pertains to
         * @param what  the type of error that has occurred:
         *              <ul>
         *              <li>{@link #MEDIA_ERROR_UNKNOWN}
         *              <li>{@link #MEDIA_ERROR_SERVER_DIED}
         *              <li>{@link #MEDIA_ERROR_DEFINITION_EMPTY}
         *              </ul>
         * @param extra an extra code, specific to the error. Typically
         *              implementation dependent.
         *              <ul>
         *              <li>{@link #MEDIA_ERROR_IO}
         *              <li>{@link #MEDIA_ERROR_MALFORMED}
         *              <li>{@link #MEDIA_ERROR_UNSUPPORTED}
         *              <li>{@link #MEDIA_ERROR_TIMED_OUT}
         *              </ul>
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all,
         * will cause the OnCompletionListener to be called.
         */
        boolean onError(IPlayer mp, int what, int extra);
    }

    /**
     * 播放信息回调
     */
    interface OnInfoListener {
        boolean onInfo(IPlayer mp, int what, int extra, Bundle extraData);
    }

    /**
     * 播放缓冲更新回调
     */
    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IPlayer mp, int percent);
    }

    /**
     * 播放完成回调
     */
    interface OnCompletionListener {
        void onCompletion(IPlayer mp);
    }

    /**
     * 视频大小变更回调
     */
    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IPlayer mp, int width, int height);
    }

    /**
     * 清晰度解析后的回调
     */
    interface OnDefinitionListener {
        void onDefinition(SparseArray<VideoUrl> defnInfoList, VideoUrl currentDefn);
    }

    /**
     * 播放准备完成回调
     */
    interface OnPreparedListener {
        void onPrepared(IPlayer mp);
    }

    /**
     * 前贴广告准备完成  可以播放
     */
    interface OnPreAdPreparedListener {
        void onPreAdPrepared(IPlayer mp, long time);
    }

    /**
     * 后贴广告准备完成  可以播放
     */
    interface OnPostrollAdPreparedListener {
        void onPostrollAdPrepared(IPlayer mp, long time);
    }

    /**
     * 中插广告准备完成  可以播放
     */
    interface OnMidAdPreparedListener {
        void onMidAdPrepared(IPlayer mp, long time);
    }

    /**
     * 拖动完成回调
     */
    interface OnSeekCompleteListener {
        void onSeekComplete(IPlayer mp);
    }

    interface OnVipChargeListener {
        void onVipCharge(IPlayer mp, String info);
    }

    /**
     * 字幕文字出现时间变更
     */
    interface OnTimedTextChangedListener {
        void onTimedTextChanger(String text, long stat, long end);
    }

    interface OnLogoPositionListener {
        void onLogoPosition(int x, int y, int width, int height, boolean isShow);
    }

    void setAutoCharge(boolean auto);

}