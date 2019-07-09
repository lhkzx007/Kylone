package com.kylone.base

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log

/**
 *
 * 屏幕适配方案
 *
 * 通过修改activity.resources.displayMetrics 的 density值来进行适配
 *
 *
 * Created by zack on 2018/8/8
 */
object Density {

    private var WIDTH: Float = 0f
    private var appDensity: Float = 0f
    private var appScaledDensity: Float = 0f
    private var targetDensity: Float = 0f

    fun setDensity(app: Application, width: Float) {
        val appDisplayMetrics = app.resources.displayMetrics
        val mWidth = appDisplayMetrics.widthPixels         // 屏幕宽度（像素）
        val mHeight = appDisplayMetrics.heightPixels       // 屏幕高度（像素）
        val mDensity = appDisplayMetrics.density      // 屏幕密度（像素）
        val mDensityDpi = appDisplayMetrics.densityDpi      // 屏幕密度dpi（像素）

        Log.i("Density"," 屏幕宽高 :[ $mWidth / $mHeight ]")
        Log.i("Density"," 屏幕密度 :[ $mDensity / $mDensityDpi ]")

        targetDensity = mWidth / width
        WIDTH = width

        registerActivityLifecycleCallbacks(app)
        if (appDensity == 0f) {
            appDensity = mDensity
            appScaledDensity = appDisplayMetrics.scaledDensity

            app.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration?) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaledDensity = app.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {
                }
            })

        }
    }

    /**
     *  dp转换成px
     */
    fun dp2px(dp: Float): Float {
        return dp * targetDensity
    }

    /**
     *  dp转换成px
     */
    fun dp2px(dp: Int): Int {
        return (dp * targetDensity).toInt()
    }

    /**
     * 进行像素转换计算
     */
    private fun setDefault(activity: Activity?) {

        val targetScaledDensity = targetDensity * (appScaledDensity / appDensity)
        val targetDensityDip = 160f * targetDensity

        val activityDM = activity!!.resources.displayMetrics
        activityDM.density = targetDensity
        activityDM.scaledDensity = targetScaledDensity
        activityDM.densityDpi = targetDensityDip.toInt()

        Log.i("Density"," 当前密度 :[ ${activityDM.density} / ${activityDM.densityDpi} ]")
    }

    /**
     * 注册activity生命周期监听
     */
    private fun registerActivityLifecycleCallbacks(app: Application) {
        app.registerActivityLifecycleCallbacks(ALC)
    }


    private object ALC : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            setDefault(activity)
        }

    }
//      JAVA代码
//    private static float appDensity;
//    private static float appScaledDensity;
//    private static DisplayMetrics appDisplayMetrics;
//    /**
//     * 用来参照的的width
//     */
//    private static float WIDTH;
//
//    public static void setDensity(@NonNull final Application application, float width) {
//        appDisplayMetrics = application.getResources().getDisplayMetrics();
//        WIDTH = width;
//        registerActivityLifecycleCallbacks(application);
//
//        if (appDensity == 0) {
//            //初始化的时候赋值
//            appDensity = appDisplayMetrics.density;
//            appScaledDensity = appDisplayMetrics.scaledDensity;
//
//            //添加字体变化的监听
//            application.registerComponentCallbacks(new ComponentCallbacks() {
//                @Override
//                public void onConfigurationChanged(Configuration newConfig) {
//                    //字体改变后,将appScaledDensity重新赋值
//                    if (newConfig != null && newConfig.fontScale > 0) {
//                        appScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
//                    }
//                }
//
//                @Override
//                public void onLowMemory() {
//                }
//            });
//        }
//    }
//
//
//    private static void setDefault(Activity activity) {
//        setAppOrientation(activity);
//    }
//
//    private static void setAppOrientation(@Nullable Activity activity) {
//
//        float targetDensity = 0;
//        try {
//            targetDensity = appDisplayMetrics.widthPixels / WIDTH;
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        float targetScaledDensity = targetDensity * (appScaledDensity / appDensity);
//        int targetDensityDpi = (int) (160 * targetDensity);
//
//        /**
//         *
//         * 最后在这里将修改过后的值赋给系统参数
//         *
//         * 只修改Activity的density值
//         */
//
//        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
//        activityDisplayMetrics.density = targetDensity;
//        activityDisplayMetrics.scaledDensity = targetScaledDensity;
//        activityDisplayMetrics.densityDpi = targetDensityDpi;
//    }
//
//
//    private static void registerActivityLifecycleCallbacks(Application application) {
//        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                setDefault(activity);
//            }
//            @Override
//            public void onActivityStarted(Activity activity) {
//            }
//            @Override
//            public void onActivityResumed(Activity activity) {
//            }
//            @Override
//            public void onActivityPaused(Activity activity) {
//            }
//            @Override
//            public void onActivityStopped(Activity activity) {
//            }
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//            }
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//
//            }
//        });
}
