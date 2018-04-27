package com.kylone.utils;

import com.kylone.video.IPlayer;

/**
 * Created by Zack on 2018/4/15
 */

public class MediaResourceHelper {
    public static String getScaleName(int scale) {
        String name = null;
        switch (scale) {
            case IPlayer.SURFACE_16_9:
                name = "16:9";
                break;
            case IPlayer.SURFACE_4_3:
                name = "4:3";
                break;
            case IPlayer.SURFACE_BEST_FIT:
                name = "原始比例";
                break;
            case IPlayer.SURFACE_FILL:
                name = "全屏";
                break;
            default:
                name = "原始比例";
                break;
        }
        return name;
    }

    public static String getQualityName(int quality) {
        String name = null;
        switch (quality) {
            case IPlayer.DEFINITION_1080P:
                name = "原画";
                break;
            case IPlayer.DEFINITION_BLUE:
                name = "蓝光";
                break;
            case IPlayer.DEFINITION_FULLHD:
                name = "超清";
                break;
            case IPlayer.DEFINITION_HD:
                name = "高清";
                break;
            case IPlayer.DEFINITION_SD:
                name = "标清";
                break;
            case IPlayer.DEFINITION_LD:
                name = "流畅";
                break;
            case IPlayer.DEFINITION_4K:
                name = "4K";
                break;
        }
        return name;
    }

    public static String getSurfaceSize(int surfaceSize) {
        String surface = "原始比例";
        switch (surfaceSize) {
            case IPlayer.SURFACE_BEST_FIT:
                surface = "原始比例";
                break;
            case IPlayer.SURFACE_FILL:
                surface = "全屏";
                break;
            default:
                break;
        }
        return surface;
    }

    public static String getDecode(int decodeType) {
        String decode = "智能解码";
        switch (decodeType) {
            case IPlayer.INTELLIGENT_DECODE:
                decode = "智能解码";
                break;
            case IPlayer.HARD_DECODE:
                decode = "硬解";
                break;
            case IPlayer.SOFT_DECODE:
                decode = "软解";
                break;
            default:
                break;
        }
        return decode;
    }

    public static int getDecodeType(String decode) {
        if ("智能解码".equals(decode)) {
            return IPlayer.INTELLIGENT_DECODE;
        } else if ("硬解".equals(decode)) {
            return IPlayer.HARD_DECODE;
        } else if ("软解".equals(decode)) {
            return IPlayer.SOFT_DECODE;
        }
        return IPlayer.INTELLIGENT_DECODE;
    }
}
