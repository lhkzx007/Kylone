package com.kylone.utils;

import android.text.TextUtils;

/**
 * Created by frank.z on 2018/4/14
 */

public class StringUtils {

    public static float parseFloat(String s , float def){
        float f = def;

        if (!TextUtils.isEmpty(s)){
            f = Float.parseFloat(s.trim());
        }
        return f;
    }

    public static int parseInt(String str, int def) {
        int i = def;
        if (!TextUtils.isEmpty(str)) {
            try {
                i = Integer.parseInt(str.trim());
            } catch (Throwable e) {
                //
            }
        }
        return i;
    }

    public static int parseInt(String strf) {
        return parseInt(strf, -1);
    }
}
