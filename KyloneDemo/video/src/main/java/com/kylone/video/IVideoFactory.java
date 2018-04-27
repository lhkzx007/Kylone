package com.kylone.video;

import android.content.Context;
import android.text.TextUtils;

import com.kylone.player.NativeVideo;
import com.kylone.player.SystemVideo;
import com.kylone.utils.LogUtil;

public class IVideoFactory {
    public static String VIDEO_SYSTEM = "system";
    public static String VIDEO_NATIVE = "native";

    /**
     * 通过标志创建播放器实例
     *
     * @param context
     * @param target
     * @return
     */
    public static IPlayer createIVideo(Context context, String target) {
        LogUtil.d("IVideoFactory", "create video target  " + target);
        IVideoView video = null;

        if (TextUtils.equals(VIDEO_NATIVE, target)) {
            video = new NativeVideo(context);
        } else {
            video = new SystemVideo(context);
        }
        return video;
    }
}
