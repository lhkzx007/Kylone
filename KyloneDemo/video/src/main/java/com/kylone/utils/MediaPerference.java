package com.kylone.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kylone.base.ComponentContext;
import com.kylone.video.IPlayer;

/**
 * Created by Zack on 2018/4/15
 */

public class MediaPerference {
    public static final String SETTING_PREFERENCES = "setting";

    private static final String LIVE_SCALE = "live_scale";// 直播画面比例
    private static final String LIVE_QUICK = "live_quick";// 快速进入频道
    private static final String LIVE_DECODER = "live_decoder";// 直播默认解码方式
    private static final String LIVE_TALK = "live_talk";// 直播弹幕开关
    private static final String VOD_SCALE = "vod_scale";// vod画面比例
    public static final String VOD_DEFINITION = "vod_quality";// vod清晰度
    private static final String VOD_DEFINITION_LIVE = "vod_quality_live";// 点播清晰度
    private static final String VOD_SKIP = "vod_isSkip";

    public static final String PLAYER_DECODER = "player_decoder";// 播放器解码方式
    public static final String VOD_TRACE = "no_trace";// 无痕浏览

    private static final String NAME = "media";


    /**
     * 获取单个应用的sharedpreferences 和包名相关 这个可以保存一些非设置相关的存储信息
     *
     * @param ctx
     * @return
     */
    public static SharedPreferences getSpForName(String name) {
        return ComponentContext.getContext().getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
    }

    /**
     * 获取默认的 获取设置文件的偏好文件
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSp() {
        return getSpForName(SETTING_PREFERENCES);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean(key, value);
        et.apply();
    }

    public static void putString(String key, String value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.apply();
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean def) {
        return getSp().getBoolean(key, def);
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String def) {
        return getSp().getString(key, def);
    }

    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSp().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int def) {
        return getSp().getInt(key, def);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSp().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, int def) {
        return getSp().getLong(key, def);
    }

    public static long getLong(String key) {
        return getLong(key, 0);
    }


    public static void setPlayerDecoder(int decodeType) {
        putInt(PLAYER_DECODER, decodeType);
    }
    public static int getPlayerDecoder() {
        return getInt(PLAYER_DECODER, IPlayer.INTELLIGENT_DECODE); //IPlayer.INTELLIGENT_DECODE
    }

    /*-----------------------------画面大小数据的存取-----------------------------*/
    public static void putVodScale(int scale) {
        putInt(VOD_SCALE, scale);
    }

    public static int getVodScale() {
//        return PreferenceUtil.getSp().getInt(VOD_SCALE, IPlayer.SURFACE_BEST_FIT);
        return getInt(VOD_SCALE, IPlayer.SURFACE_BEST_FIT);
    }

    /*-----------------------------点播清晰度数据的存取-----------------------------*/
    public static void putVodDefinition(int definition) {
        putInt(VOD_DEFINITION, definition);

    }

    public static int getVodDefinition() {
        return getInt(VOD_DEFINITION, IPlayer.DEFINITION_FULLHD);
//        return PreferenceUtil.getInt(PreferenceUtil.DEFINITION_SET, 1); //IPlayer.DEFINITION_SD
    }

}
