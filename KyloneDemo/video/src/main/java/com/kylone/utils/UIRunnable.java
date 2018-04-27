package com.kylone.utils;

/**
 * User: zhang.xing
 * Date: 2015-07-14
 * Time: 11:31
 */

public abstract class UIRunnable implements Runnable {
    Object[] mObjs;

    public UIRunnable(Object... objs) {
        mObjs = objs;
    }

    public Object[] getObjs() {
        return mObjs;
    }

    @Override
    public abstract void run();
}