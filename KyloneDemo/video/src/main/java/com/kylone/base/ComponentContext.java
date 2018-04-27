package com.kylone.base;

import android.content.Context;

/**
 * @author zack
 * @ClassName: ComponentContext
 * @Description: TODO(配置一个Context的引用，用来避免传递Activity的Context，建议在application中配置改方法)
 */
public class ComponentContext {

    private static Context mContext;

    public static boolean isAppStart = false;

    public static void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }


    public static boolean isDebug = true;
}
