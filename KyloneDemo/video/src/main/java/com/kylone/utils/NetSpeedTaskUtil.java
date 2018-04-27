package com.kylone.utils;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;

import com.kylone.base.ComponentContext;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by frank.z on 2017/6/13
 */

public class NetSpeedTaskUtil {
    private static final String SPEED = "SPEED";
    private static final String Speed_Changed_BROADCAST = "intent.action.Speed_Changed_BROADCAST";

    private final SpeedChangedReceiver speedReceiver;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    TimerTask task;

    public NetSpeedTaskUtil(SpeedChangedReceiver.CallBack callBack) {
        speedReceiver = new SpeedChangedReceiver(callBack);
        ComponentContext.getContext().registerReceiver(speedReceiver, new IntentFilter(Speed_Changed_BROADCAST));
    }


    private long getTotalRxBytes() {
        return (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()) / 1024;
//        return TrafficStats.getUidRxBytes(ComponentContext.getContext().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    public void startShowNetSpeed() {
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    showNetSpeed();
                }
            };
            new Timer().schedule(task, 1000, 1000); // 1s后启动任务，每2s执行一次
        }
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        try {
            ComponentContext.getContext().unregisterReceiver(speedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = (nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp);//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        Intent intent = new Intent(Speed_Changed_BROADCAST);
        intent.putExtra(SPEED, getNetSpeed(speed));
        ComponentContext.getContext().sendBroadcast(intent);
    }


    public String getNetSpeed(long mRateSpeed) {
        if (mRateSpeed >= 1000 * 1024) {
            return String.format(Locale.CHINA, "%.2fGB/S", (float) mRateSpeed / (1024f * 1000f));
        }
        if (mRateSpeed >= 1000) {
            return String.format(Locale.CHINA, "%.2fMB/S", (float) mRateSpeed / 1000f);
        }
        return mRateSpeed + "KB/S";
    }

}
