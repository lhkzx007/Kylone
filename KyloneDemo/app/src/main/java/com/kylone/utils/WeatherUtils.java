package com.kylone.utils;

import com.kylone.player.R;

/**
 * Created by Zack on 2018/5/31
 */

public class WeatherUtils {
    public static final int NO_VALUE_FLAG = -999;//无
    public static final int SUNNY = 0;//晴
    public static final int CLOUDY = 1;//多云
    public static final int OVERCAST = 2;//阴
    public static final int FOGGY = 3;//雾
    public static final int SEVERE_STORM = 4;//飓风
    public static final int HEAVY_STORM = 5;//大暴风雨
    public static final int STORM = 6;//暴风雨
    public static final int THUNDERSHOWER = 7;//雷阵雨
    public static final int SHOWER = 8;//阵雨
    public static final int HEAVY_RAIN = 9;//大雨
    public static final int MODERATE_RAIN = 10;//中雨
    public static final int LIGHT_RAIN = 11;//小雨
    public static final int SLEET = 12;//雨夹雪
    public static final int SNOWSTORM = 13;//暴雪
    public static final int SNOW_SHOWER = 14;//阵雪
    public static final int HEAVY_SNOW = 15;//大雪
    public static final int MODERATE_SNOW = 16;//中雪
    public static final int LIGHT_SNOW = 17;//小雪
    public static final int STRONGSANDSTORM = 18;//强沙尘暴
    public static final int SANDSTORM = 19;//沙尘暴
    public static final int SAND = 20;//沙尘
    public static final int BLOWING_SAND = 21;//风沙
    public static final int ICE_RAIN = 22;//冻雨
    public static final int DUST = 23;//尘土
    public static final int HAZE = 24;//霾


//    40 scattered showers
//    41 heavy snow
//    42 scattered snow showers
//    43 heavy snow
//    45 thundershowers
//    46 snow showers
//    47 isolated thundershowers
//    3200 not available

    public static int getWeatherIcon(String code) {
        int type = NO_VALUE_FLAG;
        switch (Integer.parseInt(code)) {
            case 0:
            case 1:
            case 2:
                type = SEVERE_STORM;
                break;
            case 3:
            case 4:
                type = STORM;
                break;
            case 37:
            case 38:
            case 39:
            case 45:
                type = THUNDERSHOWER;
                break;
            case 5:
            case 6:
            case 7:
                type = SLEET;
                break;
            case 8:
            case 9:
                type = LIGHT_RAIN;
                break;
            case 10:
                type = ICE_RAIN;
                break;
            case 11:
            case 12:
            case 40:
            case 42:
                type = SHOWER;
                break;
            case 13:
                type = LIGHT_SNOW;
                break;
            case 14:
            case 46:
                type = SNOW_SHOWER;
                break;
            case 15:
                type = HEAVY_SNOW;
                break;
            case 16:
                type = MODERATE_SNOW;
                break;
            case 17:
                type = ICE_RAIN;
                break;
            case 18:
                type = SLEET;
                break;
            case 19:
                type = DUST;
                break;
            case 20:
                type = FOGGY;
                break;
            case 21:
            case 22:
                type = HAZE;
                break;
            case 23:
                type = BLOWING_SAND;
                break;
            case 24:
                type = OVERCAST;
                break;
            case 32:
            case 33:
            case 34:
                type = SUNNY;
                break;
            case 35:
                type = HEAVY_STORM;
                break;
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 44:
                type = CLOUDY;
                break;
            case 41:
            case 43:
                type = HEAVY_SNOW;
                break;
        }

        return getWeatherIcon(type);
    }

    /**
     * 获取天气图标
     *
     * @param type
     * @return
     */
    public static int getWeatherIcon(int type) {
        // 如果是晚上
        if (isNight(System.currentTimeMillis()))
            switch (type) {
                case SUNNY:
                    return R.mipmap.ic_nightsunny_big;
                case CLOUDY:
                    return R.mipmap.ic_nightcloudy_big;
                case HEAVY_RAIN:
                case LIGHT_RAIN:
                case MODERATE_RAIN:
                case SHOWER:
                case STORM:
                    return R.mipmap.ic_nightrain_big;
                case SNOWSTORM:
                case LIGHT_SNOW:
                case MODERATE_SNOW:
                case HEAVY_SNOW:
                case SNOW_SHOWER:
                    return R.mipmap.ic_nightsnow_big;
                default:
                    break;
            }
        // 如果是白天
        switch (type) {
            case SUNNY:
                return R.mipmap.ic_sunny_big;
            case CLOUDY:
                return R.mipmap.ic_cloudy_big;
            case OVERCAST:
                return R.mipmap.ic_overcast_big;
            case FOGGY:
                return R.mipmap.tornado_day_night;
            case SEVERE_STORM:
                return R.mipmap.hurricane_day_night;
            case HEAVY_STORM:
                return R.mipmap.ic_heavyrain_big;
            case STORM:
                return R.mipmap.ic_heavyrain_big;
            case THUNDERSHOWER:
                return R.mipmap.ic_thundeshower_big;
            case SHOWER:
                return R.mipmap.ic_shower_big;
            case HEAVY_RAIN:
                return R.mipmap.ic_heavyrain_big;
            case MODERATE_RAIN:
                return R.mipmap.ic_moderraterain_big;
            case LIGHT_RAIN:
                return R.mipmap.ic_lightrain_big;
            case SLEET:
                return R.mipmap.ic_sleet_big;
            case SNOWSTORM:
                return R.mipmap.ic_snow_big;
            case SNOW_SHOWER:
                return R.mipmap.ic_snow_big;
            case HEAVY_SNOW:
                return R.mipmap.ic_heavysnow_big;
            case MODERATE_SNOW:
                return R.mipmap.ic_snow_big;
            case LIGHT_SNOW:
                return R.mipmap.ic_snow_big;
            case STRONGSANDSTORM:
                return R.mipmap.ic_sandstorm_big;
            case SANDSTORM:
                return R.mipmap.ic_sandstorm_big;
            case SAND:
                return R.mipmap.ic_sandstorm_big;
            case BLOWING_SAND:
                return R.mipmap.ic_sandstorm_big;
            case ICE_RAIN:
                return R.mipmap.freezing_rain_day_night;
            case DUST:
                return R.mipmap.ic_dust_big;
            case HAZE:
                return R.mipmap.ic_haze_big;
            default:
                return R.mipmap.ic_default_big;
        }
    }

    private static boolean isNight(long l) {
        return false;
    }
}
