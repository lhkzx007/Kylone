package com.kylone.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Zack on 2018/4/15
 */

public class ExtraUitls {
    public static String stringForTime(long timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        int totalSeconds = (int) (timeMs / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return new Formatter(mFormatBuilder, Locale.getDefault()).format("%02d:%02d:%02d", hours,
                    minutes, seconds).toString();
        } else {
            return new Formatter(mFormatBuilder, Locale.getDefault()).format("%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    public static void setVolume(int next, Context ctx) {
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, AudioManager.FLAG_PLAY_SOUND);
    }

    public static void changeVolume(int delta, Context ctx) {
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int next = current + delta;
        if (next > max) {
            next = max;
        } else if (next < 0) {
            next = 0;
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, next, AudioManager.FLAG_PLAY_SOUND);
    }


    // 改变亮度
    public static void setScreenLightness(Context ctx, int value) {
        if (isAutoBrightness(ctx)) {
            stopAutoBrightness(ctx);
        }
        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                Math.min(Math.max(value, 1), 255));
        if (ctx instanceof Activity) {
            try {
                WindowManager.LayoutParams lp = ((Activity) ctx).getWindow().getAttributes();
                lp.screenBrightness = (Math.min(Math.max(value, 30), 255)) / 255f;
                ((Activity) ctx).getWindow().setAttributes(lp);
            } catch (Exception e) {
            }
        }
    }


    //判断是否自动调整亮度
    public static boolean isAutoBrightness(Context act) {
        boolean automicBrightness = false;
        ContentResolver aContentResolver = act.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
        }
        return automicBrightness;
    }

    // 获取亮度
    public static int getScreenLightness(Context ctx) {
        return Settings.System.getInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
    }

    // 停止自动亮度调节
    public static void stopAutoBrightness(Context ctx) {
        Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }
}
