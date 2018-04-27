package com.kylone.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by frank.z on 2018/4/14
 */
public class HandlerUtils {
    private static HandlerThread handerThread = null;
    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    /**
     * 在UI线程运行
     *
     * @param runnable
     */
    public static void runUITask(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            uiHandler.post(runnable);
        }
    }

    /**
     * 在UI线程延时运行
     *
     * @param runnable
     */
    public static void runUITask(Runnable runnable, long delayTime) {
        uiHandler.postDelayed(runnable, delayTime);
    }

    public static void removeUITask(Runnable runnable) {
        if (null != runnable) {
            uiHandler.removeCallbacks(runnable);
        }
    }

    /**
     * 避免两个关联主线程的Handler,执行post的时候有冲突而新增的方法
     *
     * @param runnable
     * @param delayTime
     */
    public static void postDelayed(final Runnable runnable, final long delayTime) {
        if (delayTime <= 0) {
            runUITask(runnable);
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runUITask(runnable);
                }
            }.start();
        }
    }


}
