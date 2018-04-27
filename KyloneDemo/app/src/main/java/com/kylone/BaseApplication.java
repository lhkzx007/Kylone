package com.kylone;

import android.app.Application;

import com.kylone.base.ComponentContext;

/**
 * Created by zack
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ComponentContext.setContext(this);
    }
}
