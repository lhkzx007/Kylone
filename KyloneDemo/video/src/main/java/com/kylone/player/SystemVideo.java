package com.kylone.player;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.kylone.utils.LogUtil;
import com.kylone.utils.ThreadManager;
import com.kylone.utils.UIRunnable;
import com.kylone.video.BaseVideoView;
import com.kylone.video.IPlayer;
import com.kylone.video.VideoUrl;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by frank.z on 2018/4/14
 */

public class SystemVideo extends BaseVideoView {
    private static final String TAG = "SystemVideo";
    public SystemVideo(Context context) {
        super(context);
    }

    @Override
    public void setVideoPath(String path, Map<String, String> headers) {
        super.setVideoPath(path, headers);
        if (!TextUtils.isEmpty(path)) {
            ThreadManager.execute(new UIRunnable(path, headers) {
                @Override
                public void run() {
                    String path = (String) getObjs()[0];
                    parseDefinition(path);
                }
            });
        }
    }

    /**
     * 获取当前播放平台中的清晰度,并根据清晰度返回相应的播放连接
     *
     * @param site 平台
     */
    private void parseDefinition(String site) {
        LogUtil.i(TAG, "开始解析清晰度 : " + site);
        if (TextUtils.isEmpty(site)) {
            LogUtil.d(TAG, "源地址是空的!!");
            return;
        }
        // 获取平台中的清晰度
        SparseArray<VideoUrl> map;
        isClose = false;
        int indexL = 0;
        do {
            if (indexL++ > 0) {
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            map = ModelHelper.getVideoUrlsFromNet(site);
            map = new SparseArray<VideoUrl>();
            VideoUrl l = new VideoUrl();
            l.quality = 3;
            l.url = site;
            map.put(3,l);
            LogUtil.i(TAG, "QualityList : " + map);
        } while ((indexL < 5) && (map == null || map.size() == 0) && !isClose);
        if (map == null || map.size() == 0 || isClose) {
            if (!isClose && mOnErrorListener != null) {
                //如果不是关闭播放,并且解析
                mOnErrorListener.onError(this, MEDIA_ERROR_DEFINITION_EMPTY, 0);
            }
            return;
        }


        //初始化当前清晰度
        if (currentQuality == IPlayer.DEFINITION_AUTO) {
            currentQuality = IPlayer.DEFINITION_HD;
        }

        VideoUrl videoUrl = map.get(currentQuality);


        //对清晰度排序
        if (videoUrl == null) {// 如果默认清晰度的源为空,依次获取清晰度的源
            int[] quality = IPlayer.DEFINITION_ORDER;
            // 根据设置对清晰度选择进行判断
            for (int i = 0; i < quality.length; i++) {
                if (quality[i] == currentQuality) {
                    int[] temp1 = Arrays.copyOfRange(quality, i, quality.length);
                    int[] temp2 = Arrays.copyOfRange(quality, 0, i);
                    System.arraycopy(temp1, 0, quality, 0, temp1.length);
                    int index = temp1.length;
                    for (int k = temp2.length - 1; k >= 0; k--) {
                        quality[index++] = temp2[k];
                    }
                    break;
                }
            }
            for (int i : quality) {
                videoUrl = map.get(i);
                if (videoUrl != null) {
                    currentQuality = i;
                    break;
                }
            }
            if (videoUrl == null) {
                videoUrl = map.valueAt(0);
            }
        }

        //保存清晰度
        mQualityList = map;
        mCurrentQuality = videoUrl;
        //准备开始播放
        setVideoPathByUrl(mCurrentQuality.url, null);
    }
}
