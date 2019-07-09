package com.kylone;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.kylone.base.ComponentContext;
import com.kylone.base.Density;
import com.kylone.utils.ApiUtils;

import java.lang.ref.WeakReference;

/**
 * Created by zack
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ComponentContext.setContext(this);
        Density.INSTANCE.setDensity(this,1280);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (ApiUtils.currentTopActivity != null) {
                    ApiUtils.currentTopActivity.clear();
                }
                ApiUtils.currentTopActivity = new WeakReference<Activity>(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
//        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
//
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                LogUtil.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//            }
//        };
//        //x5内核初始化接口
////        QbSdk.initX5Environment(getApplicationContext(),  cb);
//        if (!QbSdk.isTbsCoreInited()) {
//            QbSdk.preInit(getApplicationContext(),  cb);
//            QbSdk.initX5Environment(getApplicationContext(), cb);
//        }
    }
}
